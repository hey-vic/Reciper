package com.myprojects.reciper.data

import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.data.relations.IngredientWithRecipes
import com.myprojects.reciper.data.relations.RecipeIngredientCrossRef
import com.myprojects.reciper.data.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    suspend fun upsertRecipe(recipe: Recipe): Long

    suspend fun upsertIngredient(ingredient: Ingredient)

    suspend fun upsertRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef)

    suspend fun deleteRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef)

    suspend fun getRecipeById(id: Long): Recipe?

    fun getRecipesList(): Flow<List<Recipe>>

    suspend fun deleteRecipeById(recipeId: Long)

    suspend fun getIngredientsOfRecipeByRecipeId(recipeId: Long): List<RecipeWithIngredients>

    fun getRecipesWithIngredientsList(): Flow<List<RecipeWithIngredients>>

    suspend fun getRecipesOfIngredientByIngredientName(ingredientName: String): List<IngredientWithRecipes>

    suspend fun upsertRecipeWithIngredients(
        recipe: Recipe,
        previouslySavedIngredients: List<Ingredient>,
        currentIngredients: List<Ingredient>
    )

    suspend fun deleteRecipeWithIngredients(
        recipe: Recipe,
        ingredients: List<Ingredient>
    )
}