package org.dnj.memoria.service

import org.dnj.memoria.model.Item
import org.dnj.memoria.model.ItemDto
import org.dnj.memoria.ItemRepository
import org.dnj.memoria.MemoriaException
import org.dnj.memoria.SpaceRepository
import org.dnj.memoria.model.User
import org.dnj.memoria.UserRepository
import org.dnj.memoria.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class ItemService(
    @Autowired val itemRepository: ItemRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val spaceRepository: SpaceRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ItemService::class.java)
    }
    
    fun getBySpace(user: User, spaceId: String): List<ItemDto> {
        if (user.spaceRefs.none { it.id == spaceId}) {
            throw MemoriaException("${user.name} had no access to space: $spaceId", HttpStatus.FORBIDDEN)
        }
        return itemRepository.findBySpaceId(spaceId).map { it.toDto() }
    }

//    fun getAllByUserSpace(user: User): List<ItemDto> {
//        return user.spaceRefs.flatMap { itemRepository.findBySpace(it) }.map { it.toDto() }
//    }
    
    fun getItem(id: String): ItemDto? {
        // todo: check access
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
    
    fun updateItem(requestItem: ItemDto, user: User): ItemDto? {
        validateItem(requestItem)
        
        if (requestItem.id == null) {
            logger.info("Creating new item: $requestItem")
            return updateItemFromDto(Item.empty(user), requestItem, user).toDto()

        } else {
            val existingItem = itemRepository.findById(requestItem.id).getOrNull()
                ?: return null

            logger.info("Updating item: $requestItem")

            return updateItemFromDto(existingItem, requestItem, user).toDto()
        }

    }

    private fun updateItemFromDto(existingItem: Item, requestItem: ItemDto, creator: User): Item {
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
                    existingItem.parentRef = parent.toRef()
                }
            } else {
                throw ValidationException("Parent task ${requestItem.parent.id} does not exist")
            }
        }

        if (requestItem.assignee != null) {
            val assignee = userRepository.findById(requestItem.assignee.id).getOrNull()
            if (assignee != null ) {
                existingItem.assigneeRef = assignee.toDto()
            } else {
                throw ValidationException("User ${requestItem.assignee.id} does not exist")
            }
        }
        
        if (requestItem.space?.id != null) {
            val space = spaceRepository.findById(requestItem.space.id).getOrNull() 
                ?: throw ValidationException("No such space: ${requestItem.space}")
            
            if (creator.spaceRefs.none {it.id == space.id})
                throw ValidationException("User is not in the space: $space")

            existingItem.spaceRef = space.toRef()
        }

        return itemRepository.save(existingItem)
    }

    private fun validateItem(requestItem: ItemDto) {
        if (requestItem.title.isNullOrBlank())
            throw ValidationException("Title cannot be null")
    }
}
