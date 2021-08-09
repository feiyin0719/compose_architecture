package com.iffly.compose.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iffly.compose.Content1
import com.iffly.compose.Content2

@Composable
fun MvvmApp() {
    NavGraph()
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "screen1"
    ) {
        composable("screen1") {
            Screen1(navController = navController)
        }
        composable("screen2") {
            Screen2(navController = navController)
        }
    }
}


@Composable
fun Screen1(
    navController: NavController
) {
    val viewModel: MvvmViewModel = viewModelOfNav(navController = navController)
    val count by viewModel.countState.observeAsState(0)

    Content1(count = count,
        { navController.navigate("screen2") }
    ) {
        viewModel.add(1)
    }
}


@Composable
fun Screen2(navController: NavController) {
    val viewModel: MvvmViewModel = viewModelOfNav(navController = navController)
    val count by viewModel.countState.observeAsState(0)
    Content2(count = count) {
        viewModel.reduce(1)
    }

}


class MvvmViewModel : ViewModel() {
    val countState = MutableLiveData(1)

    fun add(num: Int) {
        countState.postValue(countState.value as Int + num)
    }

    fun reduce(num: Int) {
        countState.postValue(countState.value as Int - num)
    }
}