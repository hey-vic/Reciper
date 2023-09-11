package com.myprojects.reciper.ui.add_edit_recipe

sealed class AddEditRecipeEvent {
    data class OnTitleChange(val title: String) : AddEditRecipeEvent()
    data class OnDetailsChange(val details: String) : AddEditRecipeEvent()
    data class OnNewIngredientNameChange(val newIngredientName: String) : AddEditRecipeEvent()
    object OnAddIngredientToList : AddEditRecipeEvent()
    data class OnRemoveIngredientFromList(val ingredientIndex: Int) : AddEditRecipeEvent()
    data class OnCookingTimeChange(val cookingTime: String) : AddEditRecipeEvent()
    object OnDeleteRecipeClick : AddEditRecipeEvent()
    object OnUndoDeleteClick : AddEditRecipeEvent()
    object OnSaveRecipeClick : AddEditRecipeEvent()
}
