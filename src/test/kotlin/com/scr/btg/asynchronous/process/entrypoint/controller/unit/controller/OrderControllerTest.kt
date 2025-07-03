package com.scr.btg.asynchronous.process.entrypoint.controller.unit.controller

import com.scr.btg.asynchronous.process.domains.order.model.entity.Item
import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus
import com.scr.btg.asynchronous.process.domains.order.service.OrderService
import com.scr.btg.asynchronous.process.entrypoint.controller.OrderController
import com.scr.btg.asynchronous.process.entrypoint.mapper.toApiDto
import com.scr.btg.asynchronous.process.entrypoint.model.api.ItemApiDto
import com.scr.btg.asynchronous.process.entrypoint.model.api.OrderApiDto
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class OrderControllerTest {

    private val orderService = mockk<OrderService>()
    private val orderController = OrderController(orderService)

    @BeforeEach
    fun setUp() {
        clearMocks(orderService)
    }

    @Test
    fun `create should succeed`() {
        val request = OrderApiDto("clientId", listOf(ItemApiDto("item1"), ItemApiDto("item2")))
        every { orderService.create(any()) } answers { firstArg() }
        val result = orderController.create(request)
        Assertions.assertThat(result.statusCode).isEqualTo(HttpStatus.CREATED)
        with(result.body!!) {
            Assertions.assertThat(clientId).isEqualTo(request.clientId)
            Assertions.assertThat(items).containsExactlyInAnyOrderElementsOf(request.items)
            Assertions.assertThat(status).isEqualTo(OrderStatus.PENDING)
            Assertions.assertThat(id).isNotEmpty()
        }
        verify {
            orderService.create(match { o ->
                o.clientId == request.clientId && o.items.map { it.description }
                    .containsAll(request.items.map { it.description }) && o.status == OrderStatus.PENDING && o.id.isNotEmpty()
            })
        }
        confirmVerified(orderService)
    }

    @Test
    fun `find should succeed`() {
        val orderId = "orderId"
        val order = Order("clientId", listOf(Item("item1"), Item("item2")), OrderStatus.PENDING, orderId)
        every { orderService.findById(orderId) } answers { order }
        val result = orderController.find(orderId)
        Assertions.assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(result.body).isEqualTo(order.toApiDto())
        verify(exactly = 1) { orderService.findById(orderId) }
        confirmVerified(orderService)
    }
}