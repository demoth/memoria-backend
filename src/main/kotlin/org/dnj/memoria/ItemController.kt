package org.dnj.memoria

import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.Mapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ItemController(
    val itemRepository: ItemRepository
) {
    @GetMapping("/all")
    fun allItems(): ResponseEntity<Collection<Item>> {
        return ResponseEntity(itemRepository.findAll(), HttpStatusCode.valueOf(200))
    }
    
    @PostMapping
    fun updateItem(@RequestBody item: Item): ResponseEntity<String> {
        itemRepository.save(item)
        return ResponseEntity.ok("Ok")
    }
}