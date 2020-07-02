package com.github.jan222ik.logbookcompose.logic

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ModuleSerializer(private val context: Context) {
    private val fileName = "definition0.cb"

    fun loadModule(): UIModule.Layout? = try {
        ObjectInputStream(context.openFileInput(fileName))
            .use {
                it.readObject() as UIModule.Layout
            }
    } catch (e: Exception) {
        null
    }

    fun saveModule(module: UIModule.Layout): Boolean = try {
        ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            .use {
                it.writeObject(module)
            }
        true
    } catch (e: Exception) {
        false
    }
}
