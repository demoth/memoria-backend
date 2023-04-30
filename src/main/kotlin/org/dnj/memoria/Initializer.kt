package org.dnj.memoria

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Initializer(
    val userRepository: UserRepository,
    val itemRepository: ItemRepository
): CommandLineRunner {
    override fun run(vararg args: String?) {
        userRepository.deleteAll()
        itemRepository.deleteAll()
        val demoth = userRepository.save(User("demoth", "whatever"))
        val denolia = userRepository.save(User("denolia", "whatever"))
        
        println("Users in database:")
        userRepository.findAll().forEach { println("Found user: $it") }
        
        println("Demoths:")
        userRepository.findByName("demoth").forEach { println(it) }
        
        println("Items:")
        itemRepository.save(Item("Task", "Позвонить в страховую", Status.Backlog, Priority.high, demoth))
        itemRepository.save(Item("Task", "Пополнить баланс", Status.Backlog, Priority.medium, demoth))
        itemRepository.save(Item("Task", "Помыть кухню", Status.Backlog, Priority.medium, denolia))
        itemRepository.save(Item("Task", "Поиграть в пое", Status.Backlog, Priority.high, denolia))
        itemRepository.save(Item("Epic", "Написать свой ноушен", Status.Backlog, Priority.high, demoth))
        itemRepository.save(Item("Task", "Сходить в магазин", Status.Backlog, Priority.low, demoth))
        itemRepository.findAll().forEach { println(it) }
    }
}