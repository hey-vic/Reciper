package com.myprojects.reciper.data

import kotlinx.coroutines.flow.Flow

class RecipesRepositoryImpl(
    private val dao: RecipeDao
) : RecipesRepository {
    override suspend fun upsertRecipe(recipe: Recipe) {
        dao.upsertRecipe(recipe)
    }

    override suspend fun getRecipeById(id: Int): Recipe? {
        return dao.getRecipeById(id)
    }

    override fun getRecipesList(): Flow<List<Recipe>> {
        return dao.getRecipesList()
    }

    override suspend fun deleteRecipeById(recipeId: Int) {
        dao.deleteRecipeById(recipeId)
    }
}