package com.github.jan222ik.logbookcompose.data

import com.github.jan222ik.logbookcompose.logic.ModuleModifier
import com.github.jan222ik.logbookcompose.logic.UIModule
import java.time.LocalDate

object Demo {
    fun demoData(): DiveData =
        DiveData(
            date = LocalDate.of(2019, 9, 15),
            depthAVG = 5.1,
            depthMAX = 17.4,
            diveNumber = 227,
            duration = 87,
            spotname = "Sunken City"
        )

    fun demoModule(): UIModule.Layout {
        return UIModule.Layout.Column(
            children = mutableListOf(
                UIModule.Value.Text.DiveNumber(labelText = "No:"),
                UIModule.Value.Date(),
                UIModule.Layout.Row(
                    children = mutableListOf(
                        UIModule.Value.Duration(),
                        UIModule.Value.UnitizedDouble.DepthMAX(),
                        UIModule.Value.UnitizedDouble.DepthAVG()
                    ),
                    modifier = ModuleModifier.Layout.Row(scrollable = true)
                )
            )
        )
    }
}
