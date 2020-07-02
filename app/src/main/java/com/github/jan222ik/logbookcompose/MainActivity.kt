package com.github.jan222ik.logbookcompose

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Providers
import androidx.compose.state
import androidx.ui.core.setContent
import androidx.ui.material.Scaffold
import com.github.jan222ik.logbookcompose.data.Demo
import com.github.jan222ik.logbookcompose.logic.ModuleSerializer
import com.github.jan222ik.logbookcompose.logic.Routing
import com.github.jan222ik.logbookcompose.pages.builder.Builder
import com.github.jan222ik.logbookcompose.pages.display.Display
import com.github.jan222ik.logbookcompose.pages.menu.Menu
import com.github.jan222ik.logbookcompose.ui.LogbookComposeTheme
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.router.Router

class MainActivity : AppCompatActivity() {

    private val backPressHandler = BackPressHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = Demo.demoData()
        val serializer = ModuleSerializer(applicationContext)
        setContent {
            val currentDefinitionState = state<String?> { null }
            val (currentDefinition, setCurrentDefinition) = currentDefinitionState
            val existingDefinitions = serializer.listDefinitions()
            val modState = state { (if (existingDefinitions.isNotEmpty()) {
                val fileName = existingDefinitions[0]
                serializer.loadModule(fileName).also { setCurrentDefinition(existingDefinitions[0]) }
            } else null).also { notifyActionResult("Loading", it != null) } ?: Demo.demoModule()}
            val (module, setModule) = modState
                LogbookComposeTheme(darkTheme = true) {
                Scaffold(
                    bodyContent = {
                        Providers(AmbientBackPressHandler provides backPressHandler) {
                            Router(defaultRouting = Routing.Menu as Routing) { backStack ->
                                when (val current = backStack.last()) {
                                    is Routing.Menu -> Menu(
                                        module = module,
                                        backStack = backStack,
                                        executeSave = { it, saveName ->
                                            serializer.saveModule(it, saveName).also { b ->
                                                notifyActionResult("Saving", b)
                                            }
                                        },
                                        executeLoad = { saveName ->
                                            if (existingDefinitions.contains(saveName)) {
                                                val loaded = serializer.loadModule(saveName)
                                                if (loaded != null) {
                                                    setModule(loaded)
                                                    setCurrentDefinition(saveName)
                                                }
                                            }
                                        },
                                        existingDefinitions = existingDefinitions,
                                        currentDefinitionState = currentDefinitionState
                                    )
                                    is Routing.Display -> Display(
                                        module = current.module,
                                        data = data
                                    )
                                    is Routing.Builder -> Builder(
                                        module = module,
                                        backStack = backStack
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    private fun notifyActionResult(what: String, success: Boolean) {
        Toast.makeText(
            this,
            "$what ${if (success) "was successful" else "failed"}!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onBackPressed() {
        if (!backPressHandler.handle()) {
            super.onBackPressed()
        }
    }
}
