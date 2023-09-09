package com.myprojects.reciper.util

sealed class UIEvent {
    object PopBackStack : UIEvent()
    data class Navigate(val route: String) : UIEvent()
    data class ShowSnackbar(
        val message: String
    ) : UIEvent()
}
