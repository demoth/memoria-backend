package org.dnj.memoria

import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String>