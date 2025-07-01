package com.scr.btg.asynchronous.process.domains.order.repository

import com.scr.btg.asynchronous.process.domains.order.model.entity.Order
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class OrderRepositoryImpl : OrderRepository {

    private val orders: ConcurrentHashMap<String, Order> = ConcurrentHashMap()

    override fun save(order: Order): Order {
        orders[order.id] = order
        return order
    }

    override fun findById(id: String): Order? {
        return orders[id]
    }
}