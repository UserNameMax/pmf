package ru.omgtu.matrix.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.serialization.Serializable
import ru.omgtu.matrix.dangersSelect.store.DangersSelectStore
import ru.omgtu.matrix.dangersSelect.store.DangersSelectStoreFactory
import ru.omgtu.matrix.matrix.store.DangerStore
import ru.omgtu.matrix.matrix.store.DangerStoreFactory

class NavRoot(componentContext: ComponentContext) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Destination>()
    val childStack =
        childStack(
            source = navigation,
            serializer = Destination.serializer(),
            initialConfiguration = Destination.SelectDangers,
            childFactory = ::childFactory
        )

    fun childFactory(destination: Destination, componentContext: ComponentContext) = when (destination) {
        is Destination.Matrix -> Child.Matrix(
            store = DangerStoreFactory(
                factory = DefaultStoreFactory(),
                dangers = destination.dangers
            ).create()
        )

        is Destination.SelectDangers -> Child.SelectDangers(
            store = DangersSelectStoreFactory(
                defaultStoreFactory = DefaultStoreFactory(),
                navigateToMatrix = { dangers -> navigation.push(Destination.Matrix(dangers)) }).create()
        )
    }

    @Serializable
    sealed interface Destination {
        @Serializable
        object SelectDangers : Destination

        @Serializable
        data class Matrix(val dangers: List<String>) : Destination
    }

    sealed interface Child {
        data class SelectDangers(val store: DangersSelectStore) : Child
        data class Matrix(val store: DangerStore) : Child
    }
}