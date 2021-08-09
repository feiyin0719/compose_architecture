package com.iffly.compose.redux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.iffly.compose.redux.ui.ReduxApp

class ReduxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReduxApp()
        }
    }
}