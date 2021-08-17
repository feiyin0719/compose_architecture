package com.iffly.compose.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class DepViewModel<T> : ViewModel() {

    abstract fun getDepShareFlow(): SharedFlow<T>
}


class MVIViewModel : DepViewModel<MVIViewModel.ViewState>() {
    val userIntent = Channel<UiAction>(Channel.UNLIMITED)
    private val _sharedFlow: SharedFlow<ViewState> = handleAction()
    val viewState: StateFlow<ViewState> = _sharedFlow.stateIn(
        viewModelScope, SharingStarted.Lazily,
        ViewState()
    )


    private fun add(num: Int): ViewState {
        return viewState.value.copy(count = viewState.value.count + num)
    }

    private fun reduce(num: Int): ViewState {
        return viewState.value.copy(count = viewState.value.count - num)
    }

    private fun handleAction() =
        userIntent.receiveAsFlow().map {
            when (it) {
                is UiAction.AddAction -> add(it.num)
                is UiAction.ReduceAction -> reduce(it.num)
            }
        }.shareIn(viewModelScope, SharingStarted.Lazily, 1)


    data class ViewState(val count: Int = 0) {
        val depCount: Int
            get() = count * 2
    }

    sealed class UiAction {
        class AddAction(val num: Int) : UiAction()
        class ReduceAction(val num: Int) : UiAction()
    }

    override fun getDepShareFlow(): SharedFlow<ViewState> = _sharedFlow
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
    val viewState by viewModel.viewState.collectAsState(MVIViewModel.ViewState())

    val coroutine = rememberCoroutineScope()
    Content1(viewState.count, viewState.depCount, 0,
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
    val viewState by viewModel.viewState.collectAsState(MVIViewModel.ViewState())
    val coroutine = rememberCoroutineScope()
    Content2(count = viewState.count) {
        coroutine.launch {
            viewModel.userIntent.send(MVIViewModel.UiAction.ReduceAction(1))
        }
    }

}