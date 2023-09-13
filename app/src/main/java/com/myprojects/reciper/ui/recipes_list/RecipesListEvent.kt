package com.myprojects.reciper.ui.recipes_list

import com.myprojects.reciper.data.entities.Recipe

sealed interface RecipesListEvent {
    data class OnFavouritesChange(
        val recipe: Recipe,
        val isFavourites: Boolean
    ) : RecipesListEvent

    data class OnRecipeClick(val recipe: Recipe) : RecipesListEvent
    object OnAddRecipeClick : RecipesListEvent
    data class OnSearchTextChange(val newText: String) : RecipesListEvent
    data class OnShouldSearchInTitleChange(val newVal: Boolean) : RecipesListEvent
    data class OnShouldSearchInDetailsChange(val newVal: Boolean) : RecipesListEvent
}
