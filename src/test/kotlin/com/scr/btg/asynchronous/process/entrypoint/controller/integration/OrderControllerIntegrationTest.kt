package com.scr.btg.asynchronous.process.entrypoint.controller.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.scr.btg.asynchronous.process.domains.order.model.entity.Item
import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PENDING
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepository
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepositoryImpl
import com.scr.btg.asynchronous.process.readResponseBody
import com.scr.btg.asynchronous.process.withJsonBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.concurrent.ConcurrentHashMap

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val orderRepository: OrderRepository
) {

    @Test
    fun `create order should succeed and create order in map`() {
        val request = Order("clientId", listOf(Item("item1"), Item("item2")))
        val result = mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .withJsonBody(request, objectMapper)
        ).andExpect(status().isCreated).andReturn()
        val body = result.readResponseBody<Order>(objectMapper)
        assertThat(body).isNotNull
        assertThat(body.id).isNotNull
        assertThat(body.clientId).isEqualTo(request.clientId)
        assertThat(body.items).containsExactlyInAnyOrderElementsOf(request.items)
        assertThat(body.status).isEqualTo(PENDING)
    }

    @Test
    fun `find order should succeed and return an order`() {
        val order = Order("clientId", listOf(Item("item1"), Item("item2")))
        orderRepository.save(order)
        val result = mockMvc.perform(get("/api/orders/" + order.id))
            .andExpect(status().isOk).andReturn()
        val body = result.readResponseBody<Order>(objectMapper)
        assertThat(body).isNotNull
        assertThat(body.id).isEqualTo(order.id)
        assertThat(body.clientId).isEqualTo(order.clientId)
        assertThat(body.items).containsExactlyInAnyOrderElementsOf(order.items)
        assertThat(body.status).isEqualTo(PENDING)
    }
}