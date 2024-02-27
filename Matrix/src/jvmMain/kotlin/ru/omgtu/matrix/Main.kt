package ru.omgtu.matrix

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import ru.omgtu.matrix.di.initDi
import ru.omgtu.matrix.navigation.NavRoot
import ru.omgtu.matrix.navigation.NavView

fun main() {
    initDi()
    val navRoot = NavRoot(DefaultComponentContext(LifecycleRegistry()))
    singleWindowApplication(
        state = WindowState(),
        title = "app",
    ) {
        NavView(navRoot)
    }
}