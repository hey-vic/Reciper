package com.myprojects.reciper.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    val title: String,
    val details: String,
    val ingredients: String,
    val cookingTime: String?,
    val isFavourites: Boolean,
    @PrimaryKey val id: Int? = null
)
