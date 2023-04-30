package org.dnj.memoria.controllers

import org.dnj.memoria.LoginRequest
import org.dnj.memoria.LoginResponse
import org.dnj.memoria.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class LoginController(@Autowired val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        return authService.loginUser(request.username, request.password)
    }

}
