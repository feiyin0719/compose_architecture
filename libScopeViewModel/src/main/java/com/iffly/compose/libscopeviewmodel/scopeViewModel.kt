package com.iffly.compose.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

/**
 * get the viewModel from navController NavBackStack
 * if the viewModel have defined at navController,it will return the defined viewModel
 * also it will create a new viewModel
 */
@Suppress("MissingJvmstatic")
@Composable
inline fun <reified VM : ViewModel> viewModelOfNav(
    navController: NavController,
    key: String? = null,
    factory: ViewModelProvider.Factory? = null
): VM {
    val javaClass = VM::class.java
    var viewModelStoreOwner: ViewModelStoreOwner? = null
    navController.backQueue.forEach {
        if (it.existViewModel(javaClass, key = key)) {
            viewModelStoreOwner = it
            return@forEach
        }
    }

    if (viewModelStoreOwner == null) {
        val context = LocalContext.current
        if (context is ViewModelStoreOwner && context.existViewModel(javaClass, key = key)) {
            viewModelStoreOwner = context
        }
    }
    return viewModel(
        javaClass, viewModelStoreOwner = viewModelStoreOwner ?: checkNotNull(
            LocalViewModelStoreOwner.current
        ) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        }, key = key, factory = factory
    )
}

class NotExistException : Exception("not exist")
class ExistFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        throw NotExistException()
    }
}

fun <VM : ViewModel> ViewModelStoreOwner.existViewModel(
    modelClass: Class<VM>,
    key: String? = null
): Boolean {
    var isExist = true
    val provider = ViewModelProvider(this, ExistFactory())
    try {
        if (key != null) {
            provider.get(key, modelClass)
        } else {
            provider.get(modelClass)
        }
    } catch (e: NotExistException) {
        isExist = false
    }
    return isExist
}





