package com.linhhoacao.tastybook.presenter

import com.linhhoacao.tastybook.data.repository.RecipeRepository
import com.linhhoacao.tastybook.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class RecipeDetailState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RecipeDetailPresenter(private val recipeRepository: RecipeRepository) {

    private val _recipeState = MutableStateFlow(RecipeDetailState(isLoading = true))
    val recipeState: StateFlow<RecipeDetailState> = _recipeState

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun loadRecipe(recipeId: String) {
        _recipeState.value = RecipeDetailState(isLoading = true)

        coroutineScope.launch {
            try {
                recipeRepository.getRecipeById(recipeId)
                    .catch { e ->
                        _recipeState.value = RecipeDetailState(
                            isLoading = false,
                            error = e.message
                        )
                    }
                    .collect { recipe ->
                        _recipeState.value = RecipeDetailState(
                            recipe = recipe,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _recipeState.value = RecipeDetailState(
                    isLoading = false,
                    error = "Lỗi khi tải dữ liệu: ${e.message}"
                )
            }
        }
    }
}