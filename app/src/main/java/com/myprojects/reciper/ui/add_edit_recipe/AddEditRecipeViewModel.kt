package com.myprojects.reciper.ui.add_edit_recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprojects.reciper.data.RecipeRepository
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var recipe by mutableStateOf<Recipe?>(null)
        private set

    var title by mutableStateOf("")
        private set

    var details by mutableStateOf("")
        private set

    private var previouslySavedIngredients: List<Ingredient> = emptyList()
    var currentIngredients = mutableStateListOf<Ingredient>()
        private set

    var newIngredientName by mutableStateOf("")
        private set

    var cookingTime by mutableStateOf<String?>(null)
        private set

    private val _uiEvent = Channel<UIEvent> { }
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val recipeId = savedStateHandle.get<Long>("recipeId")!!
        if (recipeId != -1L) {
            viewModelScope.launch {
                repository.getRecipeById(recipeId)?.let { recipe ->
                    title = recipe.title
                    details = recipe.details
                    cookingTime = recipe.cookingTime ?: ""
                    this@AddEditRecipeViewModel.recipe = recipe
                }
                previouslySavedIngredients = repository
                    .getIngredientsOfRecipeByRecipeId(recipeId)
                    .first()
                    .ingredients
                previouslySavedIngredients.forEach { currentIngredients.add(it) }
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

            is AddEditRecipeEvent.OnNewIngredientNameChange -> {
                newIngredientName = event.newIngredientName
            }

            is AddEditRecipeEvent.OnTitleChange -> {
                title = event.title
            }

            is AddEditRecipeEvent.OnAddIngredientToList -> {
                currentIngredients.add(Ingredient(newIngredientName))
                newIngredientName = ""
            }

            is AddEditRecipeEvent.OnRemoveIngredientFromList -> {
                currentIngredients.removeAt(event.ingredientIndex)
            }

            is AddEditRecipeEvent.OnDeleteRecipeClick -> {
                recipe?.let { recipe ->
                    viewModelScope.launch {
                        repository.deleteRecipeWithIngredients(recipe, previouslySavedIngredients)
                        sendUiEvent(
                            UIEvent.DeleteRecipe(
                                recipe = recipe,
                                ingredients = previouslySavedIngredients
                            )
                        )
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

            AddEditRecipeEvent.OnSaveRecipeClick -> {
                viewModelScope.launch {
                    if (title.isEmpty() || details.isEmpty() || currentIngredients.isEmpty()) {
                        sendUiEvent(
                            UIEvent.ShowSnackbar(
                                when {
                                    title.isBlank() -> "The title can't be empty"
                                    details.isBlank() -> "Recipe details can't be empty"
                                    currentIngredients.isEmpty() -> "Recipe ingredients can't be empty"
                                    else -> ""
                                }
                            )
                        )
                        return@launch
                    }
                    val newRecipe = Recipe(
                        title = title,
                        details = details,
                        cookingTime = cookingTime,
                        isFavourites = recipe?.isFavourites ?: false,
                        recipeId = recipe?.recipeId ?: 0
                    )
                    repository.upsertRecipeWithIngredients(
                        newRecipe,
                        previouslySavedIngredients,
                        currentIngredients
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