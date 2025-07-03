package com.scr.btg.asynchronous.process.domains.order.messaging

interface OrderCreationEventProducer {

    fun sendOrderCreationEvent(orderId: String)
}