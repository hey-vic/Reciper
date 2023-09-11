package com.myprojects.reciper.data

import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.data.relations.IngredientWithRecipes
import com.myprojects.reciper.data.relations.RecipeIngredientCrossRef
import com.myprojects.reciper.data.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    suspend fun upsertRecipe(recipe: Recipe): Long

    suspend fun upsertIngredient(ingredient: Ingredient): Long

    suspend fun upsertRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef)

    suspend fun deleteRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef)

    suspend fun getRecipeById(id: Long): Recipe?

    fun getRecipesList(): Flow<List<Recipe>>

    suspend fun deleteRecipeById(recipeId: Long)

    suspend fun getIngredientsOfRecipeByRecipeId(recipeId: Long): List<RecipeWithIngredients>

    fun getRecipesWithIngredientsList(): Flow<List<RecipeWithIngredients>>

    suspend fun getRecipesOfIngredientByIngredientId(ingredientId: Long): List<IngredientWithRecipes>
}