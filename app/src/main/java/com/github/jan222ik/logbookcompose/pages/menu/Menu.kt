package com.github.jan222ik.logbookcompose.pages.menu

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.InnerPadding
import androidx.ui.material.Button
import androidx.ui.unit.dp
import com.github.jan222ik.logbookcompose.logic.Routing
import com.github.jan222ik.logbookcompose.logic.UIModule
import com.github.zsoltk.compose.router.BackStack

@Composable
fun Menu(
    backStack: BackStack<Routing>,
    module: UIModule.Layout,
    executeSave: (UIModule.Layout) -> Unit
) {
    Column {
        Text("Menu")
        Button(text = { Text("Builder") }, padding = InnerPadding(5.dp), onClick = {
            backStack.push(Routing.Builder)
        })
        Button(text = { Text("Display") }, padding = InnerPadding(5.dp), onClick = {
            backStack.push(
                Routing.Display(
                    module = module
                )
            )
        })
        Button(text = { Text("Save") }, padding = InnerPadding(5.dp), onClick = {
            executeSave(module)
        })
    }
}
