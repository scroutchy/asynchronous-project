package com.scr.btg.asynchronous.process.entrypoint.controller

import com.scr.btg.asynchronous.process.domains.order.service.OrderService
import com.scr.btg.asynchronous.process.entrypoint.controller.ApiConstants.ORDER_PATH
import com.scr.btg.asynchronous.process.entrypoint.controller.ValidationGroups.OrderRequest
import com.scr.btg.asynchronous.process.entrypoint.mapper.toApiDto
import com.scr.btg.asynchronous.process.entrypoint.mapper.toEntity
import com.scr.btg.asynchronous.process.entrypoint.model.api.OrderApiDto
import jakarta.validation.groups.Default
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ORDER_PATH)
class OrderController(private val orderService: OrderService) {

    private val logger: Logger = LoggerFactory.getLogger(OrderController::class.java)

    @PostMapping
    fun create(@RequestBody @Validated(Default::class, OrderRequest::class) order: OrderApiDto): ResponseEntity<OrderApiDto> {
        logger.debug("Processing order request for client order: $order")
        return ResponseEntity.status(CREATED).body(orderService.create(order.toEntity()).toApiDto())
            .also { logger.info("Order with id ${it.body?.id} created") }
    }

    @GetMapping("/{id}")
    fun find(@PathVariable id: String): ResponseEntity<OrderApiDto> {
        logger.debug("Processing request to find order with id: $id")
        return ResponseEntity.ok(orderService.findById(id).toApiDto())
            .also { logger.debug("Order with id $id found") }
    }
}