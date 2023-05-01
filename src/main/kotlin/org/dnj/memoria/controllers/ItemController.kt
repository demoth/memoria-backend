package org.dnj.memoria.controllers

import org.dnj.memoria.Item
import org.dnj.memoria.ItemRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping("/item")
@CrossOrigin
class ItemController(
    val itemRepository: ItemRepository
) {
    @GetMapping("/all")
    fun allItems(): ResponseEntity<*> {
        return ResponseEntity.ok(itemRepository.findAll())
    }
    
    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): ResponseEntity<Item> {
        return ResponseEntity.ok(itemRepository.findById(id).get())
    }
    
    @PostMapping
    fun updateItem(@RequestBody item: Item): ResponseEntity<Item> {
        item.updated = Date()
        return ResponseEntity.ok(itemRepository.save(item))
    }
    
    @DeleteMapping("/{id}")
    fun deleteItem(@PathVariable("id") id: String) {
        itemRepository.deleteById(id)
    }
}