package com.myprojects.reciper.ui.recipes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprojects.reciper.data.RecipeRepository
import com.myprojects.reciper.util.Routes
import com.myprojects.reciper.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesListViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    val recipes = repository.getRecipesList()

    private val _uiEvent = Channel<UIEvent> { }
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: RecipesListEvent) {
        when (event) {
            is RecipesListEvent.OnRecipeClick -> {
                sendUiEvent(
                    UIEvent.Navigate(Routes.ADD_EDIT_RECIPE + "?recipeId=${event.recipe.id}")
                )
            }

            is RecipesListEvent.OnAddRecipeClick -> {
                sendUiEvent(UIEvent.Navigate(Routes.ADD_EDIT_RECIPE))
            }

            is RecipesListEvent.OnFavouritesChange -> {
                viewModelScope.launch {
                    repository.upsertRecipe(
                        event.recipe.copy(isFavourites = event.isFavourites)
                    )
                }
            }
        }
    }

    private fun sendUiEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}