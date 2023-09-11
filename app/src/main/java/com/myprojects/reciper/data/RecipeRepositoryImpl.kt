package com.myprojects.reciper.data

import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.data.relations.IngredientWithRecipes
import com.myprojects.reciper.data.relations.RecipeIngredientCrossRef
import com.myprojects.reciper.data.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

class RecipeRepositoryImpl(
    private val dao: RecipeDao
) : RecipeRepository {
    override suspend fun upsertRecipe(recipe: Recipe): Long {
        return dao.upsertRecipe(recipe)
    }

    override suspend fun upsertIngredient(ingredient: Ingredient): Long {
        return dao.upsertIngredient(ingredient)
    }

    override suspend fun upsertRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef) {
        dao.upsertRecipeIngredientCrossRef(crossRef)
    }

    override suspend fun deleteRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef) {
        dao.deleteRecipeIngredientCrossRef(crossRef)
    }

    override suspend fun getRecipeById(id: Long): Recipe? {
        return dao.getRecipeById(id)
    }

    override fun getRecipesList(): Flow<List<Recipe>> {
        return dao.getRecipesList()
    }

    override suspend fun deleteRecipeById(recipeId: Long) {
        dao.deleteRecipeById(recipeId)
    }

    override suspend fun getIngredientsOfRecipeByRecipeId(recipeId: Long): List<RecipeWithIngredients> {
        return dao.getIngredientsOfRecipeByRecipeId(recipeId)
    }

    override fun getRecipesWithIngredientsList(): Flow<List<RecipeWithIngredients>> {
        return dao.getRecipesWithIngredientsList()
    }

    override suspend fun getRecipesOfIngredientByIngredientId(ingredientId: Long): List<IngredientWithRecipes> {
        return dao.getRecipesOfIngredientByIngredientId(ingredientId)
    }

}