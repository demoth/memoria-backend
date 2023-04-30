package org.dnj.memoria

import org.springframework.data.annotation.Id
import java.util.Date

data class User(
    val name: String, 
    var password: String, 
    val creationDate: Date = Date(),
    @field:Id val id: String? = null
)

data class Item(
    var type: String,
    var title: String,
    var status: Status,
    var priority: Priority,

    val creator: User,
    var assignee: User? = null,

    var description: String? = null,
    val created: Date = Date(),
    var updated: Date = Date(),
    var dueDate: Date? = null,
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