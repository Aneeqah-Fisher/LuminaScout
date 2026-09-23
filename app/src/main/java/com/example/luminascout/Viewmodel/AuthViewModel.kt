package com.example.luminascout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luminascout.data.AuthState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun registerUser(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Simulating API network call delay (Replace with Retrofit / API call)
            delay(1500)

            if (email.contains("@")) {
                _authState.value = AuthState.Success("Account created successfully!")
            } else {
                _authState.value = AuthState.Error("Invalid email address")
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Simulating API network call delay
            delay(1500)

            if (email.isNotBlank() && password.length >= 6) {
                _authState.value = AuthState.Success("Welcome back!")
            } else {
                _authState.value = AuthState.Error("Invalid credentials")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}