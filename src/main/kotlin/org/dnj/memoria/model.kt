package org.dnj.memoria

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import java.util.Date

data class User(
    val name: String,
    var password: String,
    @field:Id val id: String? = null
)

data class Item(
    var type: String,
    var title: String,
    var status: Status,
    var priority: Priority,

    @DBRef
    val creator: User,
    @DBRef
    var assignee: User? = null,

    var description: String? = null,
    var updated: Date = Date(),
    var dueDate: Date? = null,
    val created: Date = Date(),
    @field:Id val id: String? = null
)

enum class Priority {
    low,
    medium,
    high,
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
