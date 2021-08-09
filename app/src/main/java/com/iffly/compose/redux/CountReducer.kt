package com.iffly.compose.redux.ui

import com.iffly.compose.libredux.Reducer


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
    override suspend fun reduce(countState: CountState, action: CountAction): CountState {
        if (action.type == CountAction.CountActionType.Add)
            return CountState(countState.count + action.data)
        else
            return CountState(countState.count - action.data)
    }


}


data class StringState(val string: String)

data class StringAction(val type: StringActionType, val data: String) {
    enum class StringActionType {
        Add
    }
}


