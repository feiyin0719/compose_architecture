package com.iffly.compose.redux.ui

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

data class CountState(val count: Int = 1) {
    val doubleCount: Int get() = count * 2
}


class CountReducer :
    Reducer<CountState, CountAction>(CountState::class.java, CountAction::class.java) {
    override fun reduce(
        countState: CountState,
        flow: Flow<CountAction>
    ): Flow<CountState> {
        return flow.flowOn(Dispatchers.IO).flatMapConcat { action ->
            flow {
                if (action.type == CountAction.CountActionType.Add)
                    emit(countState.copy(count = countState.count + action.data))
                else
                    emit(countState.copy(count = countState.count - action.data))
            }
        }.flowOn(Dispatchers.IO)
    }


}


data class DepState(val depCount: Int = 0) {

    companion object {
        fun transform(countState: CountState): DepState {
            return DepState(countState.count * 2)
        }
    }
}

data class DepState2(val depCount: Int = 0) {

    companion object {
        fun transform(countState: CountState, depState: DepState): DepState2 {
            return DepState2(countState.count + depState.depCount)
        }
    }
}


data class StringState(val string: String)

data class StringAction(val type: StringActionType, val data: String) {
    enum class StringActionType {
        Add
    }
}


