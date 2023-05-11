package org.dnj.memoria.controllers

import org.dnj.memoria.Item
import org.dnj.memoria.ItemDto
import org.dnj.memoria.ItemRepository
import org.dnj.memoria.service.AuthService
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

@RestController
@RequestMapping("/item")
@CrossOrigin
class ItemController(
    val itemRepository: ItemRepository,
    val authService: AuthService
) {


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

    @PostMapping
    fun updateItem(
        @RequestBody requestItem: Item,
        @RequestHeader("Authentication") token: String
    ): ResponseEntity<ItemDto> {
        
        val user = authService.validateToken(token)

        if (!validateItem(requestItem))
            return ResponseEntity.badRequest().build()

        requestItem.updated = Date()
        if (requestItem.id == null) {
            requestItem.creator = user
            requestItem.created = Date()
            println("new item $requestItem created")
        } else {
            println("item $requestItem updated")
        }
        val saved = itemRepository.save(requestItem)
        return ResponseEntity.ok(saved.toDto())
    }

    private fun validateItem(requestItem: Item): Boolean {
        if (requestItem.title.isBlank()) 
            return false
        return true
    }

    @DeleteMapping("/{id}")
    fun deleteItem(
        @PathVariable("id") id: String,
        @RequestHeader("Authentication") token: String
    ): ResponseEntity<String> {
        authService.validateToken(token)
        println("item: $id deleted")
        itemRepository.deleteById(id)
        return ResponseEntity.ok("OK")
    }
}