package com.iffly.compose.libredux

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreViewModel(val list: List<Reducer<Any, Any>>) : ViewModel() {
    private val _reducerMap = mutableMapOf<Class<*>, Channel<Any>>()
    private val _stateMap = mutableMapOf<Any, LiveData<Any>>()

    init {
        viewModelScope.launch {
            list.forEach {
                _reducerMap[it.actionClass] = Channel(Channel.UNLIMITED)
                _stateMap[it.stateClass] =
                    _reducerMap[it.actionClass]!!.receiveAsFlow().flatMapConcat { action ->
                        if (_stateMap[it.stateClass]?.value != null)
                            it.reduce(_stateMap[it.stateClass]!!.value!!, action = action)
                        else
                            flow {
                                try {
                                    emit(it.stateClass.newInstance())
                                } catch (e: InstantiationException) {
                                    throw IllegalArgumentException("${it.stateClass} must provide zero argument constructor used to init state")
                                }
                            }
                    }.asLiveData()
                //send a message to init state
                _reducerMap[it.actionClass]!!.send("")

            }
        }

    }

    fun dispatch(action: Any) {
        viewModelScope.launch {
            _reducerMap[action::class.java]!!.send(action)
        }
    }

    suspend fun dispatchWithCoroutine(action: Any) {
        _reducerMap[action::class.java]!!.send(action)
    }

    fun <T> getState(stateClass: Class<T>): MutableLiveData<T> {
        return _stateMap[stateClass]!! as MutableLiveData<T>
    }
}


abstract class Reducer<S, A>(val stateClass: Class<S>, val actionClass: Class<A>) {
    abstract  fun reduce(state: S, action: A): Flow<S>

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
