package org.dnj.memoria.controllers

import org.dnj.memoria.model.UserDto
import org.dnj.memoria.UserRepository
import org.dnj.memoria.service.AuthService
import org.springframework.http.ResponseEntity
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
class UserController(
    private val userRepository: UserRepository,
    private val authService: AuthService
) {
    
    @PostMapping
    fun update(
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
}
