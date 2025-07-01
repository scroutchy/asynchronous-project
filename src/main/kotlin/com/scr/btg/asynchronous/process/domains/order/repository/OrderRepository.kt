package com.scr.btg.asynchronous.process.domains.order.repository

import com.scr.btg.asynchronous.process.domains.order.model.entity.Order

interface OrderRepository {

    fun save(order: Order): Order
    fun findById(id: String): Order?
}