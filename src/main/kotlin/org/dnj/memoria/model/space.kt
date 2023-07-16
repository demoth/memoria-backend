package org.dnj.memoria.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document
data class Space(
    @field:Indexed(unique = true) val name: String,
    var description: String? = null,
    val created: Date = Date(),
    @field:Id val id: String? = null,

    @Deprecated("starting from version2: user owner ref")
    @field:DBRef var owner: User? = null,

    var version: String? = "1",

    var ownerRef: UserDto? = null
) {
    fun toDto() = SpaceDto(id, name, description, emptySet(), ownerRef)

    fun toRef() = SpaceRef(id ?: "N/A", name)
}

data class SpaceRef(val id: String, val name: String)

data class SpaceDto(
    val id: String?,
    val name: String,
    val description: String?,
    val participants: Collection<UserDto>? = null,
    val owner: UserDto? = null
)