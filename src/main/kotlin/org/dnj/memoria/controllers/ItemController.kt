package org.dnj.memoria.controllers

import jakarta.websocket.server.PathParam
import org.dnj.memoria.Item
import org.dnj.memoria.ItemRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/item")
class ItemController(
    val itemRepository: ItemRepository
) {
    @GetMapping("/all")
    fun allItems(): ResponseEntity<*> {
        return ResponseEntity.ok(itemRepository.findAll())
    }
    
    @GetMapping("/{id}")
    fun getById(@PathParam("id") id: String): ResponseEntity<Item> {
        return ResponseEntity.ok(itemRepository.findById(id).get())
    }
    
    @PostMapping
    fun updateItem(@RequestBody item: Item): ResponseEntity<String> {
        itemRepository.save(item)
        return ResponseEntity.ok("Ok")
    }
}