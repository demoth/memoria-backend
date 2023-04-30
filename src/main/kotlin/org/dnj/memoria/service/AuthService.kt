package org.dnj.memoria.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.dnj.memoria.LoginResponse
import org.dnj.memoria.MemoriaException
import org.dnj.memoria.User
import org.dnj.memoria.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(@Autowired private val userRepository: UserRepository) {
    private val CLAIM_NAME = "loggedInAs"

    private val ISSUER = "memoria"

    private val JWT_SECRET = System.getenv("JWT_SECRET") ?: UUID.randomUUID().toString()

    private val VERIFIER: JWTVerifier = JWT.require(Algorithm.HMAC256(JWT_SECRET)).withIssuer(ISSUER).build()

    /**
     * validates jwt token and returns user associated with this token
     */
    fun validateToken(authHeader: String): User {
        val (type, token) = authHeader.split(" ")
        if (type != "Bearer")
            throw MemoriaException("Authentication $type is not supported", HttpStatus.BAD_REQUEST)
        val userName: String = try {
            VERIFIER.verify(token).getClaim(CLAIM_NAME).asString()
        } catch (e: Exception) {
            throw MemoriaException("Wrong login or password", HttpStatus.FORBIDDEN)
        }

        return userRepository.findByName(userName).firstOrNull()
        // jwt is correct, but there is no such user
            ?: throw MemoriaException("Wrong login or password", HttpStatus.FORBIDDEN)

    }

    /**
     * @return token after successful identification and authentication
     */
    fun loginUser(name: String, password: String): LoginResponse {
        val user = userRepository.findByName(name).firstOrNull() 
            ?: throw MemoriaException("Wrong login or password", HttpStatus.FORBIDDEN)

        if (user.password != password) {
            throw MemoriaException("Wrong login or password", HttpStatus.FORBIDDEN)
        }

        try {
            return LoginResponse(name,
                JWT.create()
                    .withIssuer(ISSUER)
                    .withClaim(CLAIM_NAME, user.name)
                    .sign(Algorithm.HMAC256(JWT_SECRET)))
        } catch (e: Exception) {
            throw MemoriaException("Could not authenticate", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
