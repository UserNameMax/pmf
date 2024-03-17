package ru.omgtu.pmf

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.omgtu.pmf.di.initDi
import ru.omgtu.pmf.store.PmfStoreFactory
import ru.omgtu.pmf.ui.PmfScreen

fun main() {
    try {
        initDi()
        val store = PmfStoreFactory(DefaultStoreFactory()).create()
        singleWindowApplication(
            state = WindowState(),
            title = "app",
        ) {
            PmfScreen(store)
        }
    } catch (e: Throwable) {
        println(e.stackTraceToString())
    }
}