package com.scr.btg.asynchronous.process.entrypoint.controller.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.scr.btg.asynchronous.process.AbstractIntegrationTest
import com.scr.btg.asynchronous.process.TestMockConfig
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PROCESSED
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepository
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepositoryImpl
import com.scr.btg.asynchronous.process.entrypoint.controller.ApiConstants.ORDER_PATH
import com.scr.btg.asynchronous.process.entrypoint.model.api.ItemApiDto
import com.scr.btg.asynchronous.process.entrypoint.model.api.OrderApiDto
import com.scr.btg.asynchronous.process.withJsonBody
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.concurrent.TimeUnit.SECONDS

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestMockConfig::class)
class OrderRetryIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val orderRepository: OrderRepository
) : AbstractIntegrationTest() {

    @Test
    fun `kafka consumer should try 3 times when failure in status update process`() {
        assertThat(orderRepository).isNotExactlyInstanceOf(OrderRepositoryImpl::class.java)
        val request = OrderApiDto("clientId", listOf(ItemApiDto("item1"), ItemApiDto("item2")))
        mockMvc.perform(
            post(ORDER_PATH)
                .withJsonBody(request, objectMapper)
        ).andExpect(status().isCreated).andReturn()

        await().atMost(30, SECONDS).untilAsserted {
            verify(exactly = 3) { orderRepository.save(match { it.status == PROCESSED }) }
        }
    }
}