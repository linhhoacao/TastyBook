package com.linhhoacao.tastybook.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.linhhoacao.tastybook.model.User
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Đăng nhập thất bại")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, name: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Đăng ký thất bại")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            firebaseUser.updateProfile(profileUpdates).await()

            val userData = hashMapOf<String, Any?>(
                "name" to name,
                "email" to email,
                "profilePictureUrl" to null,
                "favoriteRecipes" to emptyList<String>()
            )

            Log.d("UserRepository", "Đăng ký user data: $userData")

            usersRef.child(firebaseUser.uid).setValue(userData).await()

            auth.signOut()

            Result.success(firebaseUser.uid)
        } catch (e: Exception) {
            Log.e("UserRepository", "Lỗi đăng ký: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


    suspend fun updateProfile(name: String, photoUrl: String?): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Người dùng chưa đăng nhập"))

        return try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)

            if (photoUrl != null) {
                profileUpdates.setPhotoUri(photoUrl.toUri())
            }

            user.updateProfile(profileUpdates.build()).await()

            val updates = hashMapOf<String, Any?>()
            updates["name"] = name
            updates["profilePictureUrl"] = photoUrl

            usersRef.child(user.uid).updateChildren(updates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Lỗi cập nhật hồ sơ: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getUserData(userId: String): Result<User> {
        return try {
            val snapshot = usersRef.child(userId).get().await()

            Log.d("UserRepository", "User data raw: ${snapshot.value}")

            val name = snapshot.child("name").getValue(String::class.java) ?: ""
            val email = snapshot.child("email").getValue(String::class.java) ?: ""
            val profilePictureUrl = snapshot.child("profilePictureUrl").getValue(String::class.java)

            val favoriteRecipesSnapshot = snapshot.child("favoriteRecipes")
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

            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Lỗi lấy dữ liệu người dùng: ${e.message}", e)
            Result.failure(e)
        }
    }
}