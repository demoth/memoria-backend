package org.dnj.memoria

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Initializer(
    val userRepository: UserRepository,
    val itemRepository: ItemRepository,
    val spaceRepository: SpaceRepository
): CommandLineRunner {
    
    companion object {
        val logger = LoggerFactory.getLogger(Initializer::class.java)
    }
    
    override fun run(vararg args: String?) {

        // create default space if no space exists
        if (!spaceRepository.findAll().iterator().hasNext()) {
            logger.info("Initialized default space")
            val defaultSpace = spaceRepository.save(Space("D&J"))
            userRepository.findAll().forEach { 
                it.spaces.add(defaultSpace)
                userRepository.save(it)
            }
            
            itemRepository.findAll().forEach { 
                it.space = defaultSpace
                itemRepository.save(it)
            }
        }
        
        println("Initialized!")
    }

//    private fun getOrCreateUser(userName: String): User {
//        val existingUser = userRepository.findByName(userName)
//        return if (existingUser.isEmpty()) {
//            val password = System.getenv("PASSWORD_USER_${userName.uppercase()}") 
//                ?: throw IllegalStateException("No password is provided for $userName")
//            userRepository.save(User(userName, password))
//        } else existingUser.first()
//    }
}