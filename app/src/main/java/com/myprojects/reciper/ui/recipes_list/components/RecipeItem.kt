package com.myprojects.reciper.ui.recipes_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.myprojects.reciper.R
import com.myprojects.reciper.data.entities.Recipe
import com.myprojects.reciper.ui.recipes_list.RecipesListEvent
import com.myprojects.reciper.ui.theme.montserratFamily

@Composable
fun RecipeItem(
    recipe: Recipe,
    ingredientNames: List<String>,
    onEvent: (RecipesListEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .padding(4.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            model = recipe.relatedImageUri ?: R.drawable.image_placeholder,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x08000000), Color(0x99000000),
                        )
                    )
                )
        )
        IconButton(
            onClick = {
                onEvent(RecipesListEvent.OnFavouritesChange(recipe, !recipe.isFavourites))
            },
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                .size(32.dp)
                .padding(6.dp)
                .padding(top = 1.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = if (recipe.isFavourites) R.drawable.ic_heart_filled else R.drawable.ic_heart_unfilled
                ),
                contentDescription = "Add to Favourites",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
        ) {
            Text(
                text = recipe.title,
                fontFamily = montserratFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ingredientNames.joinToString(" • "),
                fontFamily = montserratFamily,
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}