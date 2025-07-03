package com.scr.btg.asynchronous.process

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.Network
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.utility.DockerImageName

@TestInstance(PER_CLASS)
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

    companion object {

        private val network = Network.newNetwork()
        val kafkaContainer = ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.2"))
            .withNetwork(network)
            .withNetworkAliases("kafka")
            .withEnv("KAFKA_LOG4J_ROOT_LOGLEVEL", "DEBUG")
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
        }
    }
}