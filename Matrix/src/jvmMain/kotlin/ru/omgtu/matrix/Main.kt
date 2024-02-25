package ru.omgtu.matrix

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.omgtu.matrix.di.initDi
import ru.omgtu.matrix.store.DangerStoreFactory
import ru.omgtu.matrix.ui.DangersScreen

fun main() {
    initDi()
    val store = DangerStoreFactory(DefaultStoreFactory(), listOf("падение с высоты", "шум")).create()
    singleWindowApplication(
        state = WindowState(),
        title = "app",
    ) {
        DangersScreen(store)
    }
    println("hello world")
}