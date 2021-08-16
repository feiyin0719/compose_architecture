package com.iffly.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Content1(
    count: Int,
    depCount: Int = 0,
    depCount2: Int = 0,
    click: () -> Unit = {},
    click1: () -> Unit = {}
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "The count is $count-$depCount-$depCount2")
        Button(onClick = click) {
            Text(text = "goto screen2")
        }

        Button(onClick = click1) {
            Text(text = "add")
        }
    }
}

@Composable
fun Content2(count: Int, click: () -> Unit) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = click) {
            Text("click ${count}")
        }
    }
}