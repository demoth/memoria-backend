package org.dnj.memoria

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.lang.IllegalStateException

@Component
class Initializer(
    val userRepository: UserRepository,
    val itemRepository: ItemRepository
): CommandLineRunner {
    override fun run(vararg args: String?) {
        getOrCreateUser("demoth")
        getOrCreateUser("denolia")
    }

    private fun getOrCreateUser(userName: String): User {
        val existingUser = userRepository.findByName(userName)
        return if (existingUser.isEmpty()) {
            val password = System.getenv("PASSWORD_USER_${userName.uppercase()}") 
                ?: throw IllegalStateException("No password is provided for $userName")
            userRepository.save(User(userName, password))
        } else existingUser.first()
    }
}