package com.scr.btg.asynchronous.process.entrypoint.controller

import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.service.OrderService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/orders")
class OrderController(private val orderService: OrderService) {

    private val logger: Logger = LoggerFactory.getLogger(OrderController::class.java)

    @PostMapping
    fun create(@RequestBody @Valid order: Order): ResponseEntity<Order> {
        logger.debug("Processing order request for client order: $order")
        return ResponseEntity.status(CREATED).body(orderService.create(order))
            .also { logger.info("Order with id ${it.body?.id} created") }
    }

    @GetMapping("/{id}")
    fun find(@PathVariable id: String): ResponseEntity<Order> {
        logger.debug("Processing request to find order with id: $id")
        return ResponseEntity.ok(orderService.findById(id))
            .also { logger.debug("Order with id $id found") }
    }
}