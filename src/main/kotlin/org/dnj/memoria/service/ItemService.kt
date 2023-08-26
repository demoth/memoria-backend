package org.dnj.memoria.service

import org.dnj.memoria.*
import org.dnj.memoria.model.Item
import org.dnj.memoria.model.ItemDto
import org.dnj.memoria.model.User
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.lang.StringBuilder
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
@OptIn(kotlin.ExperimentalStdlibApi::class)
class ItemService(
    val itemRepository: ItemRepository,
    val userRepository: UserRepository,
    val spaceRepository: SpaceRepository,
    val memoriaTgBot: MessageSender
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ItemService::class.java)
    }
    
    fun getBySpace(user: User, spaceId: String): List<ItemDto> {
        if (user.spaceRefs.none { it.id == spaceId}) {
            throw MemoriaException("${user.name} had no access to space: $spaceId", HttpStatus.FORBIDDEN)
        }
        return itemRepository.findBySpaceRef_Id(spaceId).map { it.toDto() }
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
        val message = StringBuilder()

        if (requestItem.title != null && requestItem.title != existingItem.title) {
            message.append("\n title: ").append(existingItem.title).append("->").append(requestItem.title)
            existingItem.title = requestItem.title
        }

        if (requestItem.priority != null && requestItem.priority != existingItem.priority) {
            message.append("\n priority: ").append(existingItem.priority).append("->").append(requestItem.priority)
            existingItem.priority = requestItem.priority
        }

        if (requestItem.description != null && requestItem.description != existingItem.description) {
            message.append("\n description: ").append(existingItem.description).append("->").append(requestItem.description)
            existingItem.description = requestItem.description
        }

        if (requestItem.status != null && requestItem.status != existingItem.status) {
            message.append("\n status: ").append(existingItem.status).append("->").append(requestItem.status)
            existingItem.status = requestItem.status
        }

        if (requestItem.type != null && requestItem.type != existingItem.type) {
            message.append("\n type: ").append(existingItem.type).append("->").append(requestItem.type)
            existingItem.type = requestItem.type
        }

        if (requestItem.dueDate != null && requestItem.dueDate != existingItem.dueDate) {
            message.append("\n dueDate: ").append(existingItem.dueDate).append("->").append(requestItem.dueDate)
            existingItem.dueDate = requestItem.dueDate
        }

        if (requestItem.parent != null) {
            val parent = itemRepository.findById(requestItem.parent.id).getOrNull()
            if (parent != null) {
                if (parent.type != Item.TYPE_EPIC) {
                    throw ValidationException("Parent can only be epic: $parent")
                } else if (existingItem.type != Item.TYPE_TASK) {
                    throw ValidationException("Only tasks can have a parent")
                } else if (parent.toRef() != existingItem.parentRef) {
                    message.append("\n parent: ").append(existingItem.parentRef?.title).append("->").append(parent.toRef().title)
                    existingItem.parentRef = parent.toRef()
                }
            } else {
                throw ValidationException("Parent task ${requestItem.parent.id} does not exist")
            }
        }

        if (requestItem.assignee != null) {
            val assignee = userRepository.findById(requestItem.assignee.id).getOrNull()
            if (assignee != null && assignee.toDto() != existingItem.assigneeRef) {
                message.append("\n assignee: ").append(existingItem.assigneeRef?.name).append("->").append(assignee.toDto().name)
                existingItem.assigneeRef = assignee.toDto()
            } else {
                throw ValidationException("User ${requestItem.assignee.id} does not exist")
            }
        }
        
        if (requestItem.space?.id != null) {
            val space = spaceRepository.findById(requestItem.space.id).getOrNull() 
                ?: throw ValidationException("No such space: ${requestItem.space}")
            
            if (creator.spaceRefs.none { it.id == space.id })
                throw ValidationException("User is not in the space: $space")

            if (existingItem.spaceRef != space.toRef()) {
                message.append("\n space: ").append(existingItem.spaceRef?.name).append("->").append(space.toRef().name)
                existingItem.spaceRef = space.toRef()
            }
        }
        if (message.isNotEmpty()) {
            memoriaTgBot.sendUpdate("${existingItem.title} updated: " + message.toString())
        }
        return itemRepository.save(existingItem)
    }

    private fun validateItem(requestItem: ItemDto) {
        if (requestItem.title.isNullOrBlank())
            throw ValidationException("Title cannot be null")
    }
}
