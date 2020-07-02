package com.github.jan222ik.logbookcompose.logic

sealed class Routing {
    object Builder : Routing()
    data class Display(val module: UIModule.Layout) : Routing()
    object Menu : Routing()
}