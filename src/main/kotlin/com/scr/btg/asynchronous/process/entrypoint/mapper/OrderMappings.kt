package com.scr.btg.asynchronous.process.entrypoint.mapper

import com.scr.btg.asynchronous.process.domains.order.model.entity.Item
import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import com.scr.btg.asynchronous.process.entrypoint.model.api.ItemApiDto
import com.scr.btg.asynchronous.process.entrypoint.model.api.OrderApiDto

fun OrderApiDto.toEntity() = Order(clientId, items.map { it.toEntity() })

fun Order.toApiDto() = OrderApiDto(clientId, items.map { it.toApiDto() }, status, id)

private fun ItemApiDto.toEntity() = Item(description)

private fun Item.toApiDto() = ItemApiDto(description)