package com.github.jan222ik.logbookcompose.pages.menu

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.layout.Column
import androidx.ui.layout.InnerPadding
import androidx.ui.layout.Row
import androidx.ui.material.Button
import androidx.ui.material.FilledTextField
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.github.jan222ik.logbookcompose.logic.Routing
import com.github.jan222ik.logbookcompose.logic.UIModule
import com.github.zsoltk.compose.router.BackStack
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun Menu(
    backStack: BackStack<Routing>,
    module: UIModule.Layout,
    executeSave: (UIModule.Layout, String) -> Unit,
    executeLoad: (String) -> Unit,
    existingDefinitions: List<String>,
    currentDefinitionState: MutableState<String?>
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
        Text("Saving:")
        Row {
            val (saveName, setSaveName) = state { currentDefinitionState.component1() ?:
                "definition${LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm")
                )}"
            }
            Button(text = { Text("Save") }, padding = InnerPadding(5.dp), onClick = {
                executeSave(module, saveName)
            })
            Text("as")
            FilledTextField(
                value = saveName,
                onValueChange = { setSaveName(it) },
                label = { Text("Name of Save") }
            )
        }
        Text("Loading:")
        LazyColumnItems(items = existingDefinitions) {
            Text(text= it, modifier = Modifier.clickable(onClick = {
                executeLoad(it)
            }),
            color = if (currentDefinitionState.component1() == null || it != currentDefinitionState.component1())
                MaterialTheme.colors.onSurface else MaterialTheme.colors.secondary)
        }
    }
}
