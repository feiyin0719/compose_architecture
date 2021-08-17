package com.iffly.compose.libredux

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreViewModel(private val list: List<Reducer<Any, Any>>) : ViewModel() {
    private val _reducerMap = mutableMapOf<Class<*>, Channel<Any>>()
    val sharedMap = mutableMapOf<Any, SharedFlow<Any>>()
    val stateMap = mutableMapOf<Any, LiveData<Any>>()

    init {
        viewModelScope.launch {
            list.forEach {
                _reducerMap[it.actionClass] = Channel(Channel.UNLIMITED)
                sharedMap[it.stateClass] =
                    _reducerMap[it.actionClass]!!.receiveAsFlow().flatMapConcat { action ->
                        if (stateMap[it.stateClass]?.value != null)
                            it.reduce(stateMap[it.stateClass]!!.value!!, action = action)
                        else
                            flow {
                                try {
                                    emit(it.stateClass.newInstance())
                                } catch (e: InstantiationException) {
                                    throw IllegalArgumentException("${it.stateClass} must provide zero argument constructor used to init state")
                                }
                            }
                    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

                stateMap[it.stateClass] = sharedMap[it.stateClass]!!
                    .asLiveData()
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

    inline fun <reified T, reified R> depState(
        crossinline transform: (T) -> R,
        scope: CoroutineScope = viewModelScope
    ) {
        sharedMap[R::class.java] = sharedMap[T::class.java]!!
            .map {
                transform(it as T)
            }.shareIn(scope = scope, SharingStarted.Lazily, 1) as SharedFlow<Any>
        stateMap[R::class.java] =
            sharedMap[R::class.java]!!.asLiveData(context = scope.coroutineContext)
    }


    inline fun <reified T1, reified T2, reified R> depState(
        crossinline transform: (T1, T2) -> R,
        scope: CoroutineScope = viewModelScope
    ) {

        sharedMap[R::class.java] = sharedMap[T1::class.java]!!.zip(sharedMap[T2::class.java]!!)
        { t1, t2 ->
            transform(t1 as T1, t2 as T2)
        }.shareIn(scope = scope, SharingStarted.Lazily, 1) as SharedFlow<Any>
        stateMap[R::class.java] = sharedMap[R::class.java]!!
            .asLiveData(context = scope.coroutineContext)
    }


    suspend fun dispatchWithCoroutine(action: Any) {
        _reducerMap[action::class.java]!!.send(action)
    }

    fun <T> getState(stateClass: Class<T>): MutableLiveData<T> {
        return stateMap[stateClass]!! as MutableLiveData<T>
    }
}


abstract class Reducer<S, A>(val stateClass: Class<S>, val actionClass: Class<A>) {
    abstract fun reduce(state: S, action: A): Flow<S>

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
