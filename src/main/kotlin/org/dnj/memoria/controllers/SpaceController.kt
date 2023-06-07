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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date
import java.util.Optional
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
        return spaceRepository.findAll().map { it.toDto() }
    }

    @PostMapping
    fun createSpace(
        @RequestHeader("Authentication") token: String,
        @RequestBody request: SpaceDto
    ): ResponseEntity<SpaceDto> {
        val user = authService.validateToken(token)
        if (request.id == null || spaceRepository.findById(request.id).isEmpty) {
            val space = spaceRepository.save(Space(request.name, request.description, Date(), null, user))
            user.spaces.add(space)
            userRepository.save(user)
            return ResponseEntity.ok(space.toDto())
        }
        logger.warn("Wrong request to 'createSpace': $request")
        return ResponseEntity.badRequest().build()
    }
    
    @OptIn(ExperimentalStdlibApi::class)
    @PostMapping("/invite")
    fun joinSpace(
        @RequestHeader("Authentication") token: String,
        @RequestParam("spaceId") spaceId: String,
        @RequestParam("inviteeId") inviteeId: String,
    ): ResponseEntity<String> {
        
        val owner = authService.validateToken(token)
        
        val space = spaceRepository.findById(spaceId).getOrNull()
            ?: throw MemoriaException("Space $spaceId not found", HttpStatus.NOT_FOUND)

        if (owner.id != space.owner?.id)
            throw MemoriaException("You are not an owner of this space", HttpStatus.FORBIDDEN)

        val invitee = userRepository.findById(inviteeId).getOrNull() 
            ?: throw MemoriaException("User $inviteeId not found", HttpStatus.NOT_FOUND)
        
        invitee.spaces.add(space)
        userRepository.save(invitee)
        return ResponseEntity.ok("Ok")
    }
}
