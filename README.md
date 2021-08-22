# compose_architecture

展示 jetpack compose中使用 mvvm mvi redux 各框架

## compose redux

### 添加依赖
增加maven仓库
```groovy
maven { url "https://raw.githubusercontent.com/feiyin0719/compose_architecture/main" }
```

```groovy
 implementation 'com.iffly:redux:0.0.3'
```

### 示例代码

创建state和action类，继承Reducer创建reducer类

```kotlin
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
```
state和action使用data class  可以方便创建，同时可以基于koltin提供的copy方法快速更新state

state类需要提供无参初始化函数，以用于当作初始状态

reducer类 reduce方法传入当前state和action的流，可以方便的进行异步操作，最后返回state flow流即可

在app初始化时创建store,传入reducer list，这里实现js redux略有不同

```koltin
val s =
        storeViewModel(
            listOf(CountReducer(), CountFloatReducer())
        )
```
因为storeViewModel是绑定activity生命周期的，所以需要获取store时候只需要调用storeViewModel即可获取，然后通过getState获取需要的state即可，操作时候通过dispatch发送对应action即可

```kotlin  
    val s = storeViewModel()
    val state: CountState by s.getState(CountState::class.java)
        .observeAsState(CountState(1))
    Content2(count = state.count) {
        s.dispatch(CountAction reduceWith 1)
    }
        
```


#### middleware

redux同时还提供中间件，可以更加丰富redux能力，middleware自定义也比较方便

比如我们实现一个redux-thunk，只需要继承Middleware

```kotlin
class FunctionActionMiddleWare : MiddleWare {

    fun interface FunctionAction {
        suspend operator fun invoke(dispatchAction: StoreDispatch, state: StoreState): Any?
    }

    override suspend fun invoke(store: StoreViewModel): (MiddleWareDispatch) -> MiddleWareDispatch {
        return { next ->
            MiddleWareDispatch { action ->
                if (action is FunctionAction)
                    action(store, store)
                else {
                    next.dispatchAction(action = action)
                }
            }
        }
    }
}

```

然后在创建store时候传入middleware即可

```kotlin
 val s =
        storeViewModel(
            listOf(CountReducer(), CountFloatReducer()),
            listOf(FunctionActionMiddleWare())
        )
```
这样我们就可以dispatch一个 函数来作为action，以此来丰富dispatch能力,同时还可以返回值

```kotlin
val i = s.dispatch(FunctionActionMiddleWare.FunctionAction { storeDispatch: StoreDispatch, _: StoreState ->
            storeDispatch.dispatch(CountAction addWith 1)
            storeDispatch.dispatch(CountAction addWith 1)
            1
        })

```

#### 依赖状态

我们开发中会存在这么一类状态，它是依赖于其他一个或者多个状态来变化的，我们提供了 depState方法来处理这种状态

我们只需要提前定义好依赖的状态和转换方法

```kotlin
data class DepState2(val depCount: Int = 0) {

    companion object {
        fun transform(countState: CountState, countFloatState: CountFloatState): DepState2 {
            return DepState2((countState.count + (countFloatState.count)).toInt())
        }
    }
}
```
依赖的状态都是定义在transform的参数中，我们通过depState方法创建依赖状态

```kotlin
 s.depState(DepState2::transform)
```
然后就可以普通状态一样通过getState方法获取使用

具体更多使用方法参考源码

### 实现原理

redux基于jetpack的viewmodel livedata 以及kotlin协程 flow实现，即通过绑定activity的生命周期创建一个全局存在的store，store就是ViewModel，然后通过store保存状态和reducer，reducer处理过程是基于flow 进行处理，可以方便进行异步操作，state基于livedata来保存

