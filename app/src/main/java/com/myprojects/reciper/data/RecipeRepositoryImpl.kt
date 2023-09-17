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

    override suspend fun upsertIngredient(ingredient: Ingredient) {
        dao.upsertIngredient(ingredient)
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

    override suspend fun getRecipeByTitle(title: String): Recipe? {
        return dao.getRecipeByTitle(title)
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return dao.getAllRecipes()
    }

    override suspend fun deleteRecipeById(recipeId: Long) {
        dao.deleteRecipeById(recipeId)
    }

    override suspend fun getIngredientsOfRecipeByRecipeId(recipeId: Long): List<RecipeWithIngredients> {
        return dao.getIngredientsOfRecipeByRecipeId(recipeId)
    }

    override fun getAllRecipesWithIngredients(): Flow<List<RecipeWithIngredients>> {
        return dao.getAllRecipesWithIngredients()
    }

    override fun getAllRecipesWithIngredientsByOptions(
        titleQuery: String, detailsQuery: String, favouritesOnly: Boolean
    ): Flow<List<RecipeWithIngredients>> {
        return dao.getAllRecipesWithIngredientsByOptions(titleQuery, detailsQuery, favouritesOnly)
    }

    override suspend fun getRecipesOfIngredientByIngredientName(ingredientName: String): List<IngredientWithRecipes> {
        return dao.getRecipesOfIngredientByIngredientName(ingredientName)
    }

    override suspend fun upsertRecipeWithIngredients(
        recipe: Recipe,
        previouslySavedIngredients: List<Ingredient>,
        currentIngredients: List<Ingredient>
    ) {
        return dao.upsertRecipeWithIngredients(
            recipe,
            previouslySavedIngredients,
            currentIngredients
        )
    }

    override suspend fun deleteRecipeWithIngredients(
        recipe: Recipe,
        ingredients: List<Ingredient>
    ) {
        return dao.deleteRecipeWithIngredients(recipe, ingredients)
    }
}