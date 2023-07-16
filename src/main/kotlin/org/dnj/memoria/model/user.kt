package org.dnj.memoria.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    @Indexed(unique = true) //fixme: doesn't work
    val name: String,
    var password: String?, // todo: don't store password in plain text // todo2: make non nullable 
    @field:Id val id: String? = null,

    @Deprecated("starting from version 2: use spaceRefs instead")
    @field:DBRef
    var spaces:MutableList<Space>? = mutableListOf(),

    var version: String? = "1",
    var spaceRefs: MutableSet<SpaceRef> = mutableSetOf()
) {
    fun toDto() = UserDto(name, id ?: "N/A")
}

data class UserDto(val name: String, val id: String)
