package com.github.jan222ik.logbookcompose.logic

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ModuleSerializer(private val context: Context) {
    private val fileExtension = ".cb"

    fun listDefinitions(): List<String> =
        context.dataDir.listFiles()[2].listFiles().filter{file -> file != null && file.extension == "cb" }.map { it.name.removeSuffix(fileExtension) }

    fun loadModule(fileName: String): UIModule.Layout? = try {
        ObjectInputStream(context.openFileInput(fileName + fileExtension))
            .use {
                it.readObject() as UIModule.Layout
            }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    fun saveModule(module: UIModule.Layout, fileName: String): Boolean = try {
        ObjectOutputStream(context.openFileOutput(fileName + fileExtension, Context.MODE_PRIVATE))
            .use {
                it.writeObject(module)
            }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
