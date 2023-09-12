package com.myprojects.reciper.data.relations

import androidx.room.Entity

@Entity(primaryKeys = ["recipeId", "ingredientName"])
data class RecipeIngredientCrossRef(
    val recipeId: Long,
    val ingredientName: String
)
