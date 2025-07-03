package com.scr.btg.asynchronous.process.entrypoint.controller.unit.messaging

import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus
import com.scr.btg.asynchronous.process.domains.order.service.OrderService
import com.scr.btg.asynchronous.process.entrypoint.messaging.OrderCreationEventProcessor
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class OrderCreationEventProcessorTest {

    private val orderService = mockk<OrderService>()
    private val orderCreationEventProcessor = OrderCreationEventProcessor(orderService)

    @BeforeEach
    fun setUp() {
        clearMocks(orderService)
    }

    @Test
    fun `process should succeed and call orderService`() {
        val order = Order("clientId")
        every { orderService.updateStatus(order.id) } returns order.copy(status = OrderStatus.PROCESSED)
        assertDoesNotThrow { orderCreationEventProcessor.process(order.id) }
        verify(exactly = 1) { orderService.updateStatus(order.id) }
        confirmVerified(orderService)
    }

    @Test
    fun `process should throw exception when orderService fails`() {
        val orderId = "invalidId"
        every { orderService.updateStatus(orderId) } throws NoSuchElementException("Order not found")
        val throwable = Assertions.catchThrowable { orderCreationEventProcessor.process(orderId) }
        Assertions.assertThat(throwable).isInstanceOf(NoSuchElementException::class.java)
        verify(exactly = 1) { orderService.updateStatus(orderId) }
        confirmVerified(orderService)
    }
}