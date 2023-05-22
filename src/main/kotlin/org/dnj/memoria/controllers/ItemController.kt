package org.dnj.memoria.controllers

import org.dnj.memoria.Item
import org.dnj.memoria.ItemDto
import org.dnj.memoria.ItemRepository
import org.dnj.memoria.UserRepository
import org.dnj.memoria.service.AuthService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/item")
@CrossOrigin
class ItemController(
    val itemRepository: ItemRepository,
    val authService: AuthService,
    val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(ItemController::class.java)

    @GetMapping("/all")
    fun allItems(
        @RequestHeader("Authentication") token: String
    ): ResponseEntity<*> {
        authService.validateToken(token)
        return ResponseEntity.ok(itemRepository.findAll().map { it.toDto() })
    }

    @OptIn(ExperimentalStdlibApi::class)
    @GetMapping("/{id}")
    fun getById(
        @PathVariable("id") id: String,
        @RequestHeader("Authentication") token: String
    ): ResponseEntity<ItemDto> {
        authService.validateToken(token)
        val item = itemRepository.findById(id).getOrElse {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(item.toDto())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @PostMapping
    fun updateItem(
        @RequestBody requestItem: ItemDto,
        @RequestHeader("Authentication") token: String
    ): ResponseEntity<ItemDto> {
        
        val user = authService.validateToken(token)

        if (!validateItem(requestItem)) {
            logger.info("Invalid item: $requestItem")
            return ResponseEntity.badRequest().build()
        }
        if (requestItem.id == null) {
            logger.info("Creating new item: $requestItem")
            return ResponseEntity.ok(updateItemFromDto(Item.empty(user), requestItem).toDto())
            
        } else {
            val existingItem = itemRepository.findById(requestItem.id).getOrNull() 
                ?: return ResponseEntity.notFound().build()
            
            logger.info("Updating item: $requestItem")
            
            return ResponseEntity.ok(updateItemFromDto(existingItem, requestItem).toDto())
        }
        
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun updateItemFromDto(existingItem: Item, requestItem: ItemDto): Item {
        existingItem.updated = Date()

        if (requestItem.title != null)
            existingItem.title = requestItem.title

        if (requestItem.priority != null)
            existingItem.priority = requestItem.priority

        if (requestItem.description != null)
            existingItem.description = requestItem.description

        if (requestItem.status != null)
            existingItem.status = requestItem.status

        if (requestItem.type != null)
            existingItem.type = requestItem.type

        if (requestItem.dueDate != null)
            existingItem.dueDate = requestItem.dueDate

        if (requestItem.parent != null) {
            // todo: proper validation
            val parent = itemRepository.findById(requestItem.parent.id).getOrNull()
            if (parent != null) {
                existingItem.parent = parent
            } else {
                logger.warn("Parent task ${requestItem.parent.id} does not exist")
            }
        }

        if (requestItem.assignee != null) {
            // todo: proper validation
            val assignee = userRepository.findById(requestItem.assignee.id).getOrNull()
            if (assignee != null ) {
                existingItem.assignee = assignee
            } else {
                logger.warn("User ${requestItem.assignee.id} does not exist")
            }
        }

        return itemRepository.save(existingItem)
    }

    private fun validateItem(requestItem: ItemDto): Boolean {
        return !requestItem.title.isNullOrBlank()
    }

    @DeleteMapping("/{id}")
    fun deleteItem(
        @PathVariable("id") id: String,
        @RequestHeader("Authentication") token: String
    ): ResponseEntity<String> {
        authService.validateToken(token)
        logger.info("item: $id deleted")
        itemRepository.deleteById(id)
        return ResponseEntity.ok("OK")
    }
}