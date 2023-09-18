package com.myprojects.reciper.ui.add_edit_recipe

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.myprojects.reciper.R
import com.myprojects.reciper.data.entities.Ingredient
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.ui.shared.components.CustomTextField
import com.myprojects.reciper.ui.shared.components.CustomToolbar
import com.myprojects.reciper.ui.theme.BackgroundColor
import com.myprojects.reciper.ui.theme.DarkRed
import com.myprojects.reciper.ui.theme.montserratFamily
import com.myprojects.reciper.util.UIEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditRecipeScreen(
    onPopBackStack: () -> Unit,
    onPopToMain: () -> Unit,
    viewModel: AddEditRecipeViewModel = hiltViewModel(),
    showSnackbar: (String, String?, () -> Unit) -> Unit,
    onRecipeDelete: (
        deletedRecipe: Recipe?,
        deletedIngredients: List<Ingredient>?,
        deletedImageUri: Uri?
    ) -> Unit,
    onUndoDelete: () -> Unit,
    onImageSave: (String, Uri) -> Uri?,
    onImageLoad: suspend (Uri) -> Uri?
) {
    val interactionSource = remember { MutableInteractionSource() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.onEvent(AddEditRecipeEvent.OnDisplayedImageUriChange(uri))
        }
    )

    LaunchedEffect(key1 = viewModel.locallySavedImageUri) {
        viewModel.locallySavedImageUri?.let { uri ->
            viewModel.onEvent(
                AddEditRecipeEvent.OnDisplayedImageUriChange(
                    onImageLoad(uri)
                )
            )
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.PopBackStack -> onPopBackStack()
                is UIEvent.ShowSnackbar -> {
                    showSnackbar(
                        event.message,
                        event.action,
                    ) {
                        onUndoDelete()
                    }
                }

                is UIEvent.DeleteRecipe -> {
                    onRecipeDelete(event.recipe, event.ingredients, event.imageUri)
                }

                is UIEvent.PopToMain -> {
                    onPopToMain()
                }

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
                    if (viewModel.isTitleUnique) {
                        viewModel.displayedImageUri?.let { uri ->
                            val imageFilename = "${viewModel.title}_image"
                            viewModel.onEvent(
                                AddEditRecipeEvent.OnLocallySavedImageUriChange(
                                    onImageSave(imageFilename, uri)
                                )
                            )
                        }
                    }
                    viewModel.onEvent(AddEditRecipeEvent.OnSaveRecipeClick)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Save"
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CustomToolbar()
                viewModel.recipe?.let {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(AddEditRecipeEvent.OnDeleteRecipeClick)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                            tint = Color.White,
                            contentDescription = "Delete"
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AsyncImage(
                        model = if (viewModel.displayedImageUri != null) {
                            viewModel.displayedImageUri
                        } else {
                            R.drawable.image_placeholder
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                item {
                    Button(onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Text(text = "Pick one photo")
                    }
                }
                item {
                    CustomTextField(
                        placeholder = "Title",
                        value = viewModel.title,
                        onValueChange = {
                            viewModel.onEvent(AddEditRecipeEvent.OnTitleChange(it))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    CustomTextField(
                        placeholder = "Details",
                        value = viewModel.details,
                        onValueChange = {
                            viewModel.onEvent(AddEditRecipeEvent.OnDetailsChange(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 100
                    )
                }
                item {
                    Text(
                        text = "Ingredients",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = montserratFamily,
                            color = Color.Black
                        )
                    )
                }
                item {
                    if (viewModel.currentIngredients.isEmpty()) {
                        Text(
                            text = "No ingredients yet",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = montserratFamily,
                                color = Color.Gray
                            )
                        )
                    } else {
                        FlowRow(
                            Modifier.fillMaxWidth()
                        ) {
                            viewModel.currentIngredients.forEachIndexed { index, ingredient ->
                                InputChip(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .align(alignment = Alignment.CenterVertically),
                                    onClick = { },
                                    label = { Text(ingredient.ingredientName) },
                                    selected = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Remove",
                                            modifier =
                                            Modifier.clickable(
                                                interactionSource = interactionSource,
                                                indication = null
                                            ) {
                                                viewModel.onEvent(
                                                    AddEditRecipeEvent.OnRemoveIngredientFromList(
                                                        index
                                                    )
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = viewModel.newIngredientName,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditRecipeEvent.OnNewIngredientNameChange(it)
                                )
                            },
                            modifier = Modifier
                                .weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            placeholder = { Text(text = "Add a new ingredient...") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                        )
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .size(40.dp)
                                .background(Color(0xFF960F0F)),
                            onClick = {
                                viewModel.onEvent(
                                    AddEditRecipeEvent.OnAddIngredientToList
                                )
                            }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                tint = Color.White,
                                contentDescription = "Add"
                            )
                        }
                    }
                }
                item {
                    CustomTextField(
                        placeholder = "Cooking time (optional)",
                        value = viewModel.cookingTime ?: "",
                        onValueChange = {
                            viewModel.onEvent(AddEditRecipeEvent.OnCookingTimeChange(it))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}