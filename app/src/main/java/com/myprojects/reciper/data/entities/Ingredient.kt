package com.myprojects.reciper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ingredient(
    val name: String,
    @PrimaryKey(autoGenerate = true) val ingredientId: Long = 0L
)
