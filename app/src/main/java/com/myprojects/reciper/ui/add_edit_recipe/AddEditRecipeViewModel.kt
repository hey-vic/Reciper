package com.myprojects.reciper.ui.add_edit_recipe

import android.net.Uri
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

    // uri изображения, сохраненного локально или выбранного из галереи
    var displayedImageUri by mutableStateOf<Uri?>(null)
        private set

    var locallySavedImageUri by mutableStateOf<Uri?>(null)
        private set

    var isTitleUnique by mutableStateOf(true)
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
                    cookingTime = recipe.cookingTime
                    locallySavedImageUri = recipe.relatedImageUri?.let { Uri.parse(it) }
                    this@AddEditRecipeViewModel.recipe = recipe
                }
                previouslySavedIngredients = repository
                    .getIngredientsOfRecipeByRecipeId(recipeId)
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
                viewModelScope.launch {
                    val recipeWithSameTitle = repository.getRecipeByTitle(title)
                    isTitleUnique =
                        recipeWithSameTitle == null || recipeWithSameTitle.recipeId == recipe?.recipeId
                }
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
                        repository.deleteRecipeWithIngredients(
                            recipe,
                            previouslySavedIngredients
                        )
                        sendUiEvent(
                            UIEvent.DeleteRecipe(
                                recipe = recipe,
                                ingredients = previouslySavedIngredients,
                                imageUri = locallySavedImageUri
                            )
                        )
                        sendUiEvent(
                            UIEvent.ShowSnackbar(
                                message = "Recipe deleted",
                                action = "Undo"
                            )
                        )
                        sendUiEvent(UIEvent.PopToMain)
                    }
                }
            }

            AddEditRecipeEvent.OnSaveRecipeClick -> {
                viewModelScope.launch {
                    if (title.isBlank()) {
                        sendUiEvent(UIEvent.ShowSnackbar("The title can't be empty"))
                        return@launch
                    }
                    if (details.isBlank()) {
                        sendUiEvent(UIEvent.ShowSnackbar("Recipe details can't be empty"))
                        return@launch
                    }
                    if (currentIngredients.isEmpty()) {
                        sendUiEvent(UIEvent.ShowSnackbar("Recipe ingredients can't be empty"))
                        return@launch
                    }
                    if (!isTitleUnique) {
                        sendUiEvent(UIEvent.ShowSnackbar("Recipe with this title already exists"))
                        return@launch
                    }
                    cookingTime?.let { time ->
                        if (time.isBlank()) cookingTime = null
                    }

                    val newRecipe = Recipe(
                        title = title,
                        details = details,
                        cookingTime = cookingTime,
                        isFavourites = recipe?.isFavourites ?: false,
                        recipeId = recipe?.recipeId ?: 0,
                        relatedImageUri = locallySavedImageUri?.toString()
                    )
                    repository.upsertRecipeWithIngredients(
                        newRecipe,
                        previouslySavedIngredients,
                        currentIngredients
                    )
                    sendUiEvent(UIEvent.PopBackStack)
                }
            }

            is AddEditRecipeEvent.OnLocallySavedImageUriChange -> {
                locallySavedImageUri = event.newUri
            }

            is AddEditRecipeEvent.OnDisplayedImageUriChange -> {
                displayedImageUri = event.newUri
            }
        }
    }

    private fun sendUiEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}