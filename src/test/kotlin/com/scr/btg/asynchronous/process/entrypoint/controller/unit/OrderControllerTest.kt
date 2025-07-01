package com.scr.btg.asynchronous.process.entrypoint.controller.unit

import com.scr.btg.asynchronous.process.domains.order.model.entity.Item
import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PENDING
import com.scr.btg.asynchronous.process.domains.order.service.OrderService
import com.scr.btg.asynchronous.process.entrypoint.controller.OrderController
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED

class OrderControllerTest {

    private val orderService = mockk<OrderService>()
    private val orderController = OrderController(orderService)

    @BeforeEach
    fun setUp() {
        clearMocks(orderService)
    }

    @Test
    fun `create should succeed`() {
        val order = Order("clientId", listOf(Item("item1"), Item("item2")))
        every { orderService.create(order) } answers { order }
        val result = orderController.create(order)
        assertThat(result.statusCode).isEqualTo(CREATED)
        assertThat(result.body).isEqualTo(order)
        verify(exactly = 1) { orderService.create(order) }
        confirmVerified(orderService)
    }

    @Test
    fun `find should succeed`() {
        val orderId = "orderId"
        val order = Order("clientId", listOf(Item("item1"), Item("item2")), PENDING, orderId)
        every { orderService.findById(orderId) } answers { order }
        val result = orderController.find(orderId)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(order)
        verify(exactly = 1) { orderService.findById(orderId) }
        confirmVerified(orderService)
    }
}