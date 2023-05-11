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
        val demoth = getOrCreateUser("demoth")
        val denolia = getOrCreateUser("denolia")

        itemRepository.save(Item("Task", "Позвонить в страховую", Status.Backlog, Priority.High, demoth, demoth, null, "Не знаю ещё зачем"))
        itemRepository.save(Item("Task", "Пополнить баланс", Status.Todo, Priority.Medium, demoth))
        itemRepository.save(Item("Task", "Помыть кухню", Status.Backlog, Priority.Medium, denolia))
        itemRepository.save(Item("Task", "Поиграть в PoE", Status.Backlog, Priority.High, denolia))
        itemRepository.save(Item("Epic", "Написать свой ноушен", Status.InProgress, Priority.High, demoth, denolia))
        itemRepository.save(Item("Task", "Сходить в магазин", Status.Done, Priority.Low, demoth))
        itemRepository.findAll().forEach { println(it) }
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