package com.scr.btg.asynchronous.process.domains.order.model.entity

import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PENDING
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class Order(
    @field:NotBlank
    val clientId: String,
    val items: List<Item> = listOf(),
    val status: OrderStatus? = PENDING,
    val id: String = UUID.randomUUID().toString(),
)