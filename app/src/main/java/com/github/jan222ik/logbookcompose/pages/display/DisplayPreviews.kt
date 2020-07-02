package com.github.jan222ik.logbookcompose.pages.display

import androidx.compose.Composable
import androidx.ui.tooling.preview.Preview
import com.github.jan222ik.logbookcompose.data.Demo
import com.github.jan222ik.logbookcompose.pages.display.UIModuleProcessor
import com.github.jan222ik.logbookcompose.ui.LogbookComposeTheme

@Preview(widthDp = 411)
@Composable
fun previewWith411dpWidth() {
    val data = Demo.demoData()
    LogbookComposeTheme(darkTheme = true) {
        UIModuleProcessor(
            UIModule = Demo.demoModule(),
            data = data
        )
    }
}

@Preview(widthDp = 487)
@Composable
fun previewOpenWidth() {
    val data = Demo.demoData()
    LogbookComposeTheme(darkTheme = true) {
        UIModuleProcessor(
            UIModule = Demo.demoModule(),
            data = data
        )
    }
}
