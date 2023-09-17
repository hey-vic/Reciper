package com.myprojects.reciper.ui.recipes_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.myprojects.reciper.R
import com.myprojects.reciper.ui.shared.components.CustomTextField
import com.myprojects.reciper.ui.theme.LightGray
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
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            leadingIconRes = R.drawable.ic_search,
            leadingIconDescr = "Search",
            trailingIconRes = if (searchText.isNotBlank()) {
                R.drawable.ic_clear
            } else null,
            trailingIconDescr = "Clear",
            trailingIconOnClick = {
                onSearchTextChange("")
            }
        )

        Text(
            text = "Search in... ",
            style = TextStyle(fontFamily = montserratFamily, fontWeight = FontWeight.SemiBold),
            color = LightGray,
            modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 16.dp, bottom = 8.dp)
        )

        Row(
            Modifier.padding(start = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CheckboxItem(
                text = "titles",
                isChecked = shouldSearchInTitle,
                onCheckedChange = onShouldSearchInTitleChange
            )
            CheckboxItem(
                text = "details",
                isChecked = shouldSearchInDetails,
                onCheckedChange = onShouldSearchInDetailsChange
            )
            CheckboxItem(
                text = "favourites",
                isChecked = shouldSearchInFavourites,
                onCheckedChange = onShouldSearchInFavouritesChange
            )
        }
    }
}