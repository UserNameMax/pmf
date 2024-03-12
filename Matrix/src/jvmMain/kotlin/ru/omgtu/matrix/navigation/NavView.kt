package ru.omgtu.matrix.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import ru.omgtu.matrix.dangersSelect.ui.DangersSelectScreen
import ru.omgtu.matrix.matrix.ui.DangersScreen

@Composable
fun NavView(navRoot: NavRoot) {
    Children(navRoot.childStack){
        val child = it.instance
        when(child){
            is NavRoot.Child.Matrix -> DangersScreen(child.store)
            is NavRoot.Child.SelectDangers -> DangersSelectScreen(child.store)
        }
    }
}