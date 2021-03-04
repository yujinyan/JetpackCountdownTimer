/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
