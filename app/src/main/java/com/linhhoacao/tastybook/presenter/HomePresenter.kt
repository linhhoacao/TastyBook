package com.linhhoacao.tastybook.presenter

import com.linhhoacao.tastybook.data.repository.RecipeRepository
import com.linhhoacao.tastybook.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomePresenter(private val recipeRepository: RecipeRepository) {

    private val _newRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val newRecipes: StateFlow<List<Recipe>> = _newRecipes

    private val _popularRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val popularRecipes: StateFlow<List<Recipe>> = _popularRecipes

    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val allRecipes: StateFlow<List<Recipe>> = _allRecipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun loadRecipes() {
        _isLoading.value = true

        coroutineScope.launch {
            try {
                recipeRepository.getNewRecipes()
                    .catch { e ->
                        _error.value = "Lỗi khi tải new recipes: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { recipes ->
                        _newRecipes.value = recipes
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Lỗi ngoại lệ: ${e.message}"
                _isLoading.value = false
            }
        }

        coroutineScope.launch {
            try {
                recipeRepository.getPopularRecipes()
                    .catch { e ->
                        _error.value = "Lỗi khi tải popular recipes: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { recipes ->
                        _popularRecipes.value = recipes
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Lỗi ngoại lệ: ${e.message}"
                _isLoading.value = false
            }
        }

        coroutineScope.launch {
            try {
                recipeRepository.getAllRecipes()
                    .catch { e ->
                        _error.value = "Lỗi khi tải popular recipes: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { recipes ->
                        _allRecipes.value = recipes
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Lỗi ngoại lệ: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun deleteRecipe(recipeId: String) {
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
}