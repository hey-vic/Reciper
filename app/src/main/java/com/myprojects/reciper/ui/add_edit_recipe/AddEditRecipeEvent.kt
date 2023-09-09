package com.myprojects.reciper.ui.add_edit_recipe

sealed class AddEditRecipeEvent {
    data class OnTitleChange(val title: String) : AddEditRecipeEvent()
    data class OnDetailsChange(val details: String) : AddEditRecipeEvent()
    data class OnIngredientsChange(val ingredients: String) : AddEditRecipeEvent()
    data class OnCookingTimeChange(val cookingTime: String) : AddEditRecipeEvent()
    object OnSaveRecipeClick : AddEditRecipeEvent()
}
