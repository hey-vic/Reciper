package com.myprojects.reciper.ui.recipes_list

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myprojects.reciper.R
import com.myprojects.reciper.ui.recipes_list.components.RecipeItem
import com.myprojects.reciper.ui.recipes_list.components.SearchSection
import com.myprojects.reciper.ui.shared.components.CustomToolbar
import com.myprojects.reciper.ui.theme.BackgroundColor
import com.myprojects.reciper.ui.theme.DarkRed
import com.myprojects.reciper.util.UIEvent

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipesListScreen(
    onNavigate: (UIEvent.Navigate) -> Unit,
    onImageLoad: suspend (Uri) -> Uri?,
    viewModel: RecipesListViewModel = hiltViewModel()
) {
    val recipesWithIngredients =
        viewModel.recipesWithIngredients.collectAsState(initial = emptyList())
    val searchText by viewModel.searchText.collectAsState()
    val shouldSearchInTitle by viewModel.shouldSearchInTitle.collectAsState()
    val shouldSearchInDetails by viewModel.shouldSearchInDetails.collectAsState()
    val shouldSearchInFavourites by viewModel.shouldSearchInFavourites.collectAsState()

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
                .background(BackgroundColor)
        ) {
            CustomToolbar()
            SearchSection(
                searchText = searchText,
                onSearchTextChange = {
                    viewModel.onEvent(RecipesListEvent.OnSearchTextChange(it))
                },
                shouldSearchInTitle = shouldSearchInTitle,
                onShouldSearchInTitleChange = {
                    viewModel.onEvent(RecipesListEvent.OnShouldSearchInTitleChange(it))
                },
                shouldSearchInDetails = shouldSearchInDetails,
                onShouldSearchInDetailsChange = {
                    viewModel.onEvent(RecipesListEvent.OnShouldSearchInDetailsChange(it))
                },
                shouldSearchInFavourites = shouldSearchInFavourites,
                onShouldSearchInFavouritesChange = {
                    viewModel.onEvent(RecipesListEvent.OnShouldSearchInFavouritesChange(it))
                }
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(9.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (shouldSearchInDetails || shouldSearchInTitle) {
                    items(recipesWithIngredients.value) { recipeWithIngredients ->
                        RecipeItem(
                            recipe = recipeWithIngredients.recipe,
                            ingredientNames = recipeWithIngredients.ingredients.map { it.ingredientName },
                            onEvent = viewModel::onEvent,
                            onImageLoad = onImageLoad,
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
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}