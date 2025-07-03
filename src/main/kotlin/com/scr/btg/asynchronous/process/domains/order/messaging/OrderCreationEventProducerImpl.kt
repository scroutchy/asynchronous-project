package com.scr.btg.asynchronous.process.domains.order.messaging

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderCreationEventProducerImpl(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${kafka.topics.order-creation}") private val topicName: String = "order-creation-events"
) :
    OrderCreationEventProducer {

    private val logger = LoggerFactory.getLogger(OrderCreationEventProducerImpl::class.java)

    override fun sendOrderCreationEvent(orderId: String) {
        kafkaTemplate.send(topicName, orderId, orderId).also { logger.info("Order creation event sent for orderId: $orderId") }
    }
}