package com.iffly.compose.redux.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iffly.compose.Content1
import com.iffly.compose.Content2
import com.iffly.compose.libredux.*

class TestMiddleWare1 : MiddleWare {

    override suspend fun invoke(store: StoreViewModel): (MiddleWareDispatch) -> MiddleWareDispatch {
        return { next: MiddleWareDispatch ->
            MiddleWareDispatch { action ->
                Log.i("myyf", "mid1")
                next.dispatchAction(action = action)
            }
        }
    }
}


class FunctionActionMiddleWare : MiddleWare {

    fun interface FunctionAction {
        suspend operator fun invoke(dispatchAction: StoreDispatch, state: StoreState)
    }

    override suspend fun invoke(store: StoreViewModel): (MiddleWareDispatch) -> MiddleWareDispatch {
        return { next ->
            MiddleWareDispatch { action ->
                if (action is FunctionAction)
                    action(store, store)
                else {
                    next.dispatchAction(action = action)
                }
            }
        }
    }
}

class TestMiddleWare2 : MiddleWare {

    override suspend fun invoke(store: StoreViewModel): (MiddleWareDispatch) -> MiddleWareDispatch {
        return { next: MiddleWareDispatch ->
            MiddleWareDispatch { action ->
                Log.i("myyf", "mid2")
                next.dispatchAction(action = action)
            }
        }
    }
}

@Composable
fun ReduxApp() {
    val s =
        storeViewModel(
            listOf(CountReducer(), CountFloatReducer()),
            mutableListOf(TestMiddleWare1(), FunctionActionMiddleWare(), TestMiddleWare2())
        )
    LaunchedEffect(key1 = true) {
        s.depState(DepState::transform)
        s.depState(DepState2::transform)

    }

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
    val s = storeViewModel()
    val state: CountState by s.getState(CountState::class.java)
        .observeAsState(CountState(1))
    val depState: DepState by s.getState(DepState::class.java).observeAsState(DepState())
    val depState2: DepState2 by s.getState(DepState2::class.java).observeAsState(DepState2())
    Content1(count = state.count, depCount = depState.depCount, depCount2 = depState2.depCount,
        { navController.navigate("screen2") }
    ) {
//        s.dispatch(CountAction.provideAddAction(1))
        s.dispatch(FunctionActionMiddleWare.FunctionAction { storeDispatch: StoreDispatch, _: StoreState ->
            storeDispatch.dispatch(CountAction addWith 1)
            storeDispatch.dispatch(CountAction addWith 1)
        })
    }

}


@Composable
fun Screen2(navController: NavController) {
    val s = storeViewModel()
    val state: CountState by s.getState(CountState::class.java)
        .observeAsState(CountState(1))
    Content2(count = state.count) {
        s.dispatch(CountAction reduceWith 1)
    }
}