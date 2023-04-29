package org.dnj.memoria

import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findByName(name: String): Collection<User>
}