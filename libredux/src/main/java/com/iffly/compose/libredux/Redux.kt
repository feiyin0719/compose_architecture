package com.iffly.compose.libredux

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class StoreViewModel(
    private val list: List<Reducer<Any, Any>>,
    middleWares: List<MiddleWare> = emptyList()
) : StoreDispatch, StoreState, MiddleWareDispatch, ViewModel() {
    private val _reducerMap = mutableMapOf<Class<Any>, Channel<Any>>()
    val sharedMap = mutableMapOf<Any, SharedFlow<Any>>()
    val stateMap = mutableMapOf<Any, LiveData<Any>>()
    private lateinit var middleWareDispatchHead: MiddleWareDispatch

    init {
        viewModelScope.launch {
            middleWareDispatchHead = this@StoreViewModel
            val reserve = middleWares.map {
                it(this@StoreViewModel)
            }.toList().asReversed()
            reserve.forEach {
                middleWareDispatchHead = it(middleWareDispatchHead)
            }

            list.forEach {
                if (_reducerMap.containsKey(it.actionClass))
                    throw IllegalStateException("The  ${it.actionClass} action cannot register twice")
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

    override fun dispatch(action: Any): Any? {
        return runBlocking {
            return@runBlocking viewModelScope.async {
                return@async dispatchWithCoroutine(action = action)
            }.await()
        }
    }

    inline fun <reified T, reified R> depState(
        noinline transform: (T) -> R,
        scope: CoroutineScope = viewModelScope
    ) {
        if (sharedMap.containsKey(R::class.java))
            throw IllegalStateException("The ${R::class.java} state have already register")
        sharedMap[R::class.java] = sharedMap[T::class.java]!!
            .map {
                transform(it as T)
            }.shareIn(scope = scope, SharingStarted.Lazily, 1) as SharedFlow<Any>
        stateMap[R::class.java] =
            sharedMap[R::class.java]!!.asLiveData(context = scope.coroutineContext)
    }


    inline fun <reified T1, reified T2, reified R> depState(
        noinline transform: (T1, T2) -> R,
        scope: CoroutineScope = viewModelScope
    ) {
        if (sharedMap.containsKey(R::class.java))
            throw IllegalStateException("The ${R::class.java} state have already register")
        sharedMap[R::class.java] = sharedMap[T1::class.java]!!.combine(sharedMap[T2::class.java]!!)
        { t1, t2 ->
            transform(t1 as T1, t2 as T2)
        }.shareIn(scope = scope, SharingStarted.Lazily, 1) as SharedFlow<Any>
        stateMap[R::class.java] = sharedMap[R::class.java]!!
            .asLiveData(context = scope.coroutineContext)
    }


    override suspend fun dispatchWithCoroutine(action: Any): Any? {
        return middleWareDispatchHead.dispatchAction(action = action)
    }

    override fun <T> getState(stateClass: Class<T>): MutableLiveData<T> {
        return stateMap[stateClass]!! as MutableLiveData<T>
    }

    override suspend fun dispatchAction(action: Any) {
        _reducerMap[action::class.java]!!.send(action)
    }
}

interface StoreDispatch {
    fun dispatch(action: Any): Any?
    suspend fun dispatchWithCoroutine(action: Any): Any?
}

interface StoreState {
    fun <T> getState(stateClass: Class<T>): MutableLiveData<T>
}


abstract class Reducer<S, A>(
    val stateClass: Class<S>,
    val actionClass: Class<A>
) {
    abstract fun reduce(state: S, flow: Flow<A>): Flow<S>
}


fun interface MiddleWareDispatch {
    suspend fun dispatchAction(action: Any): Any?
}

fun interface MiddleWare {
    suspend operator fun invoke(store: StoreViewModel): (MiddleWareDispatch) -> MiddleWareDispatch
}


class StoreViewModelFactory(
    val list: List<Reducer<out Any, out Any>>?,
    val middleWares: List<MiddleWare>
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (StoreViewModel::class.java.isAssignableFrom(modelClass)) {
            var useList = list
            try {
                val conClass = Class.forName("com.iffly.compose.libredux.ReduxListContainer")
                val constructor = conClass.getDeclaredConstructor()
                constructor.isAccessible = true
                val container = constructor.newInstance()
                val listField = conClass.getDeclaredField("reducerList")
                listField.isAccessible = true
                useList = listField.get(container) as List<Reducer<out Any, out Any>>?

            } catch (e: Exception) {
                Log.i("myyf", "$e")


            }

            return StoreViewModel(list = useList!! as List<Reducer<Any, Any>>, middleWares) as T
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
