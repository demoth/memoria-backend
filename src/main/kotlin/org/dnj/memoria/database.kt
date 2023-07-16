package org.dnj.memoria

import org.dnj.memoria.model.Item
import org.dnj.memoria.model.Space
import org.dnj.memoria.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, String> {
    fun findByName(name: String): Collection<User>
}

interface ItemRepository : CrudRepository<Item, String> {
    fun findBySpaceId(id: String): Collection<Item>
}

interface SpaceRepository: CrudRepository<Space, String>