package com.github.jan222ik.logbookcompose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.Card
import androidx.ui.material.Scaffold
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.github.jan222ik.logbookcompose.components.DateText
import com.github.jan222ik.logbookcompose.components.DisplayUnit
import com.github.jan222ik.logbookcompose.components.DoubleText
import com.github.jan222ik.logbookcompose.components.IntegerText
import com.github.jan222ik.logbookcompose.logic.DiveData
import com.github.jan222ik.logbookcompose.logic.ModuleModifier
import com.github.jan222ik.logbookcompose.logic.UIModule
import com.github.jan222ik.logbookcompose.ui.LogbookComposeTheme
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = demoData()
        val UIModule: UIModule = demoModule(data = data)
        setContent {
            ApplicationEntryPoint(data = data, UIModule = UIModule)
        }
    }
}

fun demoData(): DiveData =
    DiveData(date = LocalDate.now(), depthAVG = 5.5, depthMAX = 20.0, diveNumber = 1, duration = 67)

fun demoModule(data: DiveData): UIModule {
    return UIModule.Layout.Column(
        children = listOf(
            UIModule.Value.DiveNumber(data = data, labelText = "No:"),
            UIModule.Value.Date(data = data),
            UIModule.Layout.Row(
                children = listOf(
                    UIModule.Value.Duration(data = data),
                    UIModule.Value.DepthMAX(data = data),
                    UIModule.Value.DepthAVG(data = data)
                ),
                modifier = ModuleModifier.Layout.Row(scrollable = true)
            )
            //,
            //UIModule.Layout.HorizontalTPiece(
            //    large = UIModule.Value.DiveNumber(data = data, labelText = "LARGE"),
            //    bottomSmall = UIModule.Value.DepthMAX(
            //        data = data,
            //        labelText = "SMALL BOTTOM"
            //    ),
            //    topSmall = UIModule.Value.DepthAVG(data = data, labelText = "SMALL TOP")
            //),
            //UIModule.Layout.HorizontalTPiece(
            //    large = UIModule.Value.Date(data),
            //            bottomSmall = UIModule.Value.DepthMAX(
            //            data = data
            //),
            //topSmall = UIModule.Value.DepthAVG(data = data, labelText = "SMALL TOP")
            //)
        )
    )
}

@Preview(widthDp = 411)
@Composable
fun previewWith411dpWidth() {
    val data = demoData()
    //ApplicationEntryPoint(data = data, UIModule = demoModule(data = data))
    LogbookComposeTheme(darkTheme = true) {
        UIModuleProcessor(UIModule = demoModule(data = data))
    }
}

@Preview(widthDp = 487)
@Composable
fun previewOpenWidth() {
    val data = demoData()
    //ApplicationEntryPoint(data = data, UIModule = demoModule(data = data))
    LogbookComposeTheme(darkTheme = true) {
        UIModuleProcessor(UIModule = demoModule(data = data))
    }
}

@Composable
fun ApplicationEntryPoint(data: DiveData, UIModule: UIModule) {
    LogbookComposeTheme(darkTheme = !true) {
        Scaffold(
            bodyContent = {
                UIModuleProcessor(UIModule = UIModule)
            }
        )
    }
}

@Composable
fun UIModuleProcessor(UIModule: UIModule) {
    when (UIModule) {
        is UIModule.Layout -> ApplyLayoutModule(layModule = UIModule)
        is UIModule.Value -> CardModule(valModule = UIModule)
    }
}

@Composable
fun ApplyValueModule(UIModule: UIModule.Value) {
    CardModule(valModule = UIModule)
}

@Composable
fun TextDisplaysOf(valModule: UIModule.Value) {
    val data = valModule.data
    when (valModule) {
        is UIModule.Value.DiveNumber -> IntegerText(value = data.diveNumber)
        is UIModule.Value.Date -> DateText(date = data.date)
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
    }
}

@Composable
fun CardModule(valModule: UIModule.Value) {
    Card {
        Row {
            Text(text = valModule.labelText, modifier = Modifier.padding(5.dp))
            TextDisplaysOf(valModule = valModule)
        }
    }
}

@Composable
fun ApplyLayoutModule(layModule: UIModule.Layout) {
    val processChildren = @Composable{ children: List<UIModule> ->
        for (it in children) {
            UIModuleProcessor(UIModule = it)
        }
    }
    when (layModule) {
        is UIModule.Layout.Column -> Column {
            processChildren(layModule.children)
        }
        is UIModule.Layout.Row -> {
            if (layModule.modifier.scrollable) {
                HorizontalScroller() { processChildren(layModule.children) }
            } else Row { processChildren(layModule.children)}
        }
        is UIModule.Layout.HorizontalTPiece -> Row(modifier = Modifier.fillMaxWidth()) {
            UIModuleProcessor(UIModule = layModule.large)
            Column {
                UIModuleProcessor(UIModule = layModule.topSmall)
                UIModuleProcessor(UIModule = layModule.bottomSmall)
            }
        }
    }
}