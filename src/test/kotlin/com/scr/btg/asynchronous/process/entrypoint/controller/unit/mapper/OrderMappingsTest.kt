package com.scr.btg.asynchronous.process.entrypoint.controller.unit.mapper

import com.scr.btg.asynchronous.process.domains.order.model.entity.Item
import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PENDING
import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PROCESSED
import com.scr.btg.asynchronous.process.entrypoint.mapper.toApiDto
import com.scr.btg.asynchronous.process.entrypoint.mapper.toEntity
import com.scr.btg.asynchronous.process.entrypoint.model.api.ItemApiDto
import com.scr.btg.asynchronous.process.entrypoint.model.api.OrderApiDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderMappingsTest {

    @Test
    fun `toEntity should succeed and map only necessary fields`() {
        val dto = OrderApiDto("clientId", listOf(ItemApiDto("item1"), ItemApiDto("item2")), PROCESSED, "myId")
        val order = dto.toEntity()
        assertThat(order).isNotNull
        assertThat(order.clientId).isEqualTo(dto.clientId)
        assertThat(order.items.map { it.description }).containsExactlyInAnyOrderElementsOf(dto.items.map { it.description })
        assertThat(order.status).isEqualTo(PENDING).isNotEqualTo(dto.status)
        assertThat(order.id).isNotNull.isNotEqualTo(dto.id)
    }

    @Test
    fun `toApiDto should succeed and map all fields from entity`() {
        val order = Order("clientId", listOf(Item("item1"), Item("item2")), PROCESSED, "myId")
        val dto = order.toApiDto()
        assertThat(dto).isNotNull
        assertThat(dto.clientId).isEqualTo(order.clientId)
        assertThat(dto.items.map { it.description }).containsExactlyInAnyOrderElementsOf(order.items.map { it.description })
        assertThat(dto.status).isEqualTo(order.status)
        assertThat(dto.id).isEqualTo(order.id)
    }
}