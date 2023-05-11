package org.dnj.memoria.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import java.lang.IllegalStateException


@Configuration
class MongoConfig : AbstractMongoClientConfiguration() {
    private val DB_NAME = "memoria-db"

    override fun getDatabaseName(): String {
        return DB_NAME
    }

    override fun mongoClient(): MongoClient {
        val mongoUser = System.getenv("MONGODB_USER") ?: throw IllegalStateException("No mongodb username is given!")
        val mongoPass = System.getenv("MONGODB_PASS") ?: throw IllegalStateException("No mongodb password is given!")
        val mongoUrl = System.getenv("MONGODB_URL") ?: throw IllegalStateException("No mongodb url is given!")
        // todo: assert non empty params
        val connectionString = ConnectionString("mongodb://$mongoUser:$mongoPass@$mongoUrl:27017")
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        val client = MongoClients.create(mongoClientSettings)
        return client
    }

    public override fun getMappingBasePackages(): Collection<String> {
        return setOf("org.dnj.memoria")
    }
}