package com.scr.btg.asynchronous.process.domains.order.error

import com.scr.btg.asynchronous.process.domains.order.error.OrderErrorReasonCode.ORDER_NOT_FOUND
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class OrderErrorExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(exception: NoSuchElementException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(ORDER_NOT_FOUND, exception.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    data class ErrorResponse(val reasonCode: OrderErrorReasonCode, val message: String?)
}