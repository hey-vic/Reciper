package com.myprojects.reciper.data

import kotlinx.coroutines.flow.Flow

interface RecipesRepository {

    suspend fun upsertRecipe(recipe: Recipe)

    suspend fun getRecipeById(id: Int): Recipe?

    fun getRecipesList(): Flow<List<Recipe>>
}