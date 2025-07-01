package com.scr.btg.asynchronous.process.domains.order.repository

import com.scr.btg.asynchronous.process.domains.order.model.entity.Item
import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PENDING
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.ConcurrentHashMap

@SpringBootTest
class OrderRepositoryImplTest(@Autowired private val orderRepository: OrderRepositoryImpl) {

    @Test
    fun `save should succeed`() {
        val order = Order("test-client", listOf(Item("item1"), Item("item2")))
        val output = orderRepository.save(order)

        assertThat(output).isNotNull
        assertThat(output.clientId).isEqualTo(order.clientId)
        assertThat(output.items).containsExactlyInAnyOrderElementsOf(order.items)
        assertThat(output.status).isEqualTo(PENDING)
        assertThat(output.id).isNotEmpty()
        val orders = OrderRepositoryImpl::class.java.getDeclaredField("orders")
        orders.isAccessible = true
        val ordersMap = orders.get(orderRepository) as ConcurrentHashMap<String, *>

        assertThat(ordersMap).isNotNull
        assertThat(ordersMap).containsKey(output.id)
        val savedOrder = ordersMap[output.id] as Order
        assertThat(savedOrder.clientId).isEqualTo(order.clientId)
        assertThat(savedOrder.items).containsExactlyInAnyOrderElementsOf(order.items)
        assertThat(savedOrder.status).isEqualTo(PENDING)
        assertThat(savedOrder.id).isEqualTo(output.id)
    }

    @Test
    fun `findById should return an order when it exists in the repository`() {
        val order = Order("test-client", listOf(Item("item1"), Item("item2")))
        val savedOrder = orderRepository.save(order)
        val retrievedOrder = orderRepository.findById(savedOrder.id)
        assertThat(retrievedOrder).isNotNull
        with(retrievedOrder!!) {
            assertThat(clientId).isEqualTo(order.clientId)
            assertThat(items).containsExactlyInAnyOrderElementsOf(order.items)
            assertThat(status).isEqualTo(PENDING)
            assertThat(id).isEqualTo(savedOrder.id)
        }
    }

    @Test
    fun `findById should return empty when order does not exist`() {
        val output = orderRepository.findById("dummy-id")
        assertThat(output).isNull()
    }
}