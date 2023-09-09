package com.myprojects.reciper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.myprojects.reciper.ui.add_edit_recipe.AddEditRecipeScreen
import com.myprojects.reciper.ui.recipes_list.RecipesListScreen
import com.myprojects.reciper.ui.theme.ReciperTheme
import com.myprojects.reciper.util.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReciperTheme {
                val navController = rememberNavController()
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
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) {
                        AddEditRecipeScreen(
                            onPopBackStack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}