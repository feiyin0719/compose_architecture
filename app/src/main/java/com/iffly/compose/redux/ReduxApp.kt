package com.iffly.compose.redux

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

@com.iffly.redux.annotation.MiddleWare(3)
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

@com.iffly.redux.annotation.MiddleWare(1)
class FunctionActionMiddleWare : MiddleWare {

    fun interface FunctionAction {
        suspend operator fun invoke(dispatchAction: StoreDispatch, state: StoreState): Any?
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
@com.iffly.redux.annotation.MiddleWare(2)
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
    val s = storeViewModelInit()
    LaunchedEffect(key1 = true) {
        s.depState(DepState.Companion::transform)
        s.depState(DepState2.Companion::transform)

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
//        s.dispatch(CountAction addWith 1)
        val i =
            s.dispatch(FunctionActionMiddleWare.FunctionAction { storeDispatch: StoreDispatch, _: StoreState ->
                storeDispatch.dispatch(CountAction addWith 1)
                storeDispatch.dispatch(CountAction addWith 1)
                1
            })
        Log.i("myyf", "$i")
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