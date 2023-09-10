package com.myprojects.reciper.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Upsert
    suspend fun upsertRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipe WHERE id = :id")
    suspend fun getRecipeById(id: Int): Recipe?

    @Query("SELECT * FROM recipe")
    fun getRecipesList(): Flow<List<Recipe>>

    @Query("DELETE FROM recipe WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Int)
}