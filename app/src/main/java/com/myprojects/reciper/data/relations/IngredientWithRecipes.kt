package com.myprojects.reciper.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe

data class IngredientWithRecipes(
    @Embedded val ingredient: Ingredient,
    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "recipeId",
        associateBy = Junction(RecipeIngredientCrossRef::class)
    )
    val recipes: List<Recipe>
)
