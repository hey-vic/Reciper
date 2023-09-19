package com.myprojects.reciper.util

import android.net.Uri
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe

sealed interface UIEvent {
    object PopBackStack : UIEvent
    object PopToMain : UIEvent
    data class Navigate(val route: String) : UIEvent
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ) : UIEvent

    data class DeleteRecipe(
        val recipe: Recipe,
        val ingredients: List<Ingredient>,
        val imageUri: Uri?
    ) : UIEvent

    data class ShareRecipe(val recipe: Recipe, val ingredients: List<Ingredient>) : UIEvent
}
