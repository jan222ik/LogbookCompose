package com.github.jan222ik.logbookcompose.pages.builder

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.drawShadow
import androidx.ui.foundation.Border
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
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
import androidx.ui.unit.dp
import com.github.jan222ik.logbookcompose.logic.DateFormat
import com.github.jan222ik.logbookcompose.logic.DoubleUnit
import com.github.jan222ik.logbookcompose.logic.Routing
import com.github.jan222ik.logbookcompose.logic.UIModule
import com.github.zsoltk.compose.router.BackStack

@Composable
fun Builder(module: UIModule, backStack: BackStack<Routing>) {
    Column {
        Text("Builder")
        Divider()
        createBuilderTreeFromModule(
            module = module,
            backStack = backStack
        )
    }
}


const val LAYER_OFFSET_FACTOR = 5


val iconButtonModifier = Modifier.drawShadow(3.dp, CircleShape)

enum class PopupStates {
    NONE, LAYOUT_ADD, LAYOUT_MODIFIERS, VALUE_MODIFIERS, VALUE_SELECT_TYPE
}

enum class ValueModuleNames(val displayName: String, val generator: () -> UIModule.Value<*>) {
    DiveNumber("Number of Dive", { UIModule.Value.Text.DiveNumber() }),
    Date("Date", { UIModule.Value.Date() }),
    Duration("Duration", { UIModule.Value.Duration() }),
    DepthMAX("Maximum Depth", { UIModule.Value.UnitizedDouble.DepthMAX() }),
    DepthAVG("Average Depth", { UIModule.Value.UnitizedDouble.DepthAVG() }),
    SpotName("Spotname", { UIModule.Value.Text.SpotName() })
}

private fun fromDisplayName(searchFor: String) =
    ValueModuleNames.values().first { it.displayName == searchFor }

@Composable
fun createBuilderTreeFromModule(
    module: UIModule,
    backStack: BackStack<Routing>,
    layer: Int = 0,
    deleteInParent: (() -> Unit)? = null
) {
    val icons = Icons.Rounded
    val (showAlert, setShowAlert) = state { PopupStates.NONE }
    if (module is UIModule.Layout) {
        val (modState, _) = state {
            @Suppress("USELESS_CAST")
            module as UIModule.Layout
        }
        val (children, setChildren) = state { modState.children }

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
                    IconButton(
                        modifier = iconButtonModifier,
                        icon = { Icon(icons.Visibility) },
                        onClick = {
                            backStack.push(
                                Routing.Display(
                                    modState
                                )
                            )
                        })
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
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            modifier = iconButtonModifier,
                            icon = { Icon(icons.PostAdd) },
                            onClick = { setShowAlert(PopupStates.LAYOUT_ADD) })
                        if (modState is UIModule.Layout.Row) {
                            IconButton(
                                modifier = iconButtonModifier,
                                icon = { Icon(icons.EditAttributes) },
                                onClick = { setShowAlert(PopupStates.LAYOUT_MODIFIERS) })
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
                if (showAlert == PopupStates.LAYOUT_MODIFIERS && modState is UIModule.Layout.Row) {
                    val (temp, setTemp) = state { modState.modifier.scrollable }
                    AlertDialog(
                        onCloseRequest = { setShowAlert(PopupStates.NONE) },
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
                if (showAlert == PopupStates.VALUE_SELECT_TYPE) {
                    AlertDialog(
                        onCloseRequest = { setShowAlert(PopupStates.NONE) },
                        text = { Text("Choose a Element") },
                        buttons = {
                            Column {
                                Text("Type")
                                val radioOptions = ValueModuleNames.values().map { it.displayName }
                                val (selectedOption, onOptionSelected) = state { radioOptions[0] }
                                RadioGroup(
                                    options = radioOptions,
                                    selectedOption = selectedOption,
                                    onSelectedChange = onOptionSelected
                                )
                                Button(
                                    text = { Text("Apply") },
                                    onClick = {
                                        setShowAlert(PopupStates.NONE)
                                        val toAdd = fromDisplayName(
                                            selectedOption
                                        ).generator()
                                        modState.children.add(toAdd)
                                        setChildren((mutableListOf<UIModule>()).also {
                                            it.addAll(
                                                children
                                            )
                                        })
                                    }
                                )
                            }
                        }
                    )
                }
                if (showAlert == PopupStates.LAYOUT_ADD) {
                    fun handleSelect(addModule: UIModule) {
                        setShowAlert(PopupStates.NONE)
                        modState.children.add(addModule)
                        setChildren((mutableListOf<UIModule>()).also { it.addAll(children) })
                    }
                    AlertDialog(
                        onCloseRequest = { setShowAlert(PopupStates.NONE) },
                        text = { Text("Choose a Element") },
                        buttons = {
                            Column {
                                Button(
                                    text = { Text("Row") },
                                    onClick = {
                                        handleSelect(
                                            UIModule.Layout.Row(
                                                mutableListOf()
                                            )
                                        )
                                    })
                                Button(
                                    text = { Text("Col") },
                                    onClick = {
                                        handleSelect(
                                            UIModule.Layout.Column(
                                                mutableListOf()
                                            )
                                        )
                                    })
                                Button(
                                    text = { Text("Value") },
                                    onClick = { setShowAlert(PopupStates.VALUE_SELECT_TYPE) })
                            }
                        }
                    )
                }
                for (child in children) {
                    createBuilderTreeFromModule(
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
        val (modState, _) = state { module as UIModule.Value<*> }
        Row(
            modifier = Modifier.gravity(align = Alignment.Start),
            verticalGravity = Alignment.CenterVertically
        ) {
            Text("Metric: ")
            when (modState) {
                is UIModule.Value.Text.DiveNumber -> Text(text = "Number of Dive")
                is UIModule.Value.Date -> Text(text = "Date")
                is UIModule.Value.UnitizedDouble.DepthMAX -> Text(text = "Max Depth")
                is UIModule.Value.UnitizedDouble.DepthAVG -> Text(text = "AVG Depth")
                is UIModule.Value.Duration -> Text(text = "Duration")
                is UIModule.Value.Text.SpotName -> Text(text = "Spotname")
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    modifier = iconButtonModifier,
                    icon = { Icon(icons.EditAttributes) },
                    onClick = { setShowAlert(PopupStates.VALUE_MODIFIERS) }
                )
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
        if (showAlert == PopupStates.VALUE_MODIFIERS) {
            val (labelLocal, setLabelLocal) = state { modState.labelText }
            AlertDialog(
                onCloseRequest = { setShowAlert(PopupStates.NONE) },
                text = { Text("Modify Metric Display") },
                buttons = {
                    Column {
                        FilledTextField(
                            value = labelLocal,
                            onValueChange = { setLabelLocal(it) },
                            label = { Text("Text of Label") }
                        )
                        val onSaveHandle: () -> Unit = when (modState) {
                            is UIModule.Value.Date -> {
                                Text(text = "Date Formats")
                                val dateFormatOptions = DateFormat.values().map { it.name }
                                val (selectedDateFormat, onDateFormatSelected) = state {
                                    dateFormatOptions[dateFormatOptions.indexOf(
                                        modState.format.name
                                    )]
                                }
                                RadioGroup(
                                    options = dateFormatOptions,
                                    selectedOption = selectedDateFormat,
                                    onSelectedChange = onDateFormatSelected
                                )
                                fun() {
                                    modState.format = DateFormat.valueOf(selectedDateFormat)
                                }
                            }
                            is UIModule.Value.UnitizedDouble -> {
                                Text(text = "Unit Options")
                                val doubleUnitOptions = DoubleUnit.values().map { it.name }
                                val (selectedDoubleUnit, onDoubleUnitSelected) = state {
                                    doubleUnitOptions[doubleUnitOptions.indexOf(
                                        modState.unit.name
                                    )]
                                }
                                RadioGroup(
                                    options = doubleUnitOptions,
                                    selectedOption = selectedDoubleUnit,
                                    onSelectedChange = onDoubleUnitSelected
                                )
                                fun() {
                                    modState.unit = DoubleUnit.valueOf(selectedDoubleUnit)
                                }
                            }
                            else -> {
                                fun() = Unit
                            }
                        }
                        Button(
                            text = { Text("Apply") },
                            onClick = {
                                modState.labelText = labelLocal;
                                onSaveHandle()
                                setShowAlert(PopupStates.NONE)
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun DateFormatRadioGroup() {
    val dateFormatOptions = DateFormat.values().map { it.name }
    val (selectedDateFormat, onDateFormatSelected) = state { dateFormatOptions[0] }
    RadioGroup(
        options = dateFormatOptions,
        selectedOption = selectedDateFormat,
        onSelectedChange = onDateFormatSelected
    )
}
