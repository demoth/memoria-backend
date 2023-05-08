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
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/item")
@CrossOrigin
class ItemController(
    val itemRepository: ItemRepository
) {
    @GetMapping("/all")
    fun allItems(): ResponseEntity<*> {
        val items = itemRepository.findAll()
        items.forEach {
            filterPasswords(it)
            val parent = it.parent
            if (parent != null)
                filterPasswords(parent)
        }
        return ResponseEntity.ok(items)
    }

    private fun filterPasswords(it: Item) {
        it.assignee?.password = ""
        it.creator.password = ""
    }

    @OptIn(ExperimentalStdlibApi::class)
    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): ResponseEntity<Item> {
        val item = itemRepository.findById(id).getOrElse { 
            return ResponseEntity.notFound().build()
        }
        filterPasswords(item)
        return ResponseEntity.ok(item)
    }

    @PostMapping
    fun updateItem(@RequestBody item: Item): ResponseEntity<Item> {
        println("item: ${item.id} updated")
        item.updated = Date()
        return ResponseEntity.ok(itemRepository.save(item))
    }

    @DeleteMapping("/{id}")
    fun deleteItem(@PathVariable("id") id: String): ResponseEntity<String> {
        println("item: $id deleted")
        itemRepository.deleteById(id)
        return ResponseEntity.ok("OK")
    }
}