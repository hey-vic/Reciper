package com.myprojects.reciper.ui.recipes_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.myprojects.reciper.ui.theme.LightGray
import com.myprojects.reciper.ui.theme.Mint
import com.myprojects.reciper.ui.theme.montserratFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckboxItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .clickable { onCheckedChange(!isChecked) }
            .padding(start = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = if (isChecked) Mint else LightGray,
            style = TextStyle(fontFamily = montserratFamily, fontWeight = FontWeight.Medium)
        )

        CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    onCheckedChange(it)
                },
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Mint,
                    uncheckedColor = LightGray
                )
            )
        }
    }
}