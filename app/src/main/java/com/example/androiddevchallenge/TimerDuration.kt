package com.example.androiddevchallenge

inline class TimerDuration(
    private val value: Int // seconds
) {
    constructor(hour: Int, minute: Int, second: Int) : this(
        hour * 3600 + minute * 60 + second
    )

    fun hours() = value / 3600
    fun minutes() = (value - 3600 * hours()) / 60
    fun seconds() = value - 3600 * hours() - 60 * minutes()

    fun updatedHour(value: Int) = TimerDuration(
        value, minutes(), seconds()
    )

    fun updatedMinute(value: Int) = TimerDuration(
        hours(), value, seconds()
    )

    fun updatedSecond(value: Int) = TimerDuration(
        hours(), minutes(), value
    )

    fun countdown(): TimerDuration = TimerDuration(value - 1)

    fun isZero(): Boolean = value == 0
}