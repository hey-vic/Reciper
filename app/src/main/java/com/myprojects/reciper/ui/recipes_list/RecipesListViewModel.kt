package com.myprojects.reciper.ui.recipes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprojects.reciper.data.RecipeRepository
import com.myprojects.reciper.util.Routes
import com.myprojects.reciper.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesListViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiEvent = Channel<UIEvent> { }
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _shouldSearchInTitle = MutableStateFlow(true)
    val shouldSearchInTitle = _shouldSearchInTitle.asStateFlow()

    private val _shouldSearchInDetails = MutableStateFlow(true)
    val shouldSearchInDetails = _shouldSearchInDetails.asStateFlow()

    private val _shouldSearchInFavourites = MutableStateFlow(false)
    val shouldSearchInFavourites = _shouldSearchInFavourites.asStateFlow()

    val recipesWithIngredients =
        merge(_searchText, _shouldSearchInTitle, _shouldSearchInDetails, _shouldSearchInFavourites)
            .flatMapLatest {
                repository.getAllRecipesWithIngredientsByOptions(
                    if (_shouldSearchInTitle.value) searchText.value.lowercase() else "",
                    if (_shouldSearchInDetails.value) searchText.value.lowercase() else "",
                    _shouldSearchInFavourites.value
                )
            }

    private val _isSearchSectionVisible = MutableStateFlow(false)
    val isSearchSectionVisible = _isSearchSectionVisible.asStateFlow()

    fun onEvent(event: RecipesListEvent) {
        when (event) {
            is RecipesListEvent.OnRecipeClick -> {
                sendUiEvent(
                    UIEvent.Navigate(Routes.ADD_EDIT_RECIPE + "?recipeId=${event.recipe.recipeId}")
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

            is RecipesListEvent.OnSearchTextChange -> {
                _searchText.value = event.newText
            }

            is RecipesListEvent.OnShouldSearchInDetailsChange -> {
                _shouldSearchInDetails.value = event.newVal
            }

            is RecipesListEvent.OnShouldSearchInTitleChange -> {
                _shouldSearchInTitle.value = event.newVal
            }

            is RecipesListEvent.OnShouldSearchInFavouritesChange -> {
                _shouldSearchInFavourites.value = event.newVal
            }

            RecipesListEvent.OnToggleSearchSectionVisibility -> {
                _isSearchSectionVisible.value = !_isSearchSectionVisible.value
            }
        }
    }


    private fun sendUiEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}