package com.myprojects.reciper.ui.recipes_list

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
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
    val isSearchSectionVisible by viewModel.isSearchSectionVisible.collectAsState()

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
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValues = paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CustomToolbar()
                IconButton(
                    onClick = {
                        viewModel.onEvent(RecipesListEvent.OnToggleSearchSectionVisibility)
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = if (isSearchSectionVisible) {
                                R.drawable.ic_clear
                            } else {
                                R.drawable.ic_search
                            }
                        ),
                        tint = Color.White,
                        contentDescription = if (isSearchSectionVisible) {
                            "Close search"
                        } else {
                            "Open search"
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = isSearchSectionVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
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
                    },
                    modifier = Modifier.background(DarkRed)
                    //modifier = Modifier.background(Color(0xFF1F1111))
                )
            }
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