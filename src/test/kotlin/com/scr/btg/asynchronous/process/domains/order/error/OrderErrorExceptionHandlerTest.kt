package com.scr.btg.asynchronous.process.domains.order.error

import com.scr.btg.asynchronous.process.domains.order.error.OrderErrorReasonCode.ORDER_NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.NOT_FOUND

class OrderErrorExceptionHandlerTest {

    private val handler = OrderErrorExceptionHandler()

    @Test
    fun `handle NoSuchElementException returns correct response`() {
        val response = handler.handleNoSuchElementException(NoSuchElementException("Test error"))
        assertThat(response.statusCode).isEqualTo(NOT_FOUND)
        assertThat(response.body?.reasonCode).isEqualTo(ORDER_NOT_FOUND)
        assertThat(response.body?.message).isEqualTo("Test error")
    }
}