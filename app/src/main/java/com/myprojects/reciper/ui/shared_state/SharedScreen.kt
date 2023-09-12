package com.myprojects.reciper.ui.shared_state

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SharedScreen (
    val snackbarHostState: SnackbarHostState,
    val snackbarScope: CoroutineScope
) {
    fun showSnackbar(message: String, actionLabel: String?, onActionPerformed: () -> Unit) {
        snackbarScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel ?: "",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                onActionPerformed()
            }
        }
    }
}