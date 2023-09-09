package com.myprojects.reciper.ui.recipes_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myprojects.reciper.R
import com.myprojects.reciper.data.Recipe
import com.myprojects.reciper.ui.theme.montserratFamily

@Composable
fun RecipeItem(
    recipe: Recipe,
    onEvent: (RecipesListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(170.dp)
            .padding(7.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.image_placeholder),
            contentDescription = "Placeholder image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x0D000000), Color(0x80000000),
                        )
                    )
                )
        )
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
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = recipe.ingredients,
                fontFamily = montserratFamily,
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp,
                fontSize = 10.sp,
                color = Color.White
            )
        }
        Icon(painter = painterResource(
            id = if (recipe.isFavourites) R.drawable.ic_heart_filled else R.drawable.ic_heart_unfilled
        ),
            contentDescription = "Add to Favourites",
            modifier = Modifier
                .size(22.dp, 19.dp)
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .clickable {
                    onEvent(RecipesListEvent.OnFavouritesChange(recipe, !recipe.isFavourites))
                })
    }
}