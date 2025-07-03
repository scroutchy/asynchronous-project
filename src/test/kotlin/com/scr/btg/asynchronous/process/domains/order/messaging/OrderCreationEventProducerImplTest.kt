package com.scr.btg.asynchronous.process.domains.order.messaging

import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID

class OrderCreationEventProducerImplTest {

    private val kafkaTemplate = mockk<KafkaTemplate<String, String>>()
    private val orderCreationEventProcessor = OrderCreationEventProducerImpl(kafkaTemplate)

    @BeforeEach
    fun setUp() {
        clearMocks(kafkaTemplate)
    }

    @Test
    fun `sendOrderCreationEvent should succeed`() {
        val orderId = UUID.randomUUID().toString()
        every { kafkaTemplate.send("order-creation-events", orderId, orderId) } returns mockk()
        assertDoesNotThrow { orderCreationEventProcessor.sendOrderCreationEvent(orderId) }
        verify(exactly = 1) { kafkaTemplate.send("order-creation-events", orderId, orderId) }
        confirmVerified(kafkaTemplate)
    }
}