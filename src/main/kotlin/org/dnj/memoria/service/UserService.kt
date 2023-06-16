package org.dnj.memoria.service

import org.dnj.memoria.MemoriaException
import org.dnj.memoria.SpaceRepository
import org.dnj.memoria.UserRepository
import org.dnj.memoria.controllers.ChangePasswordRequest
import org.dnj.memoria.model.SignupRequest
import org.dnj.memoria.model.Space
import org.dnj.memoria.model.User
import org.dnj.memoria.model.UserDto
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val spaceRepository: SpaceRepository
) {
    fun changePassword(user: User, request: ChangePasswordRequest): UserDto {
        if (user.name != request.username ||
            user.password != request.currentPassword) {
            throw MemoriaException("Password doesn't match", HttpStatus.BAD_REQUEST)
        }

        user.password = request.newPassword
        
        return userRepository.save(user).toDto()
    }
    
    fun signup(signupRequest: SignupRequest): UserDto {
        if (signupRequest.promo != System.getenv("MEMORIA_PROMO"))
            throw MemoriaException("Don't have a promo-code? - Reach the creators", HttpStatus.BAD_REQUEST)

        if (userRepository.findByName(signupRequest.username).isNotEmpty())
            throw MemoriaException("User already exists", HttpStatus.BAD_REQUEST)

        val personalSpace = spaceRepository.save(Space("${signupRequest.username}'s space"))
        val newUser = User(
            signupRequest.username,
            signupRequest.password,
            spaces = mutableListOf(personalSpace)
        )
        return userRepository.save(newUser).toDto()
    }
}
