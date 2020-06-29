package com.github.jan222ik.logbookcompose.components

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.material.Card
import androidx.ui.material.FilledTextField
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateText(date: LocalDate, formatPattern: String = "dd-MM-yy") {
    Text(modifier = Modifier.padding(5.dp), text = date.format(DateTimeFormatter.ofPattern(formatPattern)))
}

@Preview
@Composable
fun MetricDatePreview() {
    Column {
        DateText(date = LocalDate.now())
        DateText(date = LocalDate.now(), formatPattern = "dd-MM")
        DateText(date = LocalDate.now(), formatPattern = "yyyy-MM")
    }
}

@Composable
fun IntegerText(value: Int, displayUnit: DisplayUnit = DisplayUnit.None) {
    Text(modifier = Modifier.padding(5.dp), text = "$value ${displayUnit.unitName}")
}

@Preview
@Composable
fun IntegerTextPreview() {
    Column {
        IntegerText(value = 19)
        IntegerText(value = 19, displayUnit = DisplayUnit.Meter)
        IntegerText(value = 19, displayUnit = DisplayUnit.CentiMeter)
    }
}

@Composable
fun DoubleText(value: Double, displayUnit: DisplayUnit = DisplayUnit.None) {
    Text(modifier = Modifier.padding(5.dp), text = "$value ${displayUnit.unitName}")
}

@Preview
@Composable
fun DoubleTextPreview() {
    Column {
        DoubleText(value = 19.3)
        DoubleText(value = 19.3, displayUnit = DisplayUnit.Meter)
        DoubleText(value = 19.3, displayUnit = DisplayUnit.CentiMeter)
    }
}

enum class DisplayUnit(val unitName: String) {
    None(""), Meter("m"), CentiMeter("cm"), Minutes("min")
}