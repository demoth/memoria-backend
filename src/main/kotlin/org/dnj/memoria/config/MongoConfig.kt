package org.dnj.memoria.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration


@Configuration
class MongoConfig : AbstractMongoClientConfiguration() {
    private val DB_NAME = "memoria-db"

    override fun getDatabaseName(): String {
        return DB_NAME
    }

    override fun mongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://localhost:27017/$DB_NAME")
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