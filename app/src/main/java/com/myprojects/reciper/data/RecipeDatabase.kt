package com.myprojects.reciper.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.data.relations.RecipeIngredientCrossRef

@Database(
    entities = [
        Recipe::class,
        Ingredient::class,
        RecipeIngredientCrossRef::class
    ],
    version = 1
)
abstract class RecipeDatabase : RoomDatabase() {

    abstract val dao: RecipeDao
}