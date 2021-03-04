# Jetpack CountdownTimer

<!--- Replace <OWNER> with your Github Username and <REPOSITORY> with the name of your repository. -->
<!--- You can find both of these in the url bar when you open your repository in github. -->
![Workflow result](https://github.com/yujinyan/JetpackCountdownTimer/workflows/Check/badge.svg)

## :scroll: Description

<!--- Describe your app in one or two sentences -->
This is a demo countdown timer app to showcase state management and animation in Jetpack Compose.

## :bulb: Motivation and Context

<!--- Optionally point readers to interesting parts of your submission. -->
<!--- What are you especially proud of? -->

### Statement management

The main state of the app is a custom `TimerDuration` object, hoisted all the way up to a view model.

`TimerDuration` is a Kotlin inline class that packs up hour, minute and second digits.

Inside the view model, an actor (from the Kotlin coroutines library) is used to count down `TimerDuration`.

### Animation

`AnimatedVisibility` is used to slide-in new value and slide-out old one.

Currently the animation is a bit bumpy. I'm tuning this in a separate PR.

## :camera_flash: Screenshots

<!-- You can add more screenshots here if you like -->
<img src="/results/screenshot_1.png" width="260">
&emsp;<img src="/results/screenshot_2.png" width="260">

## License

```
Copyright 2020 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
