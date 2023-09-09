package com.myprojects.reciper.ui.recipes_list

import com.myprojects.reciper.data.Recipe

sealed class RecipesListEvent {
    data class OnFavouritesChange(
        val recipe: Recipe,
        val isFavourites: Boolean
    ) : RecipesListEvent()

    data class OnRecipeClick(val recipe: Recipe) : RecipesListEvent()
    object OnAddRecipeClick : RecipesListEvent()
}
