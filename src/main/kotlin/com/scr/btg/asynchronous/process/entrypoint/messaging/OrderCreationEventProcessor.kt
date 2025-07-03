package com.scr.btg.asynchronous.process.entrypoint.messaging

import com.scr.btg.asynchronous.process.domains.order.service.OrderService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderCreationEventProcessor(private val orderService: OrderService) {

    private val logger = LoggerFactory.getLogger(OrderCreationEventProcessor::class.java)

    @Value("\${kafka.processing.delay:0}")
    private val delay: Long = 0

    @KafkaListener(topics = ["\${kafka.topics.order-creation}"], groupId = "status-update-group")
    fun process(orderId: String) {
        Thread.sleep(delay) // simulate delay in processing
        logger.info("Processing order creation event for orderId: $orderId")
        try {
            orderService.updateStatus(orderId).also { logger.info("Order creation event successfully processed for orderId: $orderId") }
        } catch (e: Exception) {
            logger.warn("Failed to process order creation event for orderId: $orderId", e)
            throw e
        }
    }
}