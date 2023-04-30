package org.dnj.memoria

import java.util.Date

data class User(
    val name: String, 
    var password: String, 
    val creationDate: Date = Date(),
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
    var dueDate: Date? = null
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