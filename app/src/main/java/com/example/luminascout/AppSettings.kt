package com.example.luminascout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppSettings {

    var temperatureUnit by mutableStateOf("°C")
}