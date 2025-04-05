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
            errorMessage == null -> "Đã xảy ra lỗi không xác định"
            errorMessage.contains("no user record") -> "Tài khoản không tồn tại"
            errorMessage.contains("password is invalid") -> "Mật khẩu không chính xác"
            errorMessage.contains("email address is badly formatted") -> "Email không đúng định dạng"
            errorMessage.contains("email address is already in use") -> "Email này đã được sử dụng"
            errorMessage.contains("operation not allowed") -> "Tính năng này đang bị vô hiệu hóa"
            errorMessage.contains("weak password") -> "Mật khẩu quá yếu, cần ít nhất 6 ký tự"
            errorMessage.contains("network") -> "Lỗi kết nối mạng, vui lòng kiểm tra lại"
            else -> errorMessage
        }
    }
}