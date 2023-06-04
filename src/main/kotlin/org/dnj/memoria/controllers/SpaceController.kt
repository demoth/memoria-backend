package org.dnj.memoria.controllers

import org.dnj.memoria.SpaceRepository
import org.dnj.memoria.UserRepository
import org.dnj.memoria.model.Space
import org.dnj.memoria.model.SpaceDto
import org.dnj.memoria.service.AuthService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping("/space")
@CrossOrigin
class SpaceController(
    private val spaceRepository: SpaceRepository,
    private val authService: AuthService,
    private val userRepository: UserRepository
) {
    @GetMapping("/all")
    fun allItems(
        @RequestHeader("Authentication") token: String
    ): Collection<SpaceDto> {
        return spaceRepository.findAll().map { it.toDto() }
    }

    @PostMapping
    fun updateSpace(
        @RequestHeader("Authentication") token: String,
        @RequestBody request: SpaceDto
    ): SpaceDto {
        val user = authService.validateToken(token)
        val space = spaceRepository.save(Space(request.name, request.description, Date()))
        user.spaces.add(space)
        userRepository.save(user)
        return space.toDto()
    }
}
