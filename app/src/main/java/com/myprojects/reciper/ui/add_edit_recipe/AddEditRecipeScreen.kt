package com.myprojects.reciper.ui.add_edit_recipe

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myprojects.reciper.R
import com.myprojects.reciper.ui.CustomToolbar
import com.myprojects.reciper.ui.theme.BackgroundGray
import com.myprojects.reciper.ui.theme.DarkRed
import com.myprojects.reciper.ui.theme.Mint
import com.myprojects.reciper.ui.theme.montserratFamily
import com.myprojects.reciper.util.UIEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    onPopBackStack: () -> Unit,
    viewModel: AddEditRecipeViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.PopBackStack -> onPopBackStack()
                is UIEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(AddEditRecipeEvent.OnUndoDeleteClick)
                    }
                }

                else -> Unit
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = DarkRed,
                contentColor = Color.White,
                onClick = {
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
                .background(BackgroundGray)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                CustomTextField(
                    placeholder = "Title",
                    value = viewModel.title,
                    onValueChange = {
                        viewModel.onEvent(AddEditRecipeEvent.OnTitleChange(it))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    placeholder = "Details",
                    value = viewModel.details,
                    onValueChange = {
                        viewModel.onEvent(AddEditRecipeEvent.OnDetailsChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 100
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    placeholder = "Ingredients",
                    value = viewModel.ingredients,
                    onValueChange = {
                        viewModel.onEvent(AddEditRecipeEvent.OnIngredientsChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 10
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    placeholder = "Cooking time (optional)",
                    value = viewModel.cookingTime ?: "",
                    onValueChange = {
                        viewModel.onEvent(AddEditRecipeEvent.OnCookingTimeChange(it))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    placeholder: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    maxLines: Int = 1
) {
    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        placeholder = {
            Text(placeholder, color = Mint)
        },
        modifier = modifier
            .background(color = Color.White, shape = RoundedCornerShape(16.dp)),
        singleLine = false,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        textStyle = TextStyle(
            fontFamily = montserratFamily,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start
        ),
        maxLines = maxLines
    )
}