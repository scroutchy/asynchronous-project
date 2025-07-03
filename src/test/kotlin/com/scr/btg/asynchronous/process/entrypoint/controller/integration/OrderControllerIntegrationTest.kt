package com.scr.btg.asynchronous.process.entrypoint.controller.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.scr.btg.asynchronous.process.AbstractIntegrationTest
import com.scr.btg.asynchronous.process.domains.order.model.entity.Item
import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PENDING
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PROCESSED
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepository
import com.scr.btg.asynchronous.process.entrypoint.controller.ApiConstants.ORDER_PATH
import com.scr.btg.asynchronous.process.entrypoint.model.api.ItemApiDto
import com.scr.btg.asynchronous.process.entrypoint.model.api.OrderApiDto
import com.scr.btg.asynchronous.process.readResponseBody
import com.scr.btg.asynchronous.process.withJsonBody
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.concurrent.TimeUnit.SECONDS

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val orderRepository: OrderRepository,
) : AbstractIntegrationTest() {

    @Test
    fun `create order should succeed and create order in map`() {
        val request = OrderApiDto("clientId", listOf(ItemApiDto("item1"), ItemApiDto("item2")))
        val result = mockMvc.perform(
            post(ORDER_PATH)
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
            post(ORDER_PATH)
                .withJsonBody(request, objectMapper)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `create order should return bad request when providing non expected field in payload`() {
        val request = OrderApiDto("clientId", listOf(ItemApiDto("item1"), ItemApiDto("item2")), id = "forced_id")
        mockMvc.perform(
            post(ORDER_PATH)
                .withJsonBody(request, objectMapper)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `find order should succeed and return an order`() {
        val order = Order("clientId", listOf(Item("item1"), Item("item2")))
        orderRepository.save(order)
        val result = mockMvc.perform(get("$ORDER_PATH/${order.id}"))
            .andExpect(status().isOk).andReturn()
        val body = result.readResponseBody<OrderApiDto>(objectMapper)
        assertThat(body).isNotNull
        assertThat(body.id).isEqualTo(order.id)
        assertThat(body.clientId).isEqualTo(order.clientId)
        assertThat(body.items).containsExactlyInAnyOrderElementsOf(order.items.map { ItemApiDto(it.description) })
        assertThat(body.status).isEqualTo(PENDING)
    }

    @Test
    fun `create order should succeed and be processed after some delay`() {
        val request = OrderApiDto("clientId", listOf(ItemApiDto("item1"), ItemApiDto("item2")))
        val creationResult = mockMvc.perform(
            post(ORDER_PATH)
                .withJsonBody(request, objectMapper)
        ).andExpect(status().isCreated).andReturn()
        val creationResponseBody = creationResult.readResponseBody<OrderApiDto>(objectMapper)
        assertThat(creationResponseBody).isNotNull
        assertThat(creationResponseBody.id).isNotNull
        assertThat(creationResponseBody.clientId).isEqualTo(request.clientId)
        assertThat(creationResponseBody.items).containsExactlyInAnyOrderElementsOf(request.items)
        assertThat(creationResponseBody.status).isEqualTo(PENDING)

        await().atMost(30, SECONDS).untilAsserted {
            val consultationResult = mockMvc.perform(get("$ORDER_PATH/${creationResponseBody.id}"))
                .andExpect(status().isOk).andReturn()
            val consultationResponseBody = consultationResult.readResponseBody<OrderApiDto>(objectMapper)
            assertThat(consultationResponseBody).isNotNull
            assertThat(consultationResponseBody.id).isEqualTo(creationResponseBody.id)
            assertThat(consultationResponseBody.clientId).isEqualTo(request.clientId)
            assertThat(consultationResponseBody.items).containsExactlyInAnyOrderElementsOf(request.items)
            assertThat(consultationResponseBody.status).isEqualTo(PROCESSED)
        }
    }
}