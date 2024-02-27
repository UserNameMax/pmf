package ru.omgtu.matrix

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.omgtu.matrix.dangersSelect.store.DangersSelectStoreFactory
import ru.omgtu.matrix.dangersSelect.ui.DangersSelectScreen
import ru.omgtu.matrix.di.initDi

fun main() {
    initDi()
    //val store = DangerStoreFactory(DefaultStoreFactory(), ResourceDangersNameRepository().getDangers()).create()
    val store = DangersSelectStoreFactory(DefaultStoreFactory(), {}).create()
    singleWindowApplication(
        state = WindowState(),
        title = "app",
    ) {
        //DangersScreen(store)
        DangersSelectScreen(store)
    }
}