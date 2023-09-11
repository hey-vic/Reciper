package com.myprojects.reciper.ui.recipes_list

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myprojects.reciper.R
import com.myprojects.reciper.ui.CustomToolbar
import com.myprojects.reciper.ui.theme.BackgroundGray
import com.myprojects.reciper.ui.theme.DarkRed
import com.myprojects.reciper.util.UIEvent

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipesListScreen(
    onNavigate: (UIEvent.Navigate) -> Unit, viewModel: RecipesListViewModel = hiltViewModel()
) {
    val recipesWithIngredients =
        viewModel.recipesWithIngredients.collectAsState(initial = emptyList())

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = DarkRed,
                contentColor = Color.White,
                onClick = {
                    viewModel.onEvent(RecipesListEvent.OnAddRecipeClick)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus), contentDescription = "Add"
                )
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
        ) {
            CustomToolbar()

            LazyVerticalGrid(
                columns = GridCells.Fixed(2), contentPadding = PaddingValues(9.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(recipesWithIngredients.value) { recipeWithIngredients ->
                    RecipeItem(
                        recipe = recipeWithIngredients.recipe,
                        ingredientNames = recipeWithIngredients.ingredients.map { it.name },
                        onEvent = viewModel::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onEvent(
                                    RecipesListEvent.OnRecipeClick(
                                        recipeWithIngredients.recipe
                                    )
                                )
                            }
                    )
                }
            }
        }
    }
}