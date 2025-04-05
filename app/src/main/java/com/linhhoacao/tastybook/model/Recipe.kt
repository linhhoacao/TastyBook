package com.linhhoacao.tastybook.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Recipe(
    @get:Exclude
    var id: String = "",
    val name: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val preparationTime: Int = 0,
    val cookingTime: Int = 0,
    val servings: Int = 0,
    val calories: Int = 0,
    val isNew: Boolean = false,
    val isPopular: Boolean = false,
    val createdDate: String = "",
    val updatedDate: String = ""
) {
    constructor() : this(
        id = "",
        name = "",
        category = "",
        imageUrl = "",
        ingredients = emptyList(),
        instructions = emptyList(),
        preparationTime = 0,
        cookingTime = 0,
        servings = 0,
        calories = 0,
        isNew = false,
        isPopular = false,
        createdDate = "",
        updatedDate = ""
    )
}