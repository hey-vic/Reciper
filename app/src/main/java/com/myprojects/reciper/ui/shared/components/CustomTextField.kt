package com.myprojects.reciper.ui.shared.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myprojects.reciper.ui.theme.montserratFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    placeholder: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    maxLines: Int = 1,
    leadingIconRes: Int? = null,
    leadingIconDescr: String? = null,
    trailingIconRes: Int? = null,
    trailingIconDescr: String? = null,
    trailingIconOnClick: () -> Unit = { },
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    textColor: Color = MaterialTheme.colorScheme.background
) {
    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        placeholder = {
            Text(placeholder, color = MaterialTheme.colorScheme.secondary)
        },
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = containerColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            textColor = textColor
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(
            fontFamily = montserratFamily,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start
        ),
        maxLines = maxLines,
        singleLine = (maxLines == 1),
        leadingIcon = if (leadingIconRes != null) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(leadingIconRes),
                    contentDescription = leadingIconDescr,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        } else null,
        trailingIcon = if (trailingIconRes != null) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(trailingIconRes),
                    contentDescription = trailingIconDescr,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable { trailingIconOnClick() }
                )
            }
        } else null
    )
}