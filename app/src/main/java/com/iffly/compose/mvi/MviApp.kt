package com.iffly.compose.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iffly.compose.Content1
import com.iffly.compose.Content2
import com.iffly.compose.mvvm.viewModelOfNav
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MVIViewModel : ViewModel() {
    val userIntent = Channel<UiAction>(Channel.UNLIMITED)
    val viewState: LiveData<ViewState> = handleAction()


    private fun add(num: Int): ViewState {
        return if (viewState.value != null) {
            viewState.value!!.copy(viewState.value!!.count + num)
        } else {
            ViewState()
        }
    }

    private fun reduce(num: Int): ViewState {
        return if (viewState.value != null) {
            viewState.value!!.copy(viewState.value!!.count - num)
        } else {
            ViewState()
        }
    }

    private fun handleAction() =
        userIntent.consumeAsFlow().map {
            when (it) {
                is UiAction.AddAction -> add(it.num)
                is UiAction.ReduceAction -> reduce(it.num)
            }
        }.asLiveData()


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