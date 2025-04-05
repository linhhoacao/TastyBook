package com.linhhoacao.tastybook.model

enum class RecipeCategory(val displayName: String) {
    SALAD("Salad"),
    PASTA("Pasta"),
    RICE("Rice"),
    SOUP("Soup"),
    MAIN_DISH("Main dish"),
    NOODLE("Noodle"),
    DESSERT("Dessert"),
    UNKNOWN("Unknown");

    companion object {
        fun fromString(value: String): RecipeCategory {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}