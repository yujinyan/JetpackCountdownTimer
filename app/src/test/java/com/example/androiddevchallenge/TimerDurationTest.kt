package com.example.androiddevchallenge

import org.junit.Test
import org.junit.Assert.assertEquals

class TimerDurationTest {
    @Test
    fun componentsAreCorrect() {
        val d = TimerDuration(3661)
        assertEquals(1, d.hours())
        assertEquals(1, d.minutes())
        assertEquals(1, d.seconds())
    }
}