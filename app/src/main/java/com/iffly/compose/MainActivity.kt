package com.iffly.compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.iffly.compose.mvi.MVIActivity
import com.iffly.compose.mvvm.MVVMActivity
import com.iffly.compose.redux.ReduxActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }
}

@Composable
fun Content() {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = {
            context.startActivity(Intent(context, MVVMActivity::class.java))
        }) {
            Text(text = "goto MVVM app")
        }

        Button(onClick = {
            context.startActivity(Intent(context, ReduxActivity::class.java))
        }) {
            Text(text = "goto Redux app")
        }

        Button(onClick = {
            context.startActivity(Intent(context, MVIActivity::class.java))
        }) {
            Text(text = "goto MVI app")
        }

    }
}

