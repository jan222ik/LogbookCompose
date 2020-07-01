package com.github.jan222ik.logbookcompose.logic

import java.time.LocalDate


sealed class UIModule(open var data: DiveData) {
    sealed class Layout(override var data: DiveData, open val children: MutableList<UIModule>) :
        UIModule(data) {
        data class Column(
            override var data: DiveData,
            override val children: MutableList<UIModule>
        ) : Layout(data, children)

        data class Row(
            override var data: DiveData,
            override val children: MutableList<UIModule>,
            val modifier: ModuleModifier.Layout.Row = ModuleModifier.Layout.Row()
        ) : Layout(data, children)

        data class HorizontalTPiece(
            override var data: DiveData,
            var large: UIModule,
            var topSmall: UIModule,
            var bottomSmall: UIModule
        ) : Layout(data, mutableListOf(large, topSmall, bottomSmall))
    }

    sealed class Value(override var data: DiveData, open var labelText: String) : UIModule(data) {

        data class DiveNumber(override var data: DiveData, override var labelText: String = "No:") :
            Value(data, labelText)

        data class Date(override var data: DiveData, override var labelText: String = "Date:") :
            Value(data, labelText)

        data class Duration(
            override var data: DiveData,
            override var labelText: String = "Duration:"
        ) :
            Value(data, labelText)

        data class DepthMAX(
            override var data: DiveData,
            override var labelText: String = "Maximum Depth:"
        ) : Value(data, labelText)

        data class DepthAVG(
            override var data: DiveData,
            override var labelText: String = "Average Depth"
        ) : Value(data, labelText)
    }
}

sealed class ModuleModifier {
    sealed class Layout : ModuleModifier() {
        data class Row(var scrollable: Boolean = false) : Layout()
    }
}

data class DiveData(
    val diveNumber: Int = 0,
    val depthMAX: Double = 0.0,
    val depthAVG: Double = 0.0,
    val date: LocalDate = LocalDate.MIN,
    val duration: Int = 0
)