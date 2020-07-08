package com.github.jan222ik.logbookcompose.logic

import com.github.jan222ik.logbookcompose.data.DiveData
import java.time.LocalDate
import java.io.Serializable as S


sealed class UIModule : S {
    sealed class Layout(
        open val children: MutableList<UIModule>
    ) : UIModule(), S {
        data class Column(
            override val children: MutableList<UIModule>,
            val modifier: ModuleModifier.Layout.Column = ModuleModifier.Layout.Column()
        ) : Layout(children), S

        data class Row(
            override val children: MutableList<UIModule>,
            val modifier: ModuleModifier.Layout.Row = ModuleModifier.Layout.Row()
        ) : Layout(children), S

        data class HorizontalTPiece(
            var large: Value<*>,
            var topSmall: Value<*>,
            var bottomSmall: Value<*>
        ) : Layout(mutableListOf(large, topSmall, bottomSmall)), S
    }

    sealed class Value<T>(
        open var labelText: String
    ) : UIModule(), S {
        abstract fun value(data: DiveData): T

        sealed class Text(override var labelText: String) : Value<String>(labelText), S {

            data class SpotName(override var labelText: String = "Spot:") : Text(labelText), S {
                override fun value(data: DiveData): String = data.spotname
            }

            data class DiveNumber(override var labelText: String = "No:") : Text(labelText), S {
                override fun value(data: DiveData): String = data.diveNumber.toString()
            }
        }

        data class Date(
            override var labelText: String = "Date:",
            var format: DateFormat = DateFormat.ddMMYYDashed
        ) : Value<LocalDate>(labelText), S {
            override fun value(data: DiveData): LocalDate = data.date
        }

        data class Duration(
            override var labelText: String = "Duration:"
        ) : Value<Int>(labelText), S {
            override fun value(data: DiveData): Int = data.duration
        }

        sealed class UnitizedDouble(
            override var labelText: String,
            open var unit: DoubleUnit = DoubleUnit.NONE
        ) : Value<Double>(labelText), S {

            data class DepthMAX(
                override var labelText: String = "Maximum Depth:"
            ) : UnitizedDouble(labelText, DoubleUnit.METER), S {
                override fun value(data: DiveData): Double = data.depthMAX
            }

            data class DepthAVG(
                override var labelText: String = "Average Depth"
            ) : UnitizedDouble(labelText, DoubleUnit.METER), S {
                override fun value(data: DiveData): Double = data.depthAVG
            }
        }
    }
}

enum class DoubleUnit(val unitName: String): S  {
    NONE(""), METER("m"), CENTIMETER("cm")
}

enum class DateFormat(val pattern: String): S {
    ddMMYYDashed("dd-MM-YY"), MMddYYDashed("MM-dd-YY"), yyyyMMddDashed("yyyy-MM-dd")
}

sealed class ModuleModifier : S {
    sealed class Layout : ModuleModifier(), S {
        data class Row(
            var scrollable: Boolean = false,
            var onCard: Boolean = false
        ) : Layout(), S

        data class Column(
            var onCard: Boolean = false
        ) : Layout(), S
    }
}
