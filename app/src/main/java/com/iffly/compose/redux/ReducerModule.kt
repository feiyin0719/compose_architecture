package com.iffly.compose.redux


import com.iffly.compose.libredux.MiddleWare
import com.iffly.compose.libredux.Reducer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.List

@Module
@InstallIn(ViewModelComponent::class)
object ReducerModule {


    @Provides
    @ViewModelScoped
    fun provideReducers(
        countReducer: CountReducer,
        countFloatReducer: CountFloatReducer
    ): List<out Reducer<Any, Any>> {
        return listOf<Reducer<out Any, out Any>>(
            countReducer,
            countFloatReducer
        ) as List<Reducer<Any, Any>>
    }

    @Provides
    @ViewModelScoped
    fun provideMiddlewares(
        testMiddleWare1: TestMiddleWare1,
        testMiddleWare2: TestMiddleWare2,
        functionActionMiddleWare: FunctionActionMiddleWare
    ): List<out MiddleWare> {
        return listOf<MiddleWare>(
            testMiddleWare1,
            functionActionMiddleWare,
            testMiddleWare2
        ) as List<MiddleWare>
    }
}