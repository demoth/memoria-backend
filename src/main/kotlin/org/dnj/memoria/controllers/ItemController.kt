package org.dnj.memoria.controllers

import org.dnj.memoria.model.ErrorResponse
import org.dnj.memoria.model.ItemDto
import org.dnj.memoria.service.AuthService
import org.dnj.memoria.service.ItemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/item")
@CrossOrigin
class ItemController(
    val authService: AuthService,
    val itemService: ItemService
) {

    @GetMapping("/all")
    fun allItems(
        @RequestHeader("Authentication") token: String,
        @RequestParam("space") spaceId: String,
    ): ResponseEntity<*> {
        val user = authService.validateToken(token)
        return ResponseEntity.ok(itemService.getBySpace(user, spaceId))
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable("id") id: String,
        @RequestHeader("Authentication") token: String
    ): ResponseEntity<ItemDto> {
        authService.validateToken(token)

        val item = itemService.getItem(id)
        
        return if (item != null)
            ResponseEntity.ok(item)
        else
            ResponseEntity.notFound().build()
    }

    @PostMapping
    fun updateItem(
        @RequestBody requestItem: ItemDto,
        @RequestHeader("Authentication") token: String
    ): ResponseEntity<ItemDto> {
        
        val user = authService.validateToken(token)

        val result = itemService.updateItem(requestItem, user)

        return if (result != null)
            ResponseEntity.ok(result)
        else
            ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteItem(
        @PathVariable("id") id: String,
        @RequestHeader("Authentication") token: String
    ): ResponseEntity<String> {
        authService.validateToken(token)
        
        return if (itemService.deleteItem(id))
            ResponseEntity.ok("OK")
        else
            ResponseEntity.notFound().build()
            
    }
}