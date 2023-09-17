package com.myprojects.reciper.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprojects.reciper.data.RecipeRepository
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: RecipeRepository,
) : ViewModel() {
    private var deletedRecipe: Recipe? = null
    private var deletedIngredients: List<Ingredient>? = null

    fun cacheDeletedRecipe(recipe: Recipe?, ingredients: List<Ingredient>?) {
        deletedRecipe = recipe
        deletedIngredients = ingredients
    }

    fun restoreDeletedRecipe() {
        deletedRecipe?.let { recipe ->
            deletedIngredients?.let { ingredients ->
                viewModelScope.launch {
                    repository.upsertRecipeWithIngredients(
                        recipe,
                        emptyList(),
                        ingredients
                    )
                }
            }
        }
    }
}