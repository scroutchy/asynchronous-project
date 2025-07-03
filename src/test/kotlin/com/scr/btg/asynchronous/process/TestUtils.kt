package com.scr.btg.asynchronous.process

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

inline fun <reified T : Any> MockHttpServletRequestBuilder.withJsonBody(
    body: T,
    objectMapper: ObjectMapper
): MockHttpServletRequestBuilder {
    return this
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(body))
}

inline fun <reified T> MvcResult.readResponseBody(objectMapper: ObjectMapper): T {
    return objectMapper.readValue(response.contentAsString, T::class.java)
}