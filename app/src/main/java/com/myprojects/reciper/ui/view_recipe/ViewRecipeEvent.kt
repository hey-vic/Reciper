package com.myprojects.reciper.ui.view_recipe

sealed interface ViewRecipeEvent {
    object OnExportRecipeClick : ViewRecipeEvent
    object OnEditRecipeClick : ViewRecipeEvent
    object OnBackButtonClick : ViewRecipeEvent
    object OnFavouritesChange : ViewRecipeEvent
}
