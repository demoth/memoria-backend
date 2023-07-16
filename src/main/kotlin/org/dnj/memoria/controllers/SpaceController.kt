package org.dnj.memoria.controllers

import org.dnj.memoria.MemoriaException
import org.dnj.memoria.SpaceRepository
import org.dnj.memoria.UserRepository
import org.dnj.memoria.model.Space
import org.dnj.memoria.model.SpaceDto
import org.dnj.memoria.service.AuthService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/space")
@CrossOrigin
class SpaceController(
    private val spaceRepository: SpaceRepository,
    private val authService: AuthService,
    private val userRepository: UserRepository
) {
    
    private val logger = LoggerFactory.getLogger(SpaceController::class.java)!!

    @GetMapping("/all")
    fun allSpaces(
        @RequestHeader("Authentication") token: String
    ): Collection<SpaceDto> {
        authService.validateToken(token)
        return spaceRepository.findAll().map { it.toDto() }
    }
    
    @GetMapping("/{spaceId}")
    fun getSpace(
        @RequestHeader("Authentication") token: String,
        @PathVariable("spaceId") spaceId: String
    ): SpaceDto {
        val user = authService.validateToken(token)

        val space = spaceRepository.findById(spaceId).getOrNull()
            ?: throw MemoriaException("Space $spaceId not found", HttpStatus.NOT_FOUND)

        if (user.spaceRefs.none { it.id == space.id })
            throw MemoriaException("You are not in this space", HttpStatus.FORBIDDEN)
        
        // get all users that are in this space
        // fixme: ugly terrible disaster: find all then filter by space ids
        val participants = userRepository.findAll().filter { u -> u.spaceRefs.any { s -> s.id == space.id } }.map { it.toDto() }
        return SpaceDto(space.id, space.name, space.description, participants, space.ownerRef)

    }

    @PostMapping
    fun createSpace(
        @RequestHeader("Authentication") token: String,
        @RequestBody request: SpaceDto
    ): ResponseEntity<SpaceDto> {
        val user = authService.validateToken(token)
        if (request.id == null || spaceRepository.findById(request.id).isEmpty) {
            val space = spaceRepository.save(Space(request.name, request.description, Date(), null, user))
            user.spaceRefs.add(space.toRef())
            userRepository.save(user)
            logger.info("Created space $space")
            return ResponseEntity.ok(space.toDto())
        }
        logger.warn("Wrong request to 'createSpace': $request")
        return ResponseEntity.badRequest().build()
    }
    
    @PostMapping("/invite")
    fun joinSpace(
        @RequestHeader("Authentication") token: String,
        @RequestParam("spaceId") spaceId: String,
        @RequestParam("inviteeId") inviteeId: String,
    ): ResponseEntity<String> {
        
        val inviter = authService.validateToken(token)
        
        val space = spaceRepository.findById(spaceId).getOrNull()
            ?: throw MemoriaException("Space $spaceId not found", HttpStatus.NOT_FOUND)

        // anyone already in the space can invite
        if (inviter.spaceRefs.none { s -> s.id == space.id })
            throw MemoriaException("You are not in this space, can not invite", HttpStatus.FORBIDDEN)

        val invitee = userRepository.findById(inviteeId).getOrNull() 
            ?: throw MemoriaException("User $inviteeId not found", HttpStatus.NOT_FOUND)
        
        invitee.spaceRefs.add(space.toRef())
        userRepository.save(invitee)
        logger.info("User ${invitee.toDto()} joined space ${space.toRef()}")
        return ResponseEntity.ok("Ok")
    }
}
