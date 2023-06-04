package org.dnj.memoria.model


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

data class ErrorResponse(
    val message: String
)
