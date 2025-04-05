package com.linhhoacao.tastybook.presenter

import com.linhhoacao.tastybook.data.repository.RecipeRepository
import com.linhhoacao.tastybook.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchPresenter(private val recipeRepository: RecipeRepository) {

    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val allRecipes: StateFlow<List<Recipe>> = _allRecipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun searchRecipes(query: String) {
        _allRecipes.value = emptyList()
        _isLoading.value = true
        _error.value = null

        coroutineScope.launch {
            try {
                if (query.isBlank()) {
                    _allRecipes.value = emptyList()
                    _isLoading.value = false
                    return@launch
                }

                recipeRepository.getAllRecipes()
                    .catch { e ->
                        _error.value = "Lỗi khi tải recipes: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { recipes ->
                        val filteredRecipes = recipes.filter { recipe ->
                            val lowercaseQuery = query.lowercase().trim()
                            val lowercaseName = recipe.name.lowercase().trim()
                            val lowercaseCategory = recipe.category.lowercase().trim()

                            val nameMatchPercentage = calculateMatchPercentage(lowercaseName, lowercaseQuery)
                            val categoryMatchPercentage = calculateMatchPercentage(lowercaseCategory, lowercaseQuery)

                            nameMatchPercentage >= 0.7 || categoryMatchPercentage >= 0.7
                        }

                        _allRecipes.value = filteredRecipes
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Lỗi ngoại lệ: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun calculateMatchPercentage(source: String, query: String): Double {
        if (query.isEmpty()) return 0.0

        val sourceWords = source.split(" ")
        val queryWords = query.split(" ")

        val matchedWords = queryWords.count { queryWord ->
            sourceWords.any { it.contains(queryWord) }
        }

        return matchedWords.toDouble() / queryWords.size.toDouble()
    }

    fun deleteRecipe(recipeId: String, recipeName: String) {
        _isLoading.value = true
        coroutineScope.launch {
            try {
                val result = recipeRepository.deleteRecipe(recipeId)
                if (result.isSuccess) {
                    //
                } else {
                    _error.value = result.exceptionOrNull()?.message
                        ?: "Không thể xóa công thức"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSearchResults() {
        _allRecipes.value = emptyList()
        _isLoading.value = false
    }
}