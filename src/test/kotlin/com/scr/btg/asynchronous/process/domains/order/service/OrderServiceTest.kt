package com.scr.btg.asynchronous.process.domains.order.service

import com.scr.btg.asynchronous.process.domains.order.messaging.OrderCreationEventProducer
import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PROCESSED
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepository
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderServiceTest {

    private val orderRepository = mockk<OrderRepository>()
    private val orderMessaging = mockk<OrderCreationEventProducer>()
    private val orderService = OrderService(orderRepository, orderMessaging)

    @BeforeEach
    fun setup() {
        clearMocks(orderRepository, orderMessaging)
    }

    @Test
    fun `create order should succeed and save in repository`() {
        val order = Order("clientId", listOf())
        every { orderRepository.save(order) } answers { order }
        every { orderMessaging.sendOrderCreationEvent(order.id) } returns Unit
        val result = orderService.create(order)
        assertThat(result).isEqualTo(order)
        verify(exactly = 1) { orderRepository.save(order) }
        verify(exactly = 1) { orderMessaging.sendOrderCreationEvent(order.id) }
        confirmVerified(orderRepository, orderMessaging)
    }

    @Test
    fun `create order should throw exception when saving fails`() {
        val order = Order("clientId", listOf())
        every { orderRepository.save(order) } throws RuntimeException("Error")
        val throwable = catchThrowable { orderService.create(order) }
        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
            .hasMessage("Error")
        verify(exactly = 1) { orderRepository.save(order) }
        verify(inverse = true) { orderMessaging.sendOrderCreationEvent(order.id) }
        confirmVerified(orderRepository, orderMessaging)
    }

    @Test
    fun `findById should return order when found`() {
        val order = Order("clientId", listOf())
        every { orderRepository.findById(order.id) } returns order
        val result = orderService.findById(order.id)
        assertThat(result).isEqualTo(order)
        verify(exactly = 1) { orderRepository.findById(order.id) }
        confirmVerified(orderRepository)
    }

    @Test
    fun `findById should throw exception when order is not found`() {
        val orderId = "non-existent-id"
        every { orderRepository.findById(orderId) } returns null
        val throwable = catchThrowable { orderService.findById(orderId) }
        assertThat(throwable).isInstanceOf(NoSuchElementException::class.java)
            .hasMessage("Order with id $orderId not found")
        verify(exactly = 1) { orderRepository.findById(orderId) }
        confirmVerified(orderRepository)
    }

    @Test
    fun `updateStatus should succeed and save in repository`() {
        val order = Order("clientId", listOf())
        every { orderRepository.findById(order.id) } returns order
        every { orderRepository.save(any()) } answers { firstArg() }
        val result = orderService.updateStatus(order.id)
        assertThat(result).isEqualTo(order.copy(status = PROCESSED))
        verify(exactly = 1) { orderRepository.findById(order.id) }
        verify(exactly = 1) { orderRepository.save(order.copy(status = PROCESSED)) }
        confirmVerified(orderRepository)
    }

    @Test
    fun `updateStatus should throw exception when order is not found`() {
        val orderId = "non-existent-id"
        every { orderRepository.findById(orderId) } returns null
        val throwable = catchThrowable { orderService.updateStatus(orderId) }
        assertThat(throwable).isInstanceOf(NoSuchElementException::class.java)
            .hasMessage("Order with id $orderId not found")
        verify(exactly = 1) { orderRepository.findById(orderId) }
        verify(inverse = true) { orderRepository.save(any()) }
        confirmVerified(orderRepository)
    }

    @Test
    fun `updateStatus order should throw exception when saving fails`() {
        val order = Order("clientId", listOf())
        every { orderRepository.findById(order.id) } returns order
        every { orderRepository.save(any()) } throws RuntimeException("Error")
        val throwable = catchThrowable { orderService.updateStatus(order.id) }
        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
            .hasMessage("Error")
        verify(exactly = 1) { orderRepository.findById(order.id) }
        verify(exactly = 1) { orderRepository.save(order.copy(status = PROCESSED)) }
        verify(inverse = true) { orderMessaging.sendOrderCreationEvent(any()) }
        confirmVerified(orderRepository, orderMessaging)
    }
}