package com.iffly.compose.libredux

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreViewModel(
    private val list: List<Reducer<Any, Any>>,
    middleWares: List<MiddleWare> = emptyList()
) : StoreDispatch, StoreState, DispatchAction, ViewModel() {
    private val _reducerMap = mutableMapOf<Class<*>, Channel<Any>>()
    val sharedMap = mutableMapOf<Any, SharedFlow<Any>>()
    val stateMap = mutableMapOf<Any, LiveData<Any>>()
    lateinit var dispatchActionHead: DispatchAction

    init {
        viewModelScope.launch {
            dispatchActionHead = this@StoreViewModel
            val reserve = middleWares.map {
                it.invoke(this@StoreViewModel)
            }.toList().asReversed()
            reserve.forEach {
                dispatchActionHead = it.invoke(dispatchActionHead)
            }

            list.forEach {
                _reducerMap[it.actionClass] = Channel(Channel.UNLIMITED)
                sharedMap[it.stateClass] =
                    _reducerMap[it.actionClass]!!.receiveAsFlow().flatMapConcat { action ->
                        if (stateMap[it.stateClass]?.value != null)
                            it.reduce(
                                stateMap[it.stateClass]!!.value!!,
                                flow = flow { emit(action) })
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

    override fun dispatch(action: Any) {
        viewModelScope.launch {
            dispatchWithCoroutine(action = action)
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


    override suspend fun dispatchWithCoroutine(action: Any) {
        dispatchActionHead.dispatchAction(action = action)
    }

    override fun <T> getState(stateClass: Class<T>): MutableLiveData<T> {
        return stateMap[stateClass]!! as MutableLiveData<T>
    }

    override suspend fun dispatchAction(action: Any) {
        _reducerMap[action::class.java]!!.send(action)
    }
}

interface StoreDispatch {
    fun dispatch(action: Any)
    suspend fun dispatchWithCoroutine(action: Any)
}

interface StoreState {
    fun <T> getState(stateClass: Class<T>): MutableLiveData<T>
}


abstract class Reducer<S, A>(val stateClass: Class<S>, val actionClass: Class<A>) {
    abstract fun reduce(state: S, flow: Flow<A>): Flow<S>

}


interface DispatchAction {
    abstract suspend fun dispatchAction(action: Any)
}

interface MiddleWare {
    abstract suspend fun invoke(store: StoreViewModel): (DispatchAction) -> DispatchAction
}


class StoreViewModelFactory(
    val list: List<Reducer<out Any, out Any>>?,
    val middleWares: List<MiddleWare>
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (StoreViewModel::class.java.isAssignableFrom(modelClass)) {
            return StoreViewModel(list = list!! as List<Reducer<Any, Any>>, middleWares) as T
        }
        throw RuntimeException("unknown class:" + modelClass.name)
    }

}

@Composable
fun storeViewModel(
    list: List<Reducer<out Any, out Any>>? = null,
    middleWares: List<MiddleWare> = emptyList(),
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalContext.current as ViewModelStoreOwner) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
): StoreViewModel =
    viewModel(
        StoreViewModel::class.java,
        factory = StoreViewModelFactory(list = list, middleWares = middleWares),
        viewModelStoreOwner = viewModelStoreOwner
    )
