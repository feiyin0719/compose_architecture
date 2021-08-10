package com.iffly.compose.redux.ui

import com.iffly.compose.libredux.Reducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class CountAction(val type: CountActionType, val data: Int) {
    enum class CountActionType {
        Add, Reduce
    }

    companion object {
        fun provideAddAction(data: Int): CountAction {
            return CountAction(CountActionType.Add, data = data)
        }

        fun provideReduceAction(data: Int): CountAction {
            return CountAction(CountActionType.Reduce, data = data)
        }
    }
}

data class CountState(val count: Int = 1)


class CountReducer :
    Reducer<CountState, CountAction>(CountState::class.java, CountAction::class.java) {
    override suspend fun reduce(
        countState: CountState,
        action: CountAction
    ): CountState {
        return withContext(Dispatchers.IO) {
            when (action.type) {
                CountAction.CountActionType.Add -> return@withContext countState.copy(count = countState.count + action.data)
                CountAction.CountActionType.Reduce -> return@withContext countState.copy(count = countState.count - action.data)
            }
        }
    }
}


data class StringState(val string: String)

data class StringAction(val type: StringActionType, val data: String) {
    enum class StringActionType {
        Add
    }
}


