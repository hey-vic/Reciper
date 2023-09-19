package com.myprojects.reciper.ui.view_recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprojects.reciper.data.RecipeRepository
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.util.Routes
import com.myprojects.reciper.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: Long

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe = _recipe.asStateFlow()

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients = _ingredients.asStateFlow()

    private val _uiEvent = Channel<UIEvent> { }
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        recipeId = savedStateHandle.get<Long>("recipeId")!!
        updateViewModel()
    }

    fun updateViewModel() {
        if (recipeId != -1L) {
            viewModelScope.launch {
                repository.getRecipeById(recipeId)?.let { recipe ->
                    this@ViewRecipeViewModel._recipe.value = recipe
                }
                _ingredients.value = repository
                    .getIngredientsOfRecipeByRecipeId(recipeId)
                    .ingredients
            }
        }
    }

    fun onEvent(event: ViewRecipeEvent) {
        when (event) {
            is ViewRecipeEvent.OnExportRecipeClick -> {
                recipe.value?.let { recipe ->
                    sendUiEvent(UIEvent.ShareRecipe(recipe, ingredients.value))
                }
            }

            is ViewRecipeEvent.OnEditRecipeClick -> {
                _recipe.value?.let { recipe ->
                    sendUiEvent(
                        UIEvent.Navigate(
                            Routes.ADD_EDIT_RECIPE + "?recipeId=${recipe.recipeId}"
                        )
                    )
                }
            }

            is ViewRecipeEvent.OnBackButtonClick -> {
                sendUiEvent(UIEvent.PopBackStack)
            }

            is ViewRecipeEvent.OnFavouritesChange -> {
                _recipe.value?.let { recipe ->
                    viewModelScope.launch {
                        val updatedRecipe = recipe.copy(isFavourites = !recipe.isFavourites)
                        _recipe.value = updatedRecipe
                        repository.upsertRecipe(updatedRecipe)
                    }
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