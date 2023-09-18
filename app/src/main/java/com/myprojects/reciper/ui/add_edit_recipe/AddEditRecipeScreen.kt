package com.myprojects.reciper.ui.add_edit_recipe

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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.myprojects.reciper.ui.theme.montserratFamily
import com.myprojects.reciper.util.UIEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditRecipeScreen(
    onPopBackStack: () -> Unit,
    onPopToMain: () -> Unit,
    viewModel: AddEditRecipeViewModel = hiltViewModel(),
    showSnackbar: (String, String?, () -> Unit) -> Unit,
    onRecipeDelete: (
        deletedRecipe: Recipe?,
        deletedIngredients: List<Ingredient>?
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
                    onRecipeDelete(event.recipe, event.ingredients)
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                onClick = {
                    if (viewModel.isTitleUnique) {
                        viewModel.onEvent(
                            AddEditRecipeEvent.OnLocallySavedImageUriChange(
                                viewModel.displayedImageUri?.let { uri ->
                                    val imageFilename = "${viewModel.title}_image"
                                    onImageSave(imageFilename, uri)
                                }
                            )
                        )
                    }
                    viewModel.onEvent(AddEditRecipeEvent.OnSaveRecipeClick)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Save"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues = paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CustomToolbar()
                IconButton(
                    onClick = {
                        viewModel.onEvent(AddEditRecipeEvent.OnBackButtonClick)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(22.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Back"
                    )
                }
                viewModel.recipe?.let {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(AddEditRecipeEvent.OnDeleteRecipeClick)
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Delete"
                        )
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    AsyncImage(
                        model = viewModel.displayedImageUri?.let {
                            viewModel.displayedImageUri
                        } ?: R.drawable.image_placeholder,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .padding(8.dp)
                            .height(40.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.onEvent(AddEditRecipeEvent.OnDisplayedImageUriChange(null))
                            },
                            enabled = viewModel.displayedImageUri != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            Text(text = "Delete image")
                        }
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            Text(
                                text = viewModel.displayedImageUri?.let {
                                    "Choose another"
                                } ?: "Add an image"
                            )
                        }
                    }
                }
                item {
                    Text(
                        text = "Title",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = montserratFamily,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }
                item {
                    CustomTextField(
                        placeholder = "",
                        value = viewModel.title,
                        onValueChange = {
                            viewModel.onEvent(AddEditRecipeEvent.OnTitleChange(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 4.dp, bottom = 16.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                }
                item {
                    Text(
                        text = "Cooking time (optional)",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = montserratFamily,
                            color = MaterialTheme.colorScheme.secondary,
                        ),
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }
                item {
                    CustomTextField(
                        placeholder = "",
                        value = viewModel.cookingTime ?: "",
                        onValueChange = {
                            viewModel.onEvent(AddEditRecipeEvent.OnCookingTimeChange(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                }
                item {
                    Text(
                        text = "Ingredients",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = montserratFamily,
                            color = MaterialTheme.colorScheme.secondary,
                        ),
                        modifier = Modifier.padding(start = 28.dp, top = 24.dp)
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(start = 28.dp, top = 18.dp, bottom = 16.dp)
                        )
                    } else {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 8.dp, top = 4.dp)
                        ) {
                            viewModel.currentIngredients.forEachIndexed { index, ingredient ->
                                CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                                    InputChip(
                                        modifier = Modifier.padding(4.dp),
                                        onClick = { },
                                        label = {
                                            Text(
                                                ingredient.ingredientName,
                                                fontFamily = montserratFamily,
                                                color = MaterialTheme.colorScheme.onSecondary
                                            )
                                        },
                                        selected = false,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = InputChipDefaults.inputChipColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            trailingIconColor = MaterialTheme.colorScheme.onSecondary
                                        ),
                                        border = InputChipDefaults.inputChipBorder(
                                            borderColor = MaterialTheme.colorScheme.secondary
                                        ),
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Remove",
                                                modifier = Modifier.clickable(
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
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 24.dp)
                    ) {
                        CustomTextField(
                            placeholder = "Add a new ingredient...",
                            value = viewModel.newIngredientName,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditRecipeEvent.OnNewIngredientNameChange(it)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.surface,
                            textColor = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary),
                            onClick = {
                                viewModel.onEvent(
                                    AddEditRecipeEvent.OnAddIngredientToList
                                )
                            }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                                tint = Color.White,
                                contentDescription = "Add",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                item {
                    Text(
                        text = "Details",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = montserratFamily,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }
                item {
                    CustomTextField(
                        placeholder = "",
                        value = viewModel.details,
                        onValueChange = {
                            viewModel.onEvent(AddEditRecipeEvent.OnDetailsChange(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 4.dp, bottom = 16.dp)
                            .defaultMinSize(minHeight = 160.dp),
                        maxLines = 100,
                        containerColor = MaterialTheme.colorScheme.surface,
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}