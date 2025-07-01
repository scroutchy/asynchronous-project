package com.scr.btg.asynchronous.process.domains.order.service

import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
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
    private val orderService = OrderService(orderRepository)

    @BeforeEach
    fun setup() {
        clearMocks(orderRepository)
    }

    @Test
    fun `create order should succeed and save in repository`() {
        val order = Order("clientId", listOf())
        every { orderRepository.save(order) } answers { order }
        val result = orderService.create(order)
        assertThat(result).isEqualTo(order)
        verify(exactly = 1) { orderRepository.save(order) }
        confirmVerified(orderRepository)
    }

    @Test
    fun `create order should throw exception when saving fails`() {
        val order = Order("clientId", listOf())
        every { orderRepository.save(order) } throws RuntimeException("Error")
        val throwable = catchThrowable { orderService.create(order) }
        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
            .hasMessage("Error")
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
}