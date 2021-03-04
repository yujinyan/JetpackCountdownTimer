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
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
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
import kotlinx.coroutines.flow.mapLatest
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
        CountDownTimerScreen()
    }
}

class TimerViewModel2 : ViewModel() {
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


val digitStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 72.sp
)

@Composable
fun CountDownTimerScreen(viewModel: TimerViewModel2 = TimerViewModel2()) {
    val (isEditing, setEditing) = remember { mutableStateOf(false) }
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
            CountDownTimer(duration.value, isEditing) {
                viewModel.duration.value = it
            }
        }
        Spacer(Modifier.size(32.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { setEditing(!isEditing) }) {
                Icon(Icons.Default.Edit, contentDescription = "edit")
            }

            if (isStarted.value) IconButton(onClick = { viewModel.stop() }) {
                Icon(Icons.Filled.Pause, contentDescription = "pause")
            }
            else IconButton(onClick = { viewModel.start() }) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "start")
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun CountDownTimer(
    duration: TimerDuration,
    isEditing: Boolean = false,
    setDuration: (TimerDuration) -> Unit,
) {

    CountdownDigit(duration.hours(), range = 0..24, isEditing = isEditing) {
        setDuration(duration.updatedHour(it))
    }
    CountdownDigit(duration.minutes(), isEditing = isEditing) {
        setDuration(duration.updatedMinute(it))
    }
    CountdownDigit(duration.seconds(), isEditing = isEditing) {
        setDuration(duration.updatedSecond(it))
    }
}

@Composable
fun CountdownDigit(
    number: Int,
    range: IntRange = 0..60,
    isEditing: Boolean = false,
    onNumberChange: ((Int) -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = "%02d".format(number)
        IconButton(onClick = { onNumberChange?.invoke((number + 1).coerceAtMost(range.last)) }) {
            Icon(Icons.Filled.ExpandLess, contentDescription = "increase")
        }
        if (isEditing) OutlinedTextField(
            modifier = Modifier.width(120.dp),
            value = text,
            onValueChange = { /*TODO*/ },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = digitStyle
        ) else Crossfade(targetState = text) {
            Text(
                text = text,
                fontFamily = FontFamily.Monospace,
                fontSize = 72.sp
            )
        }
        IconButton(onClick = { onNumberChange?.invoke((number - 1).coerceAtLeast(range.first)) }) {
            Icon(Icons.Default.ExpandMore, contentDescription = "decrease")
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
