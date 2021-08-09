package com.iffly.compose.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iffly.compose.Content1
import com.iffly.compose.Content2
import com.iffly.compose.mvvm.viewModelOfNav
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class MVIViewModel : ViewModel() {
    val viewState = MutableLiveData(ViewState())
    val userIntent = Channel<UiAction>(Channel.UNLIMITED)

    init {
        handleAction()
    }

    private fun add(num: Int) {
        viewState.value?.let {
            viewState.postValue(it.copy(count = it.count + 1))
        }

    }

    private fun reduce(num: Int) {
        viewState.value?.let {
            viewState.postValue(it.copy(count = it.count - 1))
        }
    }

    private fun handleAction() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is UiAction.AddAction -> add(it.num)
                    is UiAction.ReduceAction -> reduce(it.num)
                }
            }
        }
    }

    data class ViewState(val count: Int = 1)
    sealed class UiAction {
        class AddAction(val num: Int) : UiAction()
        class ReduceAction(val num: Int) : UiAction()
    }
}


@Composable
fun MviApp() {
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
    val viewModel: MVIViewModel = viewModelOfNav(navController = navController)
    val viewState by viewModel.viewState.observeAsState(MVIViewModel.ViewState())
    val coroutine = rememberCoroutineScope()
    Content1(count = viewState.count,
        { navController.navigate("screen2") }
    ) {
        coroutine.launch {
            viewModel.userIntent.send(MVIViewModel.UiAction.AddAction(1))
        }
    }
}


@Composable
fun Screen2(navController: NavController) {
    val viewModel: MVIViewModel = viewModelOfNav(navController = navController)
    val viewState by viewModel.viewState.observeAsState(MVIViewModel.ViewState())
    val coroutine = rememberCoroutineScope()
    Content2(count = viewState.count) {
        coroutine.launch {
            viewModel.userIntent.send(MVIViewModel.UiAction.ReduceAction(1))
        }
    }

}