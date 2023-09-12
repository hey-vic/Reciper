package com.myprojects.reciper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ingredient(
    @PrimaryKey val ingredientName: String
)
