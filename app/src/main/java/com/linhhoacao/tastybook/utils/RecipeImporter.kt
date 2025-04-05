package com.linhhoacao.tastybook.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.linhhoacao.tastybook.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

object RecipeImporter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

    suspend fun importRecipesFromJson(context: Context, jsonFileName: String): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open(jsonFileName).bufferedReader().use { it.readText() }
            val gson = Gson()
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
            val recipesArray = jsonObject.getAsJsonArray("recipes")

            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val recipesList: List<Map<String, Any>> = gson.fromJson(recipesArray, type)

            recipesList.map { recipeMap ->
                val createdDateStr = recipeMap["createdDate"] as? String ?: ""
                val updatedDateStr = recipeMap["updatedDate"] as? String ?: ""

                val ingredientsType = object : TypeToken<List<String>>() {}.type
                val instructionsType = object : TypeToken<List<String>>() {}.type

                Recipe(
                    id = recipeMap["id"] as String,
                    name = recipeMap["name"] as String,
                    category = recipeMap["category"] as String,
                    imageUrl = recipeMap["imageUrl"] as String,
                    ingredients = gson.fromJson(gson.toJson(recipeMap["ingredients"]), ingredientsType),
                    instructions = gson.fromJson(gson.toJson(recipeMap["instructions"]), instructionsType),
                    preparationTime = (recipeMap["preparationTime"] as Double).toInt(),
                    cookingTime = (recipeMap["cookingTime"] as Double).toInt(),
                    servings = (recipeMap["servings"] as Double).toInt(),
                    calories = (recipeMap["calories"] as Double).toInt(),
                    isNew = recipeMap["isNew"] as Boolean,
                    isPopular = recipeMap["isPopular"] as Boolean,
                    createdDate = createdDateStr,
                    updatedDate = updatedDateStr
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}