package com.myprojects.reciper.ui.recipes_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.myprojects.reciper.R
import com.myprojects.reciper.ui.shared.components.CustomTextField
import com.myprojects.reciper.ui.theme.LightOnSurface
import com.myprojects.reciper.ui.theme.montserratFamily

@Composable
fun SearchSection(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    shouldSearchInTitle: Boolean,
    onShouldSearchInTitleChange: (Boolean) -> Unit,
    shouldSearchInDetails: Boolean,
    onShouldSearchInDetailsChange: (Boolean) -> Unit,
    shouldSearchInFavourites: Boolean,
    onShouldSearchInFavouritesChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        CustomTextField(
            placeholder = "Search for recipes...",
            value = searchText,
            onValueChange = {
                onSearchTextChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            leadingIconRes = R.drawable.ic_search,
            leadingIconDescr = "Search",
            trailingIconRes = if (searchText.isNotBlank()) {
                R.drawable.ic_clear
            } else null,
            trailingIconDescr = "Clear",
            trailingIconOnClick = {
                onSearchTextChange("")
            },
            containerColor = MaterialTheme.colorScheme.onPrimary,
            textColor = LightOnSurface
        )

        Text(
            text = "Search in... ",
            style = TextStyle(fontFamily = montserratFamily, fontWeight = FontWeight.SemiBold),
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 16.dp, bottom = 8.dp)
        )

        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CheckboxItem(
                text = "titles",
                isChecked = shouldSearchInTitle,
                onCheckedChange = onShouldSearchInTitleChange,
                modifier = Modifier.weight(1f)
            )
            CheckboxItem(
                text = "details",
                isChecked = shouldSearchInDetails,
                onCheckedChange = onShouldSearchInDetailsChange,
                modifier = Modifier.weight(1f)
            )
            CheckboxItem(
                text = "favourites",
                isChecked = shouldSearchInFavourites,
                onCheckedChange = onShouldSearchInFavouritesChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}