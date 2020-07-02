package com.github.jan222ik.logbookcompose.logic

import java.io.Serializable as S


sealed class UIModule : S {
    sealed class Layout(open val children: MutableList<UIModule>) :
        UIModule(), S {
        data class Column(
            override val children: MutableList<UIModule>
        ) : Layout(children), S

        data class Row(
            override val children: MutableList<UIModule>,
            val modifier: ModuleModifier.Layout.Row = ModuleModifier.Layout.Row()
        ) : Layout(children), S

        data class HorizontalTPiece(
            var large: UIModule,
            var topSmall: UIModule,
            var bottomSmall: UIModule
        ) : Layout(mutableListOf(large, topSmall, bottomSmall)), S
    }

    sealed class Value(open var labelText: String) : UIModule(), S {

        data class DiveNumber(override var labelText: String = "No:") : Value(labelText), S

        data class Date(override var labelText: String = "Date:") : Value(labelText), S

        data class Duration(override var labelText: String = "Duration:") : Value(labelText), S

        data class DepthMAX(override var labelText: String = "Maximum Depth:") : Value(labelText), S

        data class DepthAVG(override var labelText: String = "Average Depth") : Value(labelText), S

        data class SpotName(override var labelText: String = "Spot:") : Value(labelText), S
    }
}

sealed class ModuleModifier : S {
    sealed class Layout : ModuleModifier(), S {
        data class Row(var scrollable: Boolean = false) : Layout(), S
    }
}