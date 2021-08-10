package com.iffly.compose.redux.ui

import android.util.Log
import com.iffly.compose.libredux.Reducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


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
    override fun reduce(
        countState: CountState,
        action: CountAction
    ): Flow<CountState> {
        return flow {
            emit(action)
        }.flowOn(Dispatchers.IO).flatMapConcat { action ->
            flow {
                if (action.type == CountAction.CountActionType.Add)
                    emit(countState.copy(count = countState.count + action.data))
                else
                    emit(countState.copy(count = countState.count - action.data))
                kotlinx.coroutines.delay(1000)
                emit(countState.copy(count = countState.count + 3))
            }
        }.flowOn(Dispatchers.IO)
    }


}


data class StringState(val string: String)

data class StringAction(val type: StringActionType, val data: String) {
    enum class StringActionType {
        Add
    }
}


