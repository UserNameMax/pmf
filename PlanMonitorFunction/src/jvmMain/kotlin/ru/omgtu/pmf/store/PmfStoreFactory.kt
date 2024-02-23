package ru.omgtu.pmf.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.omgtu.pmf.ParameterStorage
import ru.omgtu.pmf.calculator.Calculator
import ru.omgtu.pmf.model.Parameter

class PmfStoreFactory(private val storeFactory: StoreFactory) : KoinComponent {
    private val parameterStorage: ParameterStorage by inject()
    private val calculator: Calculator by inject()

    @OptIn(ExperimentalMviKotlinApi::class)
    fun create(): PmfStore =
        object : PmfStore, Store<PmfStore.Intent, PmfStore.State, Nothing> by storeFactory.create(
            name = "PmfStoreFactory",
            initialState = PmfStore.State.defaultState.copy(parametersName = calculator.params),
            bootstrapper = coroutineBootstrapper {
                launch {
                    calculator.flow.collect {
                        dispatch(Action.CollectNewParameter(it))
                    }
                }
            },
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    sealed interface Action {
        data class CollectNewParameter(val parameter: Parameter) : Action
    }

    sealed interface Message {
        data class UpdateDocumentValue(val newValue: Double) : Message
        data class UpdateEliminationValue(val newValue: Double) : Message
    }

    private inner class ExecutorImpl : CoroutineExecutor<PmfStore.Intent, Action, PmfStore.State, Message, Nothing>() {
        override fun executeIntent(intent: PmfStore.Intent, getState: () -> PmfStore.State) {
            when (intent) {
                is PmfStore.Intent.UpdateParameter -> {
                    val value = intent.value.toDoubleOrNull()
                    if (value != null) {
                        parameterStorage.updateParameter(intent.name, value)
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> PmfStore.State) {
            when (action) {
                is Action.CollectNewParameter -> {
                    parameterStorage.updateParameter(action.parameter.name, action.parameter.value)
                    when (action.parameter.name) {
                        "Индикатр состояния локальных документов" -> dispatch(
                            Message.UpdateDocumentValue(
                                action.parameter.value
                            )
                        )

                        "Индикатор устранения нарушений" -> dispatch(
                            Message.UpdateEliminationValue(
                                action.parameter.value
                            )
                        )
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<PmfStore.State, Message> {
        override fun PmfStore.State.reduce(msg: Message): PmfStore.State = when (msg) {
            is Message.UpdateDocumentValue -> copy(documentValue = msg.newValue)
            is Message.UpdateEliminationValue -> copy(eliminationValue = msg.newValue)
        }
    }
}