package org.dnj.memoria.controllers

import org.dnj.memoria.MemoriaException
import org.dnj.memoria.SpaceRepository
import org.dnj.memoria.model.UserDto
import org.dnj.memoria.UserRepository
import org.dnj.memoria.model.SignupRequest
import org.dnj.memoria.model.Space
import org.dnj.memoria.model.User
import org.dnj.memoria.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ChangePasswordRequest(
    val username: String,
    val currentPassword: String,
    val newPassword: String
)

@RestController
@RequestMapping("/user")
@CrossOrigin
class UserController(
    private val userRepository: UserRepository,
    private val authService: AuthService,
    private val spaceRepository: SpaceRepository
) {
    
    @PostMapping("/change-password")
    fun changePassword(
        @RequestHeader("Authentication") token: String,
        @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<UserDto> {
        val user = authService.validateToken(token)
        if (user.name != request.username ||
            user.password != request.currentPassword) {
            return ResponseEntity.badRequest().build()
        }

        user.password = request.newPassword
        return ResponseEntity.ok(userRepository.save(user).toDto())
    }
    
    @PostMapping("/signup")
    fun signup(
        @RequestBody signupRequest: SignupRequest
    ): ResponseEntity<UserDto> {
        if (signupRequest.promo != System.getenv("MEMORIA_PROMO"))
            throw MemoriaException("Don't have a promo-code? - Reach the creators", HttpStatus.BAD_REQUEST)

        if (userRepository.findByName(signupRequest.username).isNotEmpty())
            throw MemoriaException("User already exists", HttpStatus.BAD_REQUEST)

        val personalSpace = spaceRepository.save(Space("${signupRequest.username}'s personal space"))
        val user = userRepository.save(User(signupRequest.username, signupRequest.password, spaces = mutableListOf(personalSpace)))
        return ResponseEntity.ok(user.toDto())
    }
    
    @GetMapping("/all")
    fun getUsers() : ResponseEntity<Collection<UserDto>> {
        return ResponseEntity.ok(userRepository.findAll().map { it.toDto() })
    }
    
}
