package com.waqas028.platzifakestoreapi.navigation

sealed class Screen(val route: String){
    data object HomeScreen: Screen("HomeScreen")
}