package com.iffly.compose.redux


import com.iffly.compose.libredux.MiddleWare
import com.iffly.compose.libredux.Reducer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.multibindings.IntoSet
import java.util.List

@Module
@InstallIn(ViewModelComponent::class)
object MiddleWareModule {

    @Provides
    @ViewModelScoped
    fun provideMiddlewares(
        testMiddleWare1: TestMiddleWare1,
        testMiddleWare2: TestMiddleWare2,
        functionActionMiddleWare: FunctionActionMiddleWare
    ): List<out MiddleWare> {
        return listOf(
            testMiddleWare1,
            functionActionMiddleWare,
            testMiddleWare2
        ) as List<out MiddleWare>
    }


}

@Module
@InstallIn(ViewModelComponent::class)
object CountReducerModule {
    @Provides
    @IntoSet
    @ViewModelScoped
    fun provideCountReducer(countReducer: CountReducer): Reducer<Any, Any> =
        countReducer as Reducer<Any, Any>
}


@Module
@InstallIn(ViewModelComponent::class)
object CountFloatReducerModule {
    @Provides
    @IntoSet
    @ViewModelScoped
    fun provideCountFloatReducer(countFloatReducer: CountFloatReducer): Reducer<Any, Any> =
        countFloatReducer as Reducer<Any, Any>
}