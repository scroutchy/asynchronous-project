package com.scr.btg.asynchronous.process

import com.scr.btg.asynchronous.process.domains.order.model.entity.OrderStatus.PROCESSED
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepository
import com.scr.btg.asynchronous.process.domains.order.repository.OrderRepositoryImpl
import io.mockk.every
import io.mockk.spyk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestMockConfig {

    @Bean
    fun orderRepository(): OrderRepository {
        val realRepo = OrderRepositoryImpl()
        val spyRepo = spyk(realRepo)
        every { spyRepo.save(match { it.status == PROCESSED }) } throws RuntimeException("Simulated failure")
        return spyRepo
    }
}