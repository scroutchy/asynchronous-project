package com.scr.btg.asynchronous.process.entrypoint.controller.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.scr.btg.asynchronous.process.domains.order.model.entity.Item
import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PENDING
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepository
import com.scr.btg.asynchronous.process.entrypoint.model.api.ItemApiDto
import com.scr.btg.asynchronous.process.entrypoint.model.api.OrderApiDto
import com.scr.btg.asynchronous.process.readResponseBody
import com.scr.btg.asynchronous.process.withJsonBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
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
        val request = OrderApiDto("clientId", listOf(ItemApiDto("item1"), ItemApiDto("item2")))
        val result = mockMvc.perform(
            post("/api/orders")
                .contentType(APPLICATION_JSON)
                .withJsonBody(request, objectMapper)
        ).andExpect(status().isCreated).andReturn()
        val body = result.readResponseBody<OrderApiDto>(objectMapper)
        assertThat(body).isNotNull
        assertThat(body.id).isNotNull
        assertThat(body.clientId).isEqualTo(request.clientId)
        assertThat(body.items).containsExactlyInAnyOrderElementsOf(request.items)
        assertThat(body.status).isEqualTo(PENDING)
    }

    @Test
    fun `create order should return bad request when incorrect payload`() {
        val request = OrderApiDto(" ") // Invalid clientId (blank)
        mockMvc.perform(
            post("/api/orders")
                .contentType(APPLICATION_JSON)
                .withJsonBody(request, objectMapper)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `create order should return bad request when providing non expected field in payload`() {
        val request = OrderApiDto("clientId", listOf(ItemApiDto("item1"), ItemApiDto("item2")), id = "forced_id")
        mockMvc.perform(
            post("/api/orders")
                .contentType(APPLICATION_JSON)
                .withJsonBody(request, objectMapper)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `find order should succeed and return an order`() {
        val order = Order("clientId", listOf(Item("item1"), Item("item2")))
        orderRepository.save(order)
        val result = mockMvc.perform(get("/api/orders/" + order.id))
            .andExpect(status().isOk).andReturn()
        val body = result.readResponseBody<OrderApiDto>(objectMapper)
        assertThat(body).isNotNull
        assertThat(body.id).isEqualTo(order.id)
        assertThat(body.clientId).isEqualTo(order.clientId)
        assertThat(body.items).containsExactlyInAnyOrderElementsOf(order.items.map { ItemApiDto(it.description) })
        assertThat(body.status).isEqualTo(PENDING)
    }
}