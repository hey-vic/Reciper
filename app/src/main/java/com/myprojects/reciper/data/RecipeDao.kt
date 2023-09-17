package com.myprojects.reciper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.data.relations.IngredientWithRecipes
import com.myprojects.reciper.data.relations.RecipeIngredientCrossRef
import com.myprojects.reciper.data.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertIngredient(ingredient: Ingredient)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef)

    suspend fun upsertRecipeIngredientCrossRefByPks(recipeId: Long, ingredientName: String) {
        upsertRecipeIngredientCrossRef(
            RecipeIngredientCrossRef(recipeId, ingredientName)
        )
    }

    @Delete
    suspend fun deleteRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Query("DELETE FROM RecipeIngredientCrossRef WHERE recipeId = :recipeId AND ingredientName = :ingredientName")
    suspend fun deleteRecipeIngredientCrossRefByPks(recipeId: Long, ingredientName: String)

    @Query("SELECT * FROM recipe WHERE recipeId = :recipeId")
    suspend fun getRecipeById(recipeId: Long): Recipe?

    @Query("SELECT * FROM recipe WHERE title = :title")
    suspend fun getRecipeByTitle(title: String): Recipe?

    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("DELETE FROM recipe WHERE recipeId = :recipeId")
    suspend fun deleteRecipeById(recipeId: Long)

    @Transaction
    @Query("SELECT * FROM recipe WHERE recipeId = :recipeId")
    suspend fun getIngredientsOfRecipeByRecipeId(recipeId: Long): List<RecipeWithIngredients>

    @Transaction
    @Query("SELECT * FROM recipe")
    fun getAllRecipesWithIngredients(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipe WHERE title LIKE '%' || :titleQuery || '%'")
    fun getAllRecipesWithIngredientsByTitle(titleQuery: String): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipe WHERE title LIKE '%' || :titleQuery || '%' AND isFavourites = 1")
    fun getAllRecipesWithIngredientsByTitleFavouritesOnly(titleQuery: String): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipe WHERE details LIKE '%' || :detailsQuery || '%'")
    fun getAllRecipesWithIngredientsByDetails(detailsQuery: String): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipe WHERE details LIKE '%' || :detailsQuery || '%' AND isFavourites = 1")
    fun getAllRecipesWithIngredientsByDetailsFavouritesOnly(detailsQuery: String): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query(
        "SELECT * FROM recipe " +
                "WHERE title LIKE '%' || :titleQuery || '%'" +
                "OR details LIKE '%' || :detailsQuery || '%'"
    )
    fun getAllRecipesWithIngredientsByTitleAndDetails(
        titleQuery: String, detailsQuery: String
    ): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query(
        "SELECT * FROM recipe " +
                "WHERE (title LIKE '%' || :titleQuery || '%'" +
                "OR details LIKE '%' || :detailsQuery || '%') AND isFavourites = 1"
    )
    fun getAllRecipesWithIngredientsByTitleAndDetailsFavouritesOnly(
        titleQuery: String, detailsQuery: String
    ): Flow<List<RecipeWithIngredients>>

    fun getAllRecipesWithIngredientsByOptions(
        titleQuery: String,
        detailsQuery: String,
        favouritesOnly: Boolean
    ): Flow<List<RecipeWithIngredients>> {
        return when {

            titleQuery.isNotBlank() && detailsQuery.isBlank() -> {
                if (favouritesOnly) {
                    getAllRecipesWithIngredientsByTitleFavouritesOnly(titleQuery)
                } else {
                    getAllRecipesWithIngredientsByTitle(titleQuery)
                }
            }

            titleQuery.isBlank() && detailsQuery.isNotBlank() -> {
                if (favouritesOnly) {
                    getAllRecipesWithIngredientsByDetailsFavouritesOnly(detailsQuery)
                } else {
                    getAllRecipesWithIngredientsByDetails(detailsQuery)
                }
            }

            else -> {
                if (favouritesOnly) {
                    getAllRecipesWithIngredientsByTitleAndDetailsFavouritesOnly(
                        titleQuery,
                        detailsQuery
                    )
                } else {
                    getAllRecipesWithIngredientsByTitleAndDetails(titleQuery, detailsQuery)
                }
            }
        }
    }

    @Transaction
    @Query("SELECT * FROM ingredient WHERE ingredientName = :ingredientName")
    suspend fun getRecipesOfIngredientByIngredientName(ingredientName: String): List<IngredientWithRecipes>

    @Transaction
    suspend fun upsertRecipeWithIngredients(
        recipe: Recipe,
        previouslySavedIngredients: List<Ingredient>,
        currentIngredients: List<Ingredient>
    ) {
        val newRecipeId = upsertRecipe(recipe)
        previouslySavedIngredients.forEach { ingredient ->
            if (ingredient !in currentIngredients) {
                deleteIngredientIfUsedOnce(ingredient)
                deleteRecipeIngredientCrossRefByPks(newRecipeId, ingredient.ingredientName)
            }
        }
        currentIngredients.forEach { ingredient ->
            upsertIngredient(ingredient)
            upsertRecipeIngredientCrossRefByPks(newRecipeId, ingredient.ingredientName)
        }
    }

    @Transaction
    suspend fun deleteRecipeWithIngredients(
        recipe: Recipe,
        ingredients: List<Ingredient>
    ) {
        ingredients.forEach { ingredient ->
            deleteIngredientIfUsedOnce(ingredient)
            deleteRecipeIngredientCrossRefByPks(recipe.recipeId, ingredient.ingredientName)
        }
        deleteRecipeById(recipe.recipeId)
    }

    suspend fun deleteIngredientIfUsedOnce(ingredient: Ingredient) {
        val recipesWithCurrentIngredient =
            getRecipesOfIngredientByIngredientName(ingredient.ingredientName)
        if (recipesWithCurrentIngredient.first().recipes.size == 1) {
            deleteIngredient(ingredient)
        }
    }
}