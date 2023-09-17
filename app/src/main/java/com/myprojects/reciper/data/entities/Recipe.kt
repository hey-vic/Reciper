package com.myprojects.reciper.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["title"], unique = true)])
data class Recipe(
    val title: String,
    val details: String,
    val cookingTime: String?,
    val isFavourites: Boolean,
    val relatedImageUri: String? = null,
    @PrimaryKey(autoGenerate = true) val recipeId: Long = 0
)
