package com.github.jan222ik.logbookcompose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.Providers
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.layout.ColumnScope.gravity
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.rounded.Delete
import androidx.ui.material.icons.rounded.EditAttributes
import androidx.ui.material.icons.rounded.PostAdd
import androidx.ui.material.icons.rounded.Visibility
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
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.router.BackStack
import com.github.zsoltk.compose.router.Router
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val backPressHandler = BackPressHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = demoData()
        setContent {
            val module = demoModule(data)
            LogbookComposeTheme(darkTheme = !true) {
                Providers(AmbientBackPressHandler provides backPressHandler) {
                    Router(defaultRouting = Routing.Menu as Routing) { backStack ->
                        when (val current = backStack.last()) {
                            is Routing.Menu -> Menu(backStack = backStack, module = module)
                            is Routing.Display -> Display(module = current.module)
                            is Routing.Builder -> Builder(module = module, backStack = backStack)
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!backPressHandler.handle()) {
            super.onBackPressed()
        }
    }
}

sealed class Routing {
    object Builder : Routing()
    data class Display(val module: UIModule.Layout) : Routing()
    object Menu : Routing()
}

@Composable
fun Menu(backStack: BackStack<Routing>, module: UIModule.Layout) {
    Column {
        Text("Menu")
        Button(text = { Text("Builder") }, padding = InnerPadding(5.dp), onClick = {
            backStack.push(Routing.Builder)
        })
        Button(text = { Text("Display") }, padding = InnerPadding(5.dp), onClick = {
            backStack.push(Routing.Display(module = module))
        })
    }
}

@Composable
fun Builder(module: UIModule, backStack: BackStack<Routing>) {
    Column {
        Text("Builder")
        Divider()
        buildNodeTreeFromModule(module, backStack = backStack)
    }
}

const val LAYER_OFFSET_FACTOR = 5


val iconButtonModifier = Modifier.drawBorder(Border(1.dp, Color.Black), CircleShape)

@Composable
fun buildNodeTreeFromModule(
    module: UIModule,
    backStack: BackStack<Routing>,
    layer: Int = 0,
    deleteInParent: (() -> Unit)? = null
) {
    val icons = Icons.Rounded
    if (module is UIModule.Layout) {
        val (modState, setModState) = state { module as UIModule.Layout }
        val (children, setChildren) = state { modState.children }
        val (addDisplayDialog, setAddDisplayDialog) = state { false }
        val (modifierDisplayDialog, setModifierDisplayDialog) = state { false }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (layer * LAYER_OFFSET_FACTOR).dp)
                .padding(5.dp),
            elevation = (layer * LAYER_OFFSET_FACTOR).dp,
            border = Border(1.dp, Color.Black)
        ) {
            Column(Modifier.padding(5.dp)) {
                Row(
                    modifier = Modifier.gravity(align = Alignment.Start),
                    verticalGravity = Alignment.CenterVertically
                ) {
                    when (modState) {
                        is UIModule.Layout.Column -> Text(
                            "Column",
                            modifier = Modifier.preferredHeight(24.dp)
                        )
                        is UIModule.Layout.Row -> Text(
                            "Row",
                            modifier = Modifier.preferredHeight(24.dp)
                        )
                        is UIModule.Layout.HorizontalTPiece -> Text(
                            "Horizontal T",
                            modifier = Modifier.preferredHeight(24.dp)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            modifier = iconButtonModifier,
                            icon = { Icon(icons.Visibility) },
                            onClick = { backStack.push(Routing.Display(modState)) })
                        IconButton(
                            modifier = iconButtonModifier,
                            icon = { Icon(icons.PostAdd) },
                            onClick = { setAddDisplayDialog(true) })
                        if (modState is UIModule.Layout.Row) {
                            IconButton(
                                modifier = iconButtonModifier,
                                icon = { Icon(icons.EditAttributes) },
                                onClick = { setModifierDisplayDialog(true) })
                        }
                        if (deleteInParent != null) {
                            IconButton(
                                modifier = iconButtonModifier,
                                icon = { Icon(icons.Delete) },
                                onClick = {
                                    deleteInParent()
                                })
                        }
                    }
                }
                if (modifierDisplayDialog && modState is UIModule.Layout.Row) {
                    val (temp, setTemp) = state { modState.modifier.scrollable }
                    AlertDialog(
                        onCloseRequest = { setModifierDisplayDialog(false) },
                        text = { Text("Choose a Element") },
                        buttons = {
                            Column {
                                Text("Scrollable")
                                Checkbox(
                                    checked = temp,
                                    onCheckedChange = {
                                        val b = !modState.modifier.scrollable
                                        modState.modifier.scrollable = b
                                        setTemp(b)
                                    }
                                )
                            }
                        }
                    )
                }
                if (addDisplayDialog) {
                    fun handleSelect(addModule: UIModule) {
                        setAddDisplayDialog(false)
                        modState.children.add(addModule)
                        setChildren((mutableListOf<UIModule>()).also { it.addAll(children) })
                    }
                    AlertDialog(
                        onCloseRequest = { setAddDisplayDialog(false) },
                        text = { Text("Choose a Element") },
                        buttons = {
                            Column() {
                                Button(
                                    text = { Text("Row") },
                                    onClick = {
                                        handleSelect(
                                            UIModule.Layout.Row(
                                                modState.data,
                                                mutableListOf()
                                            )
                                        )
                                    })
                                Button(
                                    text = { Text("Col") },
                                    onClick = {
                                        handleSelect(
                                            UIModule.Layout.Column(
                                                modState.data,
                                                mutableListOf()
                                            )
                                        )
                                    })
                                Button(
                                    text = { Text("Value") },
                                    onClick = { handleSelect(UIModule.Value.Date(modState.data)) })
                            }
                        }
                    )
                }
                for (child in children) {
                    buildNodeTreeFromModule(
                        module = child,
                        backStack = backStack,
                        layer = layer.inc(),
                        deleteInParent = {
                            children.remove(child)
                            setChildren(children.toMutableList())
                        }
                    )
                }
            }
        }
    } else {
        val modState: MutableState<UIModule.Value> = state { module as UIModule.Value }
        Row(
            modifier = Modifier.gravity(align = Alignment.Start),
            verticalGravity = Alignment.CenterVertically
        ) {
            Text("Metric: ")
            when (modState.component1()) {
                is UIModule.Value.DiveNumber -> Text(text = "Number of Dive")
                is UIModule.Value.Date -> Text(text = "Date")
                is UIModule.Value.DepthMAX -> Text(text = "Max Depth")
                is UIModule.Value.DepthAVG -> Text(text = "AVG Depth")
                is UIModule.Value.Duration -> Text(text = "Duration")
            }
            if (deleteInParent != null) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    modifier = iconButtonModifier,
                    icon = { Icon(icons.Delete) },
                    onClick = {
                        deleteInParent()
                    })}
            }
        }
    }
}

@Composable
fun Display(module: UIModule) {
    Text("Display")
    UIModuleProcessor(UIModule = module)
}


fun demoData(): DiveData =
    DiveData(date = LocalDate.now(), depthAVG = 5.5, depthMAX = 20.0, diveNumber = 1, duration = 67)

fun demoModule(data: DiveData): UIModule.Layout {
    return UIModule.Layout.Column(
        data = data,
        children = mutableListOf(
            UIModule.Value.DiveNumber(data = data, labelText = "No:"),
            UIModule.Value.Date(data = data),
            UIModule.Layout.Row(
                data = data,
                children = mutableListOf(
                    UIModule.Value.Duration(data = data),
                    UIModule.Value.DepthMAX(data = data),
                    UIModule.Value.DepthAVG(data = data)
                ),
                modifier = ModuleModifier.Layout.Row(scrollable = true)
            )
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
    val processChildren = @Composable { children: List<UIModule> ->
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
            } else Row { processChildren(layModule.children) }
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