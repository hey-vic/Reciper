package com.myprojects.reciper.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myprojects.reciper.R
import com.myprojects.reciper.ui.theme.DarkRed
import com.myprojects.reciper.ui.theme.sacramentoFamily

@Composable
fun CustomToolbar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(DarkRed)
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontFamily = sacramentoFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}