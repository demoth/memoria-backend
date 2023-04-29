package org.dnj.memoria

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Initializer(val userRepository: UserRepository): CommandLineRunner {
    override fun run(vararg args: String?) {
        userRepository.save(User("demoth", "whatever"))
        println("Saved to database!")
        
        userRepository.findAll().forEach { println("Found user: $it") }
    }
}