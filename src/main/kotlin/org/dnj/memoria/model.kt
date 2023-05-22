package org.dnj.memoria

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document
data class User(
    @Indexed(unique = true) //fixme: doesn't work
    val name: String,
    var password: String?, // todo: don't store password in plain text // todo2: make non nullable 
    @field:Id val id: String? = null
) {
    fun toDto() = UserDto(name, id ?: "N/A")
}

data class UserDto(
    val name: String,
    val id: String,
)

@Document
data class Item(
    var type: String,
    var title: String,
    var status: Status,
    var priority: Priority,

    @field:DBRef
    var creator: User? = null,
    @field:DBRef
    var assignee: User? = null,
    @field:DBRef
    var parent: Item? = null,

    var description: String? = null, // html? md?
    var updated: Date = Date(),
    var dueDate: Date? = null,
    var created: Date = Date(),
    @field:Id val id: String? = null
) {
    
    companion object {
        fun empty(creator: User) = Item("", "", Status.Todo, Priority.Low).apply { this.creator = creator }
    }
        
    fun toDto(): ItemDto = ItemDto(type, title, status, priority, creator?.toDto(), assignee?.toDto(), parent?.toSmallDto(), description, updated, dueDate, created, id)
    
    private fun toSmallDto() = ItemSmallDto(title, id ?: "N/A")
}

data class ItemSmallDto(
    val title: String,
    val id: String
)

data class ItemDto(
    val type: String?,
    val title: String?,
    val status: Status?,
    val priority: Priority?,
    val creator: UserDto?,
    val assignee: UserDto?,
    val parent: ItemSmallDto?,
    val description: String?,
    val updated: Date?,
    val dueDate: Date?,
    val created: Date?,
    val id: String?
)

enum class Priority {
    Low,
    Medium,
    High,
}

enum class Status {
    Backlog,
    Todo,
    InProgress,
    Done
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val username: String,
    val jwt: String
)

data class ErrorResponse(
    val message: String
)
