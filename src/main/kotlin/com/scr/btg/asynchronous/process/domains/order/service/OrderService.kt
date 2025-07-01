package com.scr.btg.asynchronous.process.domains.order.service

import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRepository: OrderRepository) {

    private val logger: Logger = LoggerFactory.getLogger(OrderService::class.java)

    fun create(order: Order): Order {
        logger.debug("Creating order for client ${order.clientId} with id ${order.id}")
        return try {
            orderRepository.save(order).also { logger.info("Order for client ${order.clientId} created with id ${it.id}") }
        } catch (e: Exception) {
            logger.warn("Error creating order for client ${order.clientId}", e)
            throw e
        }
    }

    fun findById(id: String): Order {
        logger.debug("Finding order with id $id")
        return orderRepository.findById(id)?.also { logger.debug("Order with id ${it.id} was successfully retrieved") }
            ?: throw NoSuchElementException("Order with id $id not found").also { logger.warn("Order with id $id not found") }
    }
}