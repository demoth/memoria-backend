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
    var password: String, // todo: don't store password in plain text
    @field:Id val id: String? = null
)

@Document
data class Item(
    var type: String,
    var title: String,
    var status: Status,
    var priority: Priority,

    @DBRef
    val creator: User,
    @DBRef
    var assignee: User? = null,
    @DBRef
    var parent: Item? = null,

    var description: String? = null, // html? md?
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
