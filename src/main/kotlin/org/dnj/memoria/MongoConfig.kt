package org.dnj.memoria

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration

@Configuration
class MongoConfig : AbstractMongoClientConfiguration() {
    override fun getDatabaseName(): String {
        return "test"
    }

    override fun mongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://localhost:27017/test")
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(mongoClientSettings)
    }

    public override fun getMappingBasePackages(): Collection<String> {
        return setOf("org.dnj.memoria")
    }
}