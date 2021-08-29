package com.iffly.compose.redux

import com.iffly.compose.libredux.Reducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*


data class CountAction(val type: CountActionType, val data: Int) {
    enum class CountActionType {
        Add, Reduce
    }

    companion object {

        infix fun addWith(data: Int): CountAction {
            return CountAction(CountActionType.Add, data = data)
        }

        infix fun reduceWith(data: Int): CountAction {
            return CountAction(CountActionType.Reduce, data = data)
        }
    }
}

data class CountState(val count: Int = 1) {
    val doubleCount: Int get() = count * 2
}

@com.iffly.redux.annotation.Reducer
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

data class CountFloatState(val count: Float = 1f)

data class CountFloatAction(val type: CountFloatActionType) {
    enum class CountFloatActionType {
        Add, Reduce
    }
}
@com.iffly.redux.annotation.Reducer
class CountFloatReducer : Reducer<CountFloatState, CountFloatAction>(
    CountFloatState::class.java,
    CountFloatAction::class.java
) {
    override fun reduce(
        state: CountFloatState,
        flow: Flow<CountFloatAction>
    ): Flow<CountFloatState> {
        return flow.map {
            return@map when (it.type) {
                CountFloatAction.CountFloatActionType.Add ->
                    state.copy(count = state.count + 1)
                CountFloatAction.CountFloatActionType.Reduce ->
                    state.copy(count = state.count - 1)
            }
        }
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
        fun transform(countState: CountState, countFloatState: CountFloatState): DepState2 {
            return DepState2((countState.count + (countFloatState.count)).toInt())
        }
    }
}


data class StringState(val string: String)

data class StringAction(val type: StringActionType, val data: String) {
    enum class StringActionType {
        Add
    }
}


