package com.linhhoacao.tastybook.presenter

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.UserProfileChangeRequest
import com.linhhoacao.tastybook.data.repository.UserRepository
import com.linhhoacao.tastybook.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null,
    val allUsers: List<User> = emptyList()
)

class UserPresenter(private val userRepository: UserRepository) {
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun loadCurrentUserByEmail() {
        _userState.value = _userState.value.copy(isLoading = true, error = null)

        coroutineScope.launch {
            try {
                val firebaseUser = userRepository.getCurrentUser()
                if (firebaseUser == null) {
                    _userState.value = _userState.value.copy(
                        isLoading = false,
                        error = "Chưa đăng nhập"
                    )
                    return@launch
                }

                val currentUserEmail = firebaseUser.email
                if (currentUserEmail.isNullOrEmpty()) {
                    _userState.value = _userState.value.copy(
                        isLoading = false,
                        error = "Email không hợp lệ"
                    )
                    return@launch
                }

                val allUsersResult = getAllUsers()
                if (allUsersResult.isSuccess) {
                    val allUsers = allUsersResult.getOrNull() ?: emptyList()

                    val matchingUser = allUsers.find { it.email == currentUserEmail }

                    if (matchingUser != null) {
                        _userState.value = _userState.value.copy(
                            isLoading = false,
                            currentUser = matchingUser,
                            allUsers = allUsers,
                            error = null
                        )
                    } else {
                        _userState.value = _userState.value.copy(
                            isLoading = false,
                            error = "Không tìm thấy thông tin người dùng"
                        )
                    }
                } else {
                    _userState.value = _userState.value.copy(
                        isLoading = false,
                        error = allUsersResult.exceptionOrNull()?.message
                    )
                }
            } catch (e: Exception) {
                Log.e("UserPresenter", "Lỗi tải thông tin người dùng", e)
                _userState.value = _userState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val userList = mutableListOf<User>()
            val snapshot = userRepository.database.getReference("users").get().await()

            for (childSnapshot in snapshot.children) {
                val userId = childSnapshot.key ?: continue
                val name = childSnapshot.child("name").getValue(String::class.java) ?: ""
                val email = childSnapshot.child("email").getValue(String::class.java) ?: ""
                val profilePictureUrl = childSnapshot.child("profilePictureUrl").getValue(String::class.java)

                val favoriteRecipesSnapshot = childSnapshot.child("favoriteRecipes")
                val favoriteRecipes = mutableListOf<String>()

                for (recipeSnapshot in favoriteRecipesSnapshot.children) {
                    val recipeId = recipeSnapshot.getValue(String::class.java)
                    if (recipeId != null) {
                        favoriteRecipes.add(recipeId)
                    }
                }

                val user = User(
                    id = userId,
                    name = name,
                    email = email,
                    profilePictureUrl = profilePictureUrl,
                    favoriteRecipes = favoriteRecipes
                )

                userList.add(user)
            }

            Result.success(userList)
        } catch (e: Exception) {
            Log.e("UserPresenter", "Lỗi lấy danh sách người dùng", e)
            Result.failure(e)
        }
    }

    fun clearError() {
        _userState.value = _userState.value.copy(error = null)
    }

    suspend fun updateUserProfile(name: String, photoUrl: String?): Result<Unit> {
        return try {
            val currentUser = userRepository.getCurrentUser()
                ?: return Result.failure(Exception("Người dùng chưa đăng nhập"))

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .apply {
                    photoUrl?.let { photoUri = it.toUri() }
                }
                .build()

            currentUser.updateProfile(profileUpdates).await()

            val userRef = userRepository.database.getReference("users").child(currentUser.uid)
            val updates = mutableMapOf<String, Any>()
            updates["name"] = name
            photoUrl?.let { updates["profilePictureUrl"] = it }

            userRef.updateChildren(updates).await()

            loadCurrentUserByEmail()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserPresenter", "Lỗi cập nhật hồ sơ người dùng", e)
            Result.failure(e)
        }
    }
}