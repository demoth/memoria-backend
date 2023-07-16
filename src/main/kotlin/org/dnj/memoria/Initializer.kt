package org.dnj.memoria

import org.dnj.memoria.model.Space
import org.dnj.memoria.model.SpaceRef
import org.dnj.memoria.model.User
import org.dnj.memoria.model.UserDto
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
        private val logger = LoggerFactory.getLogger(Initializer::class.java)!!
    }
    
    override fun run(vararg args: String?) {

        // create default space if no space exists
        //createDefaultSpace()

        migrate1_2()

        logger.info("Initialized!")
    }

    private fun createDefaultSpace() {
        if (!spaceRepository.findAll().iterator().hasNext()) {
            logger.info("Initialized default space")
            val defaultSpace = spaceRepository.save(Space("D&J"))
            userRepository.findAll().forEach {
                it.spaces?.add(defaultSpace)
                userRepository.save(it)
            }

            itemRepository.findAll().forEach {
                it.space = defaultSpace
                itemRepository.save(it)
            }
        }
    }

    // migrate owner to owner ref
    private fun migrate1_2() {
        // space
        spaceRepository.findByVersion("1").forEach { space ->
            space.owner?.let { owner ->
                space.ownerRef = owner.toDto()
                space.owner = null
            }

            space.version = "2"
            spaceRepository.save(space)
        }

        // users
        userRepository.findByVersion("1").forEach { user ->
            user.spaces?.let { spaces ->
                user.spaceRefs = spaces.map { it.toRef() }.toMutableSet()
                user.spaces = null
            }
            user.version = "2"
            userRepository.save(user)
        }

        // items
        itemRepository.findByVersion("1").forEach { item ->

            item.assignee?.let {
                item.assigneeRef = it.toDto()
                item.assignee = null
            }
            item.creator?.let {
                item.creatorRef = it.toDto()
                item.creator = null
            }

            item.parent?.let {
                item.parentRef = it.toRef()
                item.parent = null
            }

            item.version = "2"
            itemRepository.save(item)
        }
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