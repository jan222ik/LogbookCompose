package com.github.jan222ik.logbookcompose.pages.display

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.Card
import androidx.ui.material.Divider
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.github.jan222ik.logbookcompose.data.Demo
import com.github.jan222ik.logbookcompose.data.DiveData
import com.github.jan222ik.logbookcompose.logic.UIModule

@Composable
fun Display(module: UIModule, data: DiveData) {
    Column {
        Text("Display")
        Divider()
        UIModuleProcessor(
            UIModule = module,
            data = data
        )
    }
}

@Composable
fun UIModuleProcessor(UIModule: UIModule, data: DiveData) {
    when (UIModule) {
        is UIModule.Layout -> ApplyLayoutModule(
            layModule = UIModule,
            data = data
        )
        is UIModule.Value<*> -> CardModule(
            valModule = UIModule,
            data = data
        )
    }
}

@Composable
fun TextDisplaysOf(valModule: UIModule.Value<*>, data: DiveData) {
    when (valModule) {
        is UIModule.Value.Text -> Text(
            text = valModule.value(data),
            modifier = Modifier.padding(5.dp)
        )
        is UIModule.Value.Date -> DateText(
            date = valModule.value(data),
            formatPattern = valModule.format.pattern
        )
        is UIModule.Value.Duration -> IntegerText(
            value = valModule.value(data),
            displayUnit = DisplayUnit.Minutes
        )
        is UIModule.Value.UnitizedDouble -> DoubleText(
            value = valModule.value(data),
            displayUnit = valModule.unit
        )
    }
}

@Composable
fun CardModule(valModule: UIModule.Value<*>, data: DiveData) {
    Card(
        elevation = 10.dp,
        modifier = Modifier.padding(3.dp)
    ) {
        Row {
            Text(text = valModule.labelText, modifier = Modifier.padding(5.dp))
            TextDisplaysOf(
                valModule = valModule,
                data = data
            )
        }
    }
}

@Preview
@Composable
fun preview() {
    ApplyLayoutModule(layModule = Demo.demoModule(), data = Demo.demoData())
}

@Composable
fun ApplyLayoutModule(layModule: UIModule.Layout, data: DiveData) {
    val processChildren = @Composable { children: List<UIModule> ->
        for (it in children) {
            UIModuleProcessor(
                UIModule = it,
                data = data
            )
        }
    }
    when (layModule) {
        is UIModule.Layout.Column ->
            if (layModule.modifier.onCard) {
                Card {
                    Column { processChildren(layModule.children) }
                }
            } else Column { processChildren(layModule.children) }

        is UIModule.Layout.Row -> {
            if (layModule.modifier.onCard) {
                Card {
                    if (layModule.modifier.scrollable) {
                        Row {
                            processChildren(layModule.children)
                        }
                    } else Row { processChildren(layModule.children) }
                }
            } else {
                if (layModule.modifier.scrollable) {
                    Row {
                        processChildren(layModule.children)
                    }
                } else Row { processChildren(layModule.children) }
            }
        }
        is UIModule.Layout.HorizontalTPiece -> Row(modifier = Modifier.fillMaxWidth()) {
            UIModuleProcessor(
                UIModule = layModule.large,
                data = data
            )
            Column {
                UIModuleProcessor(
                    UIModule = layModule.topSmall,
                    data = data
                )
                UIModuleProcessor(
                    UIModule = layModule.bottomSmall,
                    data = data
                )
            }
        }
    }
}
