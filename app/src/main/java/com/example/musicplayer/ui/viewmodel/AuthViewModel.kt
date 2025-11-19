package com.example.musicplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(userRepository.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        _currentUser.value = userRepository.currentUser
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            userRepository.signIn(email, password)
                .onSuccess {
                    _currentUser.value = it
                    _authState.value = AuthState.Success
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Authentication failed")
                }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            userRepository.signUp(email, password, displayName)
                .onSuccess {
                    _currentUser.value = it
                    _authState.value = AuthState.Success
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Registration failed")
                }
        }
    }

    fun signOut() {
        userRepository.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Initial
    }

    fun updateProfile(displayName: String?, photo: String?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _photoUrl.value = photo

            userRepository.updateProfile(displayName, photo)
                .onSuccess {
                    _currentUser.value = userRepository.currentUser
                    _authState.value = AuthState.Success
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Profile update failed")
                }
        }
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
