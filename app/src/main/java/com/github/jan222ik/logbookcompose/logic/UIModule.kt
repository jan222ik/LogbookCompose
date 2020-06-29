package com.github.jan222ik.logbookcompose.logic

import java.time.LocalDate

sealed class UIModule {
    sealed class Layout : UIModule() {
        data class Column(val children: List<UIModule>) : Layout()
        data class Row(val children: List<UIModule>, val modifier: ModuleModifier.Layout.Row = ModuleModifier.Layout.Row()) : Layout()
        data class HorizontalTPiece(
            val large: UIModule,
            val topSmall: UIModule,
            val bottomSmall: UIModule
        ) : Layout()
    }

    sealed class Value(open val data: DiveData, open val labelText: String) : UIModule() {

        data class DiveNumber(override val data: DiveData, override val labelText: String = "No:") :
            Value(data, labelText)

        data class Date(override val data: DiveData, override val labelText: String = "Date:") :
            Value(data, labelText)

        data class Duration(
            override val data: DiveData,
            override val labelText: String = "Duration:"
        ) :
            Value(data, labelText)

        data class DepthMAX(
            override val data: DiveData,
            override val labelText: String = "Maximum Depth:"
        ) : Value(data, labelText)

        data class DepthAVG(
            override val data: DiveData,
            override val labelText: String = "Average Depth"
        ) : Value(data, labelText)
    }
}

sealed class ModuleModifier {
    sealed class Layout : ModuleModifier() {
        data class Row(val scrollable: Boolean = false) : Layout()
    }
}

data class DiveData(
    val diveNumber: Int = 0,
    val depthMAX: Double = 0.0,
    val depthAVG: Double = 0.0,
    val date: LocalDate = LocalDate.MIN,
    val duration: Int = 0
)