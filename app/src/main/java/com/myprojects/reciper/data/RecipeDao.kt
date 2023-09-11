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
    suspend fun upsertIngredient(ingredient: Ingredient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef)

    @Delete
    suspend fun deleteRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef)

    @Query("SELECT * FROM recipe WHERE recipeId = :recipeId")
    suspend fun getRecipeById(recipeId: Long): Recipe?

    @Query("SELECT * FROM recipe")
    fun getRecipesList(): Flow<List<Recipe>>

    @Query("DELETE FROM recipe WHERE recipeId = :recipeId")
    suspend fun deleteRecipeById(recipeId: Long)

    @Transaction
    @Query("SELECT * FROM recipe WHERE recipeId = :recipeId")
    suspend fun getIngredientsOfRecipeByRecipeId(recipeId: Long): List<RecipeWithIngredients>

    @Transaction
    @Query("SELECT * FROM recipe")
    fun getRecipesWithIngredientsList(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM ingredient WHERE ingredientId = :ingredientId")
    suspend fun getRecipesOfIngredientByIngredientId(ingredientId: Long): List<IngredientWithRecipes>
}