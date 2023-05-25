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
        val mongoUser = System.getenv("MONGODB_USER") ?: "admin"
        val mongoPass = System.getenv("MONGODB_PASS") ?: "admin"
        val mongoUrl = System.getenv("MONGODB_URL") ?: "localhost"
        // todo: assert non empty params
        val connectionString = ConnectionString("mongodb://$mongoUser:$mongoPass@$mongoUrl:27017")
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(mongoClientSettings)
    }

    public override fun getMappingBasePackages(): Collection<String> {
        return setOf("org.dnj.memoria")
    }
}