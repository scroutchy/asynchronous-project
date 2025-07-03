package com.scr.btg.asynchronous.process.entrypoint.model.api

import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus
import com.scr.btg.asynchronous.process.entrypoint.controller.ValidationGroups.OrderRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null

data class OrderApiDto(
    @field:NotBlank(message = "ClientId must not be blank")
    val clientId: String,
    val items: List<ItemApiDto> = listOf(),
    @field:Null(groups = [OrderRequest::class], message = "Status must be null when creating an order")
    val status: OrderStatus? = null,
    @field:Null(groups = [OrderRequest::class], message = "Id must be null when creating an order")
    val id: String? = null
)