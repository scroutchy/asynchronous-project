package com.scr.btg.asynchronous.process

import org.junit.jupiter.api.ClassDescriptor
import org.junit.jupiter.api.ClassOrderer
import org.junit.jupiter.api.ClassOrdererContext
import org.springframework.boot.test.context.SpringBootTest

class TestClassOrderer : ClassOrderer {

    override fun orderClasses(context: ClassOrdererContext?) {
        context?.classDescriptors?.sortBy { weight(it) }
    }

    private fun weight(classDescriptor: ClassDescriptor): Int {
        return when {
            classDescriptor.isAnnotated(SpringBootTest::class.java) -> 2
            else -> 1
        }
    }
}