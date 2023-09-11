package com.myprojects.reciper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    val title: String,
    val details: String,
    val cookingTime: String?,
    val isFavourites: Boolean,
    @PrimaryKey(autoGenerate = true) val recipeId: Long = 0
)
