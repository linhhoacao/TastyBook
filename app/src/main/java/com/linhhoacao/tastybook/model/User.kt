package com.linhhoacao.tastybook.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    @get:Exclude
    var id: String = "",
    val name: String = "",
    val email: String = "",
    val profilePictureUrl: String? = null,
    val favoriteRecipes: List<String> = emptyList()
) {
    constructor() : this(
        id = "",
        name = "",
        email = "",
        profilePictureUrl = null,
        favoriteRecipes = emptyList()
    )

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "email" to email,
            "profilePictureUrl" to profilePictureUrl,
            "favoriteRecipes" to favoriteRecipes
        )
    }
}