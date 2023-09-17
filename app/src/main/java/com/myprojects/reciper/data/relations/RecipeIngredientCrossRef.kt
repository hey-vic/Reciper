package com.myprojects.reciper.data.relations

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["recipeId", "ingredientName"])
data class RecipeIngredientCrossRef(
    val recipeId: Long,
    @ColumnInfo(index = true) val ingredientName: String
)
