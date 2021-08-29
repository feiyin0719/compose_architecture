package com.iffly.compose.redux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ReduxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReduxApp()
        }
    }
}