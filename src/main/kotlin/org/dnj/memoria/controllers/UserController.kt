package org.dnj.memoria.controllers

import org.dnj.memoria.UserRepository
import org.dnj.memoria.model.SignupRequest
import org.dnj.memoria.model.UserDto
import org.dnj.memoria.service.AuthService
import org.dnj.memoria.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    private val userService: UserService
) {
    
    @PostMapping("/change-password")
    fun changePassword(
        @RequestHeader("Authentication") token: String,
        @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<UserDto> {
        val user = authService.validateToken(token)
        return ResponseEntity.ok(userService.changePassword(user, request))
    }
    
    @PostMapping("/signup")
    fun signup(
        @RequestBody signupRequest: SignupRequest
    ): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.signup(signupRequest))
    }
    
    @GetMapping("/all")
    fun getUsers() : ResponseEntity<Collection<UserDto>> {
        return ResponseEntity.ok(userRepository.findAll().map { it.toDto() })
    }
    
}
