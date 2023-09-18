package com.myprojects.reciper.ui.view_recipe

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.myprojects.reciper.R
import com.myprojects.reciper.ui.shared.components.CustomToolbar
import com.myprojects.reciper.ui.theme.BackgroundColor
import com.myprojects.reciper.ui.theme.DarkRed
import com.myprojects.reciper.ui.theme.LightGray
import com.myprojects.reciper.ui.theme.Mint
import com.myprojects.reciper.ui.theme.montserratFamily
import com.myprojects.reciper.util.UIEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ViewRecipeScreen(
    onPopBackStack: () -> Unit,
    viewModel: ViewRecipeViewModel = hiltViewModel(),
    showSnackbar: (String, String?, () -> Unit) -> Unit,
    onNavigate: (UIEvent.Navigate) -> Unit
) {

    val recipe by viewModel.recipe.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.updateViewModel()
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.PopBackStack -> onPopBackStack()
                is UIEvent.ShowSnackbar -> {
                    showSnackbar(
                        event.message,
                        event.action,
                    ) {}
                }

                is UIEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            CustomToolbar()
            IconButton(
                onClick = {
                    viewModel.onEvent(ViewRecipeEvent.OnBackButtonClick)
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(22.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                    tint = Color.White,
                    contentDescription = "Back"
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = {
                        viewModel.onEvent(ViewRecipeEvent.OnExportRecipeClick)
                    },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_export),
                        tint = Color.White,
                        contentDescription = "Export"
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.onEvent(ViewRecipeEvent.OnEditRecipeClick)
                    },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_edit),
                        tint = Color.White,
                        contentDescription = "Edit"
                    )
                }
            }
        }
        recipe?.let { recipe ->
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    AsyncImage(
                        model = recipe.relatedImageUri ?: R.drawable.image_placeholder,
                        contentDescription = "${recipe.title} image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = recipe.title,
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Light,
                                    fontFamily = montserratFamily
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    id = if (recipe.isFavourites) R.drawable.ic_heart_filled else R.drawable.ic_heart_unfilled
                                ),
                                contentDescription = "Add to Favourites",
                                tint = DarkRed,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .size(24.dp)
                            )
                        }

                        Divider(modifier = Modifier.padding(bottom = 8.dp), color = LightGray)

                        recipe.cookingTime?.let { cookingTime ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_time),
                                    contentDescription = "Cooking time",
                                    modifier = Modifier.size(30.dp),
                                    tint = LightGray
                                )
                                Text(
                                    text = cookingTime,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = montserratFamily,
                                        color = LightGray
                                    ),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }

                        FlowRow(
                            Modifier
                                .fillMaxWidth()
                                .absoluteOffset((-4).dp)
                        ) {
                            ingredients.forEach { ingredient ->
                                CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                                    InputChip(
                                        modifier = Modifier.padding(4.dp),
                                        onClick = { },
                                        label = {
                                            Text(
                                                ingredient.ingredientName,
                                                color = Mint,
                                                fontFamily = montserratFamily
                                            )
                                        },
                                        selected = false,
                                        enabled = false,
                                        shape = RoundedCornerShape(4.dp),
                                        colors = InputChipDefaults.inputChipColors(
                                            disabledContainerColor = Color.Transparent,

                                            ),
                                        border = InputChipDefaults.inputChipBorder(
                                            disabledBorderColor = Mint,
                                            borderWidth = 1.dp
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = recipe.details,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp, top = 8.dp),
                            fontFamily = montserratFamily,
                            fontSize = 14.sp
                        )

                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}