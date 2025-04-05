package com.linhhoacao.tastybook.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.linhhoacao.tastybook.model.Recipe
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RecipeRepository {
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val recipesRef by lazy { database.getReference("recipes") }

    fun getAllRecipes(): Flow<List<Recipe>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipes = mutableListOf<Recipe>()
                for (recipeSnapshot in snapshot.children) {
                    val recipe = recipeSnapshot.getValue(Recipe::class.java)
                    recipe?.id = recipeSnapshot.key ?: ""
                    recipe?.let { recipes.add(it) }
                }
                trySend(recipes)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        recipesRef.addValueEventListener(listener)

        awaitClose {
            recipesRef.removeEventListener(listener)
        }
    }

    fun getNewRecipes(): Flow<List<Recipe>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipes = mutableListOf<Recipe>()
                for (recipeSnapshot in snapshot.children) {
                    val recipe = recipeSnapshot.getValue(Recipe::class.java)
                    if (recipe != null) {
                        recipe.id = recipeSnapshot.key ?: ""
                        recipes.add(recipe)
                    }
                }

                val sortedRecipes = recipes.sortedByDescending {
                    try {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                        dateFormat.parse(it.createdDate)
                    } catch (e: Exception) {
                        Date(0)
                    }
                }

                trySend(sortedRecipes)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        recipesRef.orderByChild("isNew").equalTo(true).addValueEventListener(listener)

        awaitClose {
            recipesRef.orderByChild("isNew").equalTo(true).removeEventListener(listener)
        }
    }

    fun getPopularRecipes(): Flow<List<Recipe>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipes = mutableListOf<Recipe>()
                for (recipeSnapshot in snapshot.children) {
                    val recipe = recipeSnapshot.getValue(Recipe::class.java)
                    if (recipe?.isPopular == true) {
                        recipe.id = recipeSnapshot.key ?: ""
                        recipes.add(recipe)
                    }
                }
                trySend(recipes)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        recipesRef.addValueEventListener(listener)

        awaitClose {
            recipesRef.removeEventListener(listener)
        }
    }

    fun getRecipeById(recipeId: String): Flow<Recipe?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipe = snapshot.getValue(Recipe::class.java)
                recipe?.id = snapshot.key ?: ""
                trySend(recipe)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        recipesRef.child(recipeId).addValueEventListener(listener)

        awaitClose {
            recipesRef.child(recipeId).removeEventListener(listener)
        }
    }

    fun getRecipesByCategory(category: String): Flow<List<Recipe>> = callbackFlow {
        val query = recipesRef.orderByChild("category").equalTo(category)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipes = mutableListOf<Recipe>()
                for (recipeSnapshot in snapshot.children) {
                    val recipe = recipeSnapshot.getValue(Recipe::class.java)
                    recipe?.id = recipeSnapshot.key ?: ""
                    recipe?.let { recipes.add(it) }
                }
                trySend(recipes)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)

        awaitClose {
            query.removeEventListener(listener)
        }
    }

    suspend fun deleteRecipe(recipeId: String): Result<Unit> {
        return try {
            val recipeSnapshot = recipesRef.child(recipeId).get().await()
            val imageUrl = recipeSnapshot.child("imageUrl").getValue(String::class.java)

            imageUrl?.let { url ->
                try {
                    storage.getReferenceFromUrl(url).delete().await()
                } catch (e: Exception) {
                    Log.e("RecipeRepository", "Lỗi xóa ảnh: ${e.message}")
                }
            }

            recipesRef.child(recipeId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addRecipe(
        name: String,
        category: String,
        ingredientsText: String,
        preparationTime: Int,
        preparationText: String,
        imageUri: Uri
    ): Result<String> {
        return try {
            val recipeId = recipesRef.push().key ?: return Result.failure(Exception("Không thể tạo ID"))

            val storageRef = storage.reference.child("recipes/${recipeId}_${System.currentTimeMillis()}")
            storageRef.putFile(imageUri).await()
            val imageUrl = storageRef.downloadUrl.await().toString()

            val ingredients = ingredientsText.split("\n").filter { it.isNotBlank() }
            val instructions = preparationText.split("\n").filter { it.isNotBlank() }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val currentDate = dateFormat.format(Date())

            val recipeMap = hashMapOf(
                "id" to recipeId,
                "name" to name,
                "category" to category,
                "imageUrl" to imageUrl,
                "ingredients" to ingredients,
                "instructions" to instructions,
                "preparationTime" to preparationTime,
                "cookingTime" to 0,
                "servings" to 4,
                "calories" to 320,
                "isNew" to true,
                "isPopular" to true,
                "createdDate" to currentDate,
                "updatedDate" to currentDate
            )

            recipesRef.child(recipeId).setValue(recipeMap).await()

            Result.success(recipeId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}