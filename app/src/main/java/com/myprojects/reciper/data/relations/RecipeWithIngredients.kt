package com.myprojects.reciper.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe

data class RecipeWithIngredients(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "ingredientName",
        associateBy = Junction(RecipeIngredientCrossRef::class)
    )
    val ingredients: List<Ingredient>
)