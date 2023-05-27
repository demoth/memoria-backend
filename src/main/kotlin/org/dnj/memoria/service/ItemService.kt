package org.dnj.memoria.service

import org.dnj.memoria.Item
import org.dnj.memoria.ItemDto
import org.dnj.memoria.ItemRepository
import org.dnj.memoria.User
import org.dnj.memoria.UserRepository
import org.dnj.memoria.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class ItemService(
    @Autowired val itemRepository: ItemRepository,
    @Autowired val userRepository: UserRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ItemService::class.java)
    }
    
    fun getAllItems(user: User): List<ItemDto> {
        // todo: fetch only relevant items
        return itemRepository.findAll().map(Item::toDto).toList()
    }
    
    @OptIn(ExperimentalStdlibApi::class)
    fun getItem(id: String): ItemDto? {
        return itemRepository.findById(id).getOrNull()?.toDto()
    }
    
    fun deleteItem(id: String): Boolean {
        return if (itemRepository.findById(id).isPresent) {
            itemRepository.deleteById(id)
            logger.info("item: $id deleted")
            true
        } else {
            false
        }

    }
    
    @OptIn(ExperimentalStdlibApi::class)
    fun updateItem(requestItem: ItemDto, user: User): ItemDto? {
        validateItem(requestItem)
        
        if (requestItem.id == null) {
            logger.info("Creating new item: $requestItem")
            return updateItemFromDto(Item.empty(user), requestItem).toDto()

        } else {
            val existingItem = itemRepository.findById(requestItem.id).getOrNull()
                ?: return null

            logger.info("Updating item: $requestItem")

            return updateItemFromDto(existingItem, requestItem).toDto()
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
            val parent = itemRepository.findById(requestItem.parent.id).getOrNull()
            if (parent != null) {
                if (parent.type != Item.TYPE_EPIC) {
                    throw ValidationException("Parent can only be epic: $parent")
                } else if (existingItem.type != Item.TYPE_TASK) {
                    throw ValidationException("Only tasks can have a parent")
                } else {
                    existingItem.parent = parent
                }
            } else {
                throw ValidationException("Parent task ${requestItem.parent.id} does not exist")
            }
        }

        if (requestItem.assignee != null) {
            // todo: proper validation
            val assignee = userRepository.findById(requestItem.assignee.id).getOrNull()
            if (assignee != null ) {
                existingItem.assignee = assignee
            } else {
                throw ValidationException("User ${requestItem.assignee.id} does not exist")
            }
        }

        return itemRepository.save(existingItem)
    }

    private fun validateItem(requestItem: ItemDto) {
        if (requestItem.title.isNullOrBlank())
            throw ValidationException("Title cannot be null")
    }
}