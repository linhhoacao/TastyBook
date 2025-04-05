package com.linhhoacao.tastybook.presenter

import android.net.Uri
import com.linhhoacao.tastybook.data.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddRecipePresenter(private val recipeRepository: RecipeRepository) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun addRecipe(
        name: String,
        category: String,
        ingredients: String,
        preparationTime: Int,
        preparation: String,
        imageUri: Uri?
    ) {
        if (name.isBlank()) {
            _error.value = "Vui lòng nhập tên công thức"
            return
        }

        if (category.isBlank()) {
            _error.value = "Vui lòng chọn danh mục"
            return
        }

        if (imageUri == null) {
            _error.value = "Vui lòng chọn ảnh cho công thức"
            return
        }

        _isLoading.value = true
        _error.value = null

        coroutineScope.launch {
            try {
                val result = recipeRepository.addRecipe(
                    name = name,
                    category = category,
                    ingredientsText = ingredients,
                    preparationTime = preparationTime,
                    preparationText = preparation,
                    imageUri = imageUri
                )

                if (result.isSuccess) {
                    _success.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Lỗi không xác định"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi thêm công thức: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _isLoading.value = false
        _error.value = null
        _success.value = false
    }

    fun clearError() {
        _error.value = null
    }
}