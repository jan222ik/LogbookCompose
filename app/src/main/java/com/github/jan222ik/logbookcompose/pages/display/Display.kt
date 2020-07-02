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
import androidx.ui.unit.dp
import com.github.jan222ik.logbookcompose.data.DiveData
import com.github.jan222ik.logbookcompose.logic.UIModule

@Composable
fun Display(module: UIModule, data: DiveData) {
    Text("Display")
    UIModuleProcessor(
        UIModule = module,
        data = data
    )
}

@Composable
fun UIModuleProcessor(UIModule: UIModule, data: DiveData) {
    when (UIModule) {
        is UIModule.Layout -> ApplyLayoutModule(
            layModule = UIModule,
            data = data
        )
        is UIModule.Value -> CardModule(
            valModule = UIModule,
            data = data
        )
    }
}

@Composable
fun TextDisplaysOf(valModule: UIModule.Value, data: DiveData) {
    when (valModule) {
        is UIModule.Value.DiveNumber -> IntegerText(
            value = data.diveNumber
        )
        is UIModule.Value.Date -> DateText(
            date = data.date
        )
        is UIModule.Value.DepthMAX -> DoubleText(
            value = data.depthMAX,
            displayUnit = DisplayUnit.Meter
        )
        is UIModule.Value.DepthAVG -> DoubleText(
            value = data.depthAVG,
            displayUnit = DisplayUnit.Meter
        )
        is UIModule.Value.Duration -> IntegerText(
            value = data.duration,
            displayUnit = DisplayUnit.Minutes
        )
        is UIModule.Value.SpotName -> Text(
            text = data.spotname,
            modifier = Modifier.padding(5.dp)
        )
    }
}

@Composable
fun CardModule(valModule: UIModule.Value, data: DiveData) {
    Card {
        Row {
            Text(text = valModule.labelText, modifier = Modifier.padding(5.dp))
            TextDisplaysOf(
                valModule = valModule,
                data = data
            )
        }
    }
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
        is UIModule.Layout.Column -> Column {
            processChildren(layModule.children)
        }
        is UIModule.Layout.Row -> {
            if (layModule.modifier.scrollable) {
                HorizontalScroller { processChildren(layModule.children) }
            } else Row { processChildren(layModule.children) }
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