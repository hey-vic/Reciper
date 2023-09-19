package com.myprojects.reciper

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.ui.add_edit_recipe.AddEditRecipeScreen
import com.myprojects.reciper.ui.recipes_list.RecipesListScreen
import com.myprojects.reciper.ui.shared.CachedImagesCleaningWorker
import com.myprojects.reciper.ui.shared.SharedScreen
import com.myprojects.reciper.ui.shared.SharedViewModel
import com.myprojects.reciper.ui.theme.ReciperTheme
import com.myprojects.reciper.ui.view_recipe.ViewRecipeScreen
import com.myprojects.reciper.util.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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

                val downloadRequest = PeriodicWorkRequestBuilder<CachedImagesCleaningWorker>(
                    2,
                    TimeUnit.HOURS
                ).build()
                val workManager = WorkManager.getInstance(applicationContext)
                workManager.enqueueUniquePeriodicWork(
                    "CacheImagesCleaningUniqueWork",
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    downloadRequest
                )

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) {
                            Snackbar(
                                actionColor = MaterialTheme.colorScheme.secondary,
                                snackbarData = it
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.RECIPES_LIST,
                        modifier = Modifier.padding(paddingValues = paddingValues)
                    ) {
                        composable(Routes.RECIPES_LIST) {
                            RecipesListScreen(
                                onNavigate = {
                                    navController.navigate(it.route)
                                }
                            )
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
                                onPopToMain = {
                                    navController.popBackStack(
                                        Routes.RECIPES_LIST,
                                        inclusive = false
                                    )
                                },
                                showSnackbar = { message, actionLabel, onActionPerformed ->
                                    sharedScreen.showSnackbar(
                                        message,
                                        actionLabel,
                                        onActionPerformed
                                    )
                                },
                                onRecipeDelete = { recipe, ingredients ->
                                    sharedViewModel.cacheDeletedRecipe(
                                        recipe,
                                        ingredients
                                    )
                                },
                                onUndoDelete = { sharedViewModel.restoreDeletedRecipe() },
                                onImageSave = { filename, uri ->
                                    savePhotoToInternalStorage(filename, uri)
                                },
                                onImageLoad = { uri -> loadPhotoFromInternalStorage(uri) }
                            )
                        }
                        composable(
                            route = Routes.VIEW_RECIPE + "?recipeId={recipeId}",
                            arguments = listOf(
                                navArgument(name = "recipeId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                }
                            )
                        ) {
                            ViewRecipeScreen(
                                onPopBackStack = {
                                    navController.popBackStack()
                                },
                                showSnackbar = { message, actionLabel, onActionPerformed ->
                                    sharedScreen.showSnackbar(
                                        message,
                                        actionLabel,
                                        onActionPerformed
                                    )
                                },
                                onNavigate = {
                                    navController.navigate(it.route)
                                },
                                onRecipeShare = { recipe, ingredients ->
                                    shareRecipe(recipe, ingredients)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun shareRecipe(recipe: Recipe, ingredients: List<Ingredient>) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND

            type = if (recipe.relatedImageUri != null) {
                val imageUri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "com.myprojects.reciper.provider",  //(use your app signature + ".provider" )
                    recipe.relatedImageUri.toUri().toFile()
                )
                putExtra(Intent.EXTRA_STREAM, imageUri)
                "image/*"
            } else {
                "text/plain"
            }

            val cookingTimeText = if (recipe.cookingTime != null) {
                "Cooking time: ${recipe.cookingTime}\n"
            } else ""
            putExtra(
                Intent.EXTRA_TEXT,
                "${recipe.title}\n${cookingTimeText}" +
                        "Ingredients: ${ingredients.joinToString { it.ingredientName }}\n" +
                        recipe.details
            )
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun savePhotoToInternalStorage(filename: String, uri: Uri): Uri? {
        try {
            val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
            openFileOutput("$filename.jpg", MODE_PRIVATE)
                .use { stream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                        throw IOException("Couldn't save bitmap.")
                    }
                }
            return filesDir.toUri().buildUpon().appendPath("$filename.jpg").build()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private suspend fun loadPhotoFromInternalStorage(uri: Uri): Uri? {
        return withContext(Dispatchers.IO) {
            val files = filesDir.listFiles()
            val newUri =
                files?.firstOrNull { it.canRead() && it.isFile && it.toUri() == uri }
                    ?.toUri()
            newUri
        }
    }
}