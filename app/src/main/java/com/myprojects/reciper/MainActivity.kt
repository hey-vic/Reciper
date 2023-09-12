package com.myprojects.reciper

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.myprojects.reciper.ui.add_edit_recipe.AddEditRecipeScreen
import com.myprojects.reciper.ui.recipes_list.RecipesListScreen
import com.myprojects.reciper.ui.shared_state.SharedScreen
import com.myprojects.reciper.ui.shared_state.SharedViewModel
import com.myprojects.reciper.ui.theme.ReciperTheme
import com.myprojects.reciper.util.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReciperTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()
                val snackbarScope: CoroutineScope = rememberCoroutineScope()
                val sharedScreen = remember(snackbarHostState, navController, snackbarScope) {
                    SharedScreen(
                        snackbarHostState = snackbarHostState,
                        snackbarScope = snackbarScope
                    )
                }
                val sharedViewModel: SharedViewModel = hiltViewModel()
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.RECIPES_LIST
                    ) {
                        composable(Routes.RECIPES_LIST) {
                            RecipesListScreen(onNavigate = {
                                navController.navigate(it.route)
                            })
                        }
                        composable(
                            route = Routes.ADD_EDIT_RECIPE + "?recipeId={recipeId}",
                            arguments = listOf(
                                navArgument(name = "recipeId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                }
                            )
                        ) {
                            AddEditRecipeScreen(
                                onPopBackStack = {
                                    navController.popBackStack()
                                },
                                showSnackbar = { message, actionLabel, onActionPerformed ->
                                    sharedScreen.showSnackbar(message, actionLabel, onActionPerformed)
                                },
                                onRecipeDelete = { recipe, ingredients ->
                                    sharedViewModel.cacheDeletedRecipe(recipe, ingredients)
                                },
                                onUndoDelete = { sharedViewModel.restoreDeletedRecipe() }
                            )
                        }
                    }
                }
            }
        }
    }
}