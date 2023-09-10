package com.myprojects.reciper.ui.add_edit_recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprojects.reciper.data.Recipe
import com.myprojects.reciper.data.RecipesRepository
import com.myprojects.reciper.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditRecipeViewModel @Inject constructor(
    private val repository: RecipesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var recipe by mutableStateOf<Recipe?>(null)
        private set

    var title by mutableStateOf("")
        private set

    var details by mutableStateOf("")
        private set

    var ingredients by mutableStateOf("")
        private set

    var cookingTime by mutableStateOf<String?>(null)
        private set

    private val _uiEvent = Channel<UIEvent> { }
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedRecipe: Recipe? = null

    init {
        val recipeId = savedStateHandle.get<Int>("recipeId")!!
        if (recipeId != -1) {
            viewModelScope.launch {
                repository.getRecipeById(recipeId)?.let { recipe ->
                    title = recipe.title
                    details = recipe.details
                    ingredients = recipe.ingredients
                    cookingTime = recipe.cookingTime ?: ""
                    this@AddEditRecipeViewModel.recipe = recipe
                }
            }
        }
    }

    fun onEvent(event: AddEditRecipeEvent) {
        when (event) {
            is AddEditRecipeEvent.OnCookingTimeChange -> {
                cookingTime = event.cookingTime
            }

            is AddEditRecipeEvent.OnDetailsChange -> {
                details = event.details
            }

            is AddEditRecipeEvent.OnIngredientsChange -> {
                ingredients = event.ingredients
            }

            is AddEditRecipeEvent.OnTitleChange -> {
                title = event.title
            }

            is AddEditRecipeEvent.OnDeleteRecipeClick -> {
                recipe?.let { recipe ->
                    viewModelScope.launch {
                        repository.deleteRecipeById(recipe.id!!)
                        deletedRecipe = recipe
                        sendUiEvent(
                            UIEvent.ShowSnackbar(
                                message = "Recipe deleted",
                                action = "Undo"
                            )
                        )
                        sendUiEvent(UIEvent.PopBackStack)
                    }
                }
            }

            is AddEditRecipeEvent.OnUndoDeleteClick -> {
                deletedRecipe?.let { recipe ->
                    viewModelScope.launch {
                        repository.upsertRecipe(recipe)
                    }
                }
            }

            AddEditRecipeEvent.OnSaveRecipeClick -> {
                viewModelScope.launch {
                    if (title.isEmpty() || details.isEmpty() || ingredients.isEmpty()) {
                        sendUiEvent(
                            UIEvent.ShowSnackbar(
                                when {
                                    title.isEmpty() -> "The title can't be empty"
                                    details.isEmpty() -> "Recipe details can't be empty"
                                    ingredients.isEmpty() -> "Recipe ingredients can't be empty"
                                    else -> ""
                                }
                            )
                        )
                        return@launch
                    }
                    repository.upsertRecipe(
                        Recipe(
                            title = title,
                            details = details,
                            ingredients = ingredients,
                            cookingTime = cookingTime,
                            isFavourites = recipe?.isFavourites ?: false,
                            id = recipe?.id
                        )
                    )
                    sendUiEvent(UIEvent.PopBackStack)
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