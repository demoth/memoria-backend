package org.dnj.memoria.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document
data class Item(
    var type: String,
    var title: String,
    var status: Status,
    var priority: Priority,

    @Deprecated("starting from version 2: use creatorRef")
    @field:DBRef
    var creator: User? = null,

    @Deprecated("starting from version 2: use assigneeRef")
    @field:DBRef
    var assignee: User? = null,

    @Deprecated("starting from version 2: use parentRef")
    @field:DBRef
    var parent: Item? = null,

    var description: String? = null, // html? md?
    var updated: Date = Date(),
    var dueDate: Date? = null,
    var created: Date = Date(),
    @field:Id val id: String? = null,

    @Deprecated("starting from version 2: use spaceRef")
    @field:DBRef var space: Space? = null,

    var version: String? = "1",
    // version "2" fields
    var creatorRef: UserDto? = null,
    var assigneeRef: UserDto? = null,
    var parentRef: ItemRef? = null,
    var spaceRef: SpaceRef? = null


    ) {
    
    companion object {
        fun empty(creator: User) = Item("", "", Status.Todo, Priority.Low).apply { this.creatorRef = creator.toDto() }

        const val TYPE_EPIC = "Epic"
        const val TYPE_TASK = "Task"
    }
        
    fun toDto(): ItemDto = ItemDto(
        type,
        title,
        status,
        priority,
        creatorRef,
        assigneeRef,
        parentRef,
        description,
        updated,
        dueDate,
        created,
        id,
        spaceRef
    )
    
    fun toRef() = ItemRef(id ?: "N/A", title)
}

data class ItemRef(val id: String, val title: String?)
data class ItemDto(
    val type: String?,
    val title: String?,
    val status: Status?,
    val priority: Priority?,
    val creator: UserDto?,
    val assignee: UserDto?,
    val parent: ItemRef?,
    val description: String?,
    val updated: Date?,
    val dueDate: Date?,
    val created: Date?,
    val id: String?,
    val space: SpaceRef?
)