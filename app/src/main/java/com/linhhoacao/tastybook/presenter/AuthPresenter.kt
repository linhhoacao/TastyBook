package com.linhhoacao.tastybook.presenter

import android.util.Log
import com.linhhoacao.tastybook.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val registrationSuccessful: Boolean = false
)

class AuthPresenter(private val userRepository: UserRepository) {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        updateAuthState()
    }

    private fun updateAuthState() {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser != null) {
            _authState.value = _authState.value.copy(
                isAuthenticated = true,
                displayName = currentUser.displayName ?: "",
                email = currentUser.email ?: "",
                photoUrl = currentUser.photoUrl?.toString(),
                isLoading = false
            )
        } else {
            _authState.value = _authState.value.copy(
                isAuthenticated = false,
                displayName = "",
                email = "",
                photoUrl = null,
                isLoading = false
            )
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true, error = null)

        coroutineScope.launch {
            try {
                val result = userRepository.signIn(email, password)
                if (result.isSuccess) {
                    updateAuthState()
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e("AuthPresenter", "Sign in error", exception)
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = getReadableErrorMessage(exception?.message)
                    )
                }
            } catch (e: Exception) {
                Log.e("AuthPresenter", "Sign in error", e)
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = getReadableErrorMessage(e.message)
                )
            }
        }
    }

    fun signUp(email: String, password: String, name: String = "") {
        _authState.value = _authState.value.copy(isLoading = true, error = null, registrationSuccessful = false)

        coroutineScope.launch {
            try {
                val displayName = name.ifBlank { email.substringBefore("@") }
                val result = userRepository.signUp(email, password, displayName)

                if (result.isSuccess) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        registrationSuccessful = true
                    )
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e("AuthPresenter", "Sign up error", exception)
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = getReadableErrorMessage(exception?.message)
                    )
                }
            } catch (e: Exception) {
                Log.e("AuthPresenter", "Sign up error", e)
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = getReadableErrorMessage(e.message)
                )
            }
        }
    }

    fun signOut() {
        _authState.value = _authState.value.copy(isLoading = true)

        try {
            userRepository.signOut()
            updateAuthState()
        } catch (e: Exception) {
            Log.e("AuthPresenter", "Sign out error", e)
            _authState.value = _authState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }

    fun updateProfile(name: String, photoUrl: String?) {
        _authState.value = _authState.value.copy(isLoading = true, error = null)

        coroutineScope.launch {
            try {
                val result = userRepository.updateProfile(name, photoUrl)
                if (result.isSuccess) {
                    updateAuthState()
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e("AuthPresenter", "Update profile error", exception)
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = exception?.message
                    )
                }
            } catch (e: Exception) {
                Log.e("AuthPresenter", "Update profile error", e)
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun resetRegistrationState() {
        _authState.value = _authState.value.copy(registrationSuccessful = false)
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    private fun getReadableErrorMessage(errorMessage: String?): String {
        return when {
            errorMessage == null -> "Unknown error"
            errorMessage.contains("no user record") -> "Account does not exist"
            errorMessage.contains("password is invalid") -> "Incorrect password"
            errorMessage.contains("email address is badly formatted") -> "Email is not in correct format"
            errorMessage.contains("email address is already in use") -> "This email already in use"
            errorMessage.contains("operation not allowed") -> "This feature is disabled"
            errorMessage.contains("weak password") -> "Password requires at least 6 characters"
            errorMessage.contains("network") -> "Network connection error, please check again"
            else -> errorMessage
        }
    }
}