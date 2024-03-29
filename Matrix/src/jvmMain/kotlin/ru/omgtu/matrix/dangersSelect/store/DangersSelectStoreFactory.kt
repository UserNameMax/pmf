package ru.omgtu.matrix.dangersSelect.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.omgtu.matrix.repository.DangersNameRepository
import ru.omgtu.matrix.repository.ProfessionRepository

class DangersSelectStoreFactory(
    private val defaultStoreFactory: DefaultStoreFactory,
    private val navigateToMatrix: (selectedDangers: List<String>, String) -> Unit
) : KoinComponent {
    private val dangersNameRepository: DangersNameRepository by inject()
    private val allDangersList = dangersNameRepository.getDangers()

    private val professionRepository: ProfessionRepository by inject()
    private val professions = professionRepository.getProfessions()

    fun create(): DangersSelectStore = object : DangersSelectStore,
        Store<DangersSelectStore.Intent, DangersSelectStore.State, Nothing> by defaultStoreFactory.create(
            name = "",
            initialState = DangersSelectStore.State(
                dangers = allDangersList,
                selectedDangers = listOf(),
                professions = professions,
                selectProfession = professions.first()
            ),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    sealed interface Message {
        data class UpdateSelectDangers(val dangers: List<String>) : Message
        data class UpdateDangers(val dangers: List<String>) : Message
        data class SelectProfession(val profession: String) : Message
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<DangersSelectStore.Intent, Nothing, DangersSelectStore.State, Message, Nothing>() {
        override fun executeIntent(intent: DangersSelectStore.Intent, getState: () -> DangersSelectStore.State) {
            val state = getState()
            when (intent) {
                is DangersSelectStore.Intent.OnDeleteDanger -> dispatch(Message.UpdateSelectDangers((state.selectedDangers - intent.danger).sorted()))
                is DangersSelectStore.Intent.OnOpenMatrix -> navigateToMatrix(state.selectedDangers, state.selectProfession)
                is DangersSelectStore.Intent.OnSelectDanger -> dispatch(Message.UpdateSelectDangers((state.selectedDangers + intent.danger).sorted()))
                is DangersSelectStore.Intent.OnSearch -> dispatch(Message.UpdateDangers(allDangersList.filter {
                    it.contains(
                        intent.searchString
                    )
                }))

                is DangersSelectStore.Intent.OnSelectProfession -> dispatch(Message.SelectProfession(intent.profession))
            }
        }
    }

    private object ReducerImpl : Reducer<DangersSelectStore.State, Message> {
        override fun DangersSelectStore.State.reduce(msg: Message): DangersSelectStore.State = when (msg) {
            is Message.UpdateDangers -> copy(dangers = msg.dangers)
            is Message.UpdateSelectDangers -> copy(selectedDangers = msg.dangers)
            is Message.SelectProfession -> copy(selectProfession = msg.profession)
        }
    }
}