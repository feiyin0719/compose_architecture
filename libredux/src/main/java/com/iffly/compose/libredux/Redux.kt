package com.iffly.compose.libredux

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreViewModel(val list: List<Reducer<Any, Any>>) : ViewModel() {
    private val _reducerMap = mutableMapOf<Class<*>, Reducer<Any, Any>>()
    private val _stateMap = mutableMapOf<Any, MutableLiveData<Any>>()

    init {
        list.forEach {
            _reducerMap[it.actionClass] = it
            try {
                _stateMap[it.stateClass] = MutableLiveData(it.stateClass.newInstance())
            } catch (e: InstantiationException) {
                throw IllegalArgumentException("${it.stateClass} must provide zero argument constructor used to init state")
            }
        }
    }

    fun dispatch(action: Any) {
        viewModelScope.launch {
            val reducer = _reducerMap[action::class.java]
            reducer?.let {
                val state = _stateMap[it.stateClass]
                state?.let { _state ->
                    _state.value?.let { _value ->
                        val newState =
                            withContext(viewModelScope.coroutineContext) {
                                it.reduce(
                                    _value,
                                    action = action
                                )
                            }
                        _state.postValue(newState)
                    }
                }
            }
        }

    }

    fun <T> getState(stateClass: Class<T>): MutableLiveData<T> {
        return _stateMap[stateClass]!! as MutableLiveData<T>
    }
}


abstract class Reducer<S, A>(val stateClass: Class<S>, val actionClass: Class<A>) {
    abstract suspend fun reduce(state: S, action: A): S

}


class StoreViewModelFactory(val list: List<Reducer<out Any, out Any>>?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (StoreViewModel::class.java.isAssignableFrom(modelClass)) {
            return StoreViewModel(list = list!! as List<Reducer<Any, Any>>) as T
        }
        throw RuntimeException("unknown class:" + modelClass.name)
    }

}

@Composable
fun storeViewModel(
    list: List<Reducer<out Any, out Any>>? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalContext.current as ViewModelStoreOwner) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
): StoreViewModel =
    viewModel(
        StoreViewModel::class.java,
        factory = StoreViewModelFactory(list = list),
        viewModelStoreOwner = viewModelStoreOwner
    )
