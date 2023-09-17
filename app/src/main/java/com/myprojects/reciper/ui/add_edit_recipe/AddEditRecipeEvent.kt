package com.myprojects.reciper.ui.add_edit_recipe

import android.net.Uri

sealed interface AddEditRecipeEvent {
    data class OnTitleChange(val title: String) : AddEditRecipeEvent
    data class OnDetailsChange(val details: String) : AddEditRecipeEvent
    data class OnNewIngredientNameChange(val newIngredientName: String) : AddEditRecipeEvent
    object OnAddIngredientToList : AddEditRecipeEvent
    data class OnRemoveIngredientFromList(val ingredientIndex: Int) : AddEditRecipeEvent
    data class OnCookingTimeChange(val cookingTime: String) : AddEditRecipeEvent
    object OnDeleteRecipeClick : AddEditRecipeEvent
    object OnSaveRecipeClick : AddEditRecipeEvent
    data class OnLocallySavedImageUriChange(val newUri: Uri?) : AddEditRecipeEvent
    data class OnDisplayedImageUriChange(val newUri: Uri?) : AddEditRecipeEvent
}
