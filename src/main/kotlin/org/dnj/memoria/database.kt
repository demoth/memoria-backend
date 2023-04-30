package org.dnj.memoria

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, String> {
    fun findByName(name: String): Collection<User>
}

interface ItemRepository : CrudRepository<Item, String>