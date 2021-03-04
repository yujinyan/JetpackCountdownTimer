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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        CountdownTimerScreen()
    }
}

class TimerViewModel : ViewModel() {
    val duration = MutableStateFlow(TimerDuration(0, 0, 0))
    val started = MutableStateFlow(false)

    sealed class Message {
        object Start : Message()
        object Stop : Message()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun CoroutineScope.actor() = actor<Message> {
        var job: Job? = null
        for (msg in channel) {
            when (msg) {
                Message.Start -> {
                    job = launch {
                        while (!duration.value.isZero()) {
                            duration.value = duration.value.countdown()
                            if (duration.value.isZero()) break
                            delay(1000)
                        }
                        started.value = false
                    }
                }
                Message.Stop -> job?.cancel()
            }
        }
    }

    private val timer = viewModelScope.actor()

    fun start() {
        if (!duration.value.isZero()) {
            timer.offer(Message.Start)
            started.value = true
        }
    }

    fun stop() {
        timer.offer(Message.Stop)
        started.value = false
    }
}

@Composable
fun CountdownTimerScreen(viewModel: TimerViewModel = TimerViewModel()) {
    val duration = viewModel.duration.collectAsState()
    val isStarted = viewModel.started.collectAsState(false)

    Column(
        Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CountdownTimer(duration.value) {
                viewModel.duration.value = it
            }
        }
        Spacer(Modifier.size(32.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val modifier = Modifier.size(72.dp)

            if (isStarted.value) IconButton(onClick = { viewModel.stop() }) {
                Icon(Icons.Filled.Pause, contentDescription = "pause", modifier = modifier)
            }
            else IconButton(onClick = { viewModel.start() }) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "start", modifier = modifier)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun CountdownTimer(
    duration: TimerDuration,
    setDuration: (TimerDuration) -> Unit,
) {
    CountdownDigit(duration.hours(), range = 0..24) {
        setDuration(duration.updatedHour(it))
    }
    CountdownDigit(duration.minutes()) {
        setDuration(duration.updatedMinute(it))
    }
    CountdownDigit(duration.seconds()) {
        setDuration(duration.updatedSecond(it))
    }
}

@Composable
fun CountdownDigit(
    number: Int,
    range: IntRange = 0..60,
    onNumberChange: ((Int) -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { onNumberChange?.invoke((number + 1).coerceAtMost(range.last)) }) {
            Icon(Icons.Filled.ExpandLess, contentDescription = "increase")
        }
        AnimatedDigit(number = number)
        IconButton(onClick = { onNumberChange?.invoke((number - 1).coerceAtLeast(range.first)) }) {
            Icon(Icons.Default.ExpandMore, contentDescription = "decrease")
        }
    }
}

private val digitFormat = "%02d"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedDigit(number: Int) {
    val (prevNumber, setNumber) = remember { mutableStateOf(number) }
    LaunchedEffect(key1 = number, block = { setNumber(number) })

    Box {
        AnimatedVisibility(
            visible = number != prevNumber,
            enter = slideInVertically()
        ) {
            Text(
                text = digitFormat.format(number),
                fontFamily = FontFamily.Monospace,
                fontSize = 72.sp
            )
        }
        AnimatedVisibility(
            visible = number == prevNumber,
            exit = slideOutVertically()
        ) {
            Text(
                text = digitFormat.format(prevNumber),
                fontFamily = FontFamily.Monospace,
                fontSize = 72.sp
            )
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
