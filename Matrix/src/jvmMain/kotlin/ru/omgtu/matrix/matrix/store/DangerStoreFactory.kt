package ru.omgtu.matrix.matrix.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import ru.omgtu.pmf.ParameterStorage
import ru.omgtu.pmf.calculator.Calculator
import ru.omgtu.pmf.model.Parameter

class DangerStoreFactory(private val factory: StoreFactory, private val dangers: List<String>) : KoinComponent {
    private val storage: ParameterStorage by inject()
    private val calculator: Calculator by inject { parametersOf(dangers) }

    @OptIn(ExperimentalMviKotlinApi::class)
    fun create(): DangerStore =
        object : DangerStore, Store<DangerStore.Intent, DangerStore.State, Nothing> by factory.create(
            name = "DangerStore",
            initialState = DangerStore.State(parameters = dangers.map { Pair(it, DangerStore.DangerParameters()) }),
            bootstrapper = coroutineBootstrapper {
                launch {
                    calculator.flow.collect { dispatch(Action.CollectNewParameter(it)) }
                }
            },
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    sealed interface Action {
        data class CollectNewParameter(val parameter: Parameter) : Action
    }

    sealed interface Message {
        data class UpdateParamsList(val params: List<Pair<String, DangerStore.DangerParameters>>) : Message
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<DangerStore.Intent, Action, DangerStore.State, Message, Nothing>() {
        override fun executeAction(action: Action, getState: () -> DangerStore.State) {
            val params = getState().parameters.toMutableList()
            when (action) {
                is Action.CollectNewParameter -> {
                    val dangerName = action.parameter.name
                    val index = params.indexOfFirst { it.first == dangerName.replace(" с ЧФ", "").trim() }
                    when {
                        dangerName.contains("с ЧФ") -> params[index] =
                            params[index].copy(
                                second = params[index].second.copy(
                                    dangerValueWithHuman = DangerStore.DangerValue.create(
                                        action.parameter.value
                                    )
                                )
                            )

                        !dangerName.contains("с ЧФ") -> params[index] =
                            params[index].copy(
                                second = params[index].second.copy(
                                    dangerValue = DangerStore.DangerValue.create(
                                        action.parameter.value
                                    )
                                )
                            )
                    }
                    dispatch(Message.UpdateParamsList(params))
                }
            }
        }

        override fun executeIntent(intent: DangerStore.Intent, getState: () -> DangerStore.State) {
            val params = getState().parameters.toMutableList()
            when (intent) {
                is DangerStore.Intent.OnInputConsequenceProbability -> {
                    val index = params.indexOfFirst { it.first == intent.dangerName }
                    params[index] =
                        params[index].copy(second = params[index].second.copy(consequenceProbability = intent.consequenceProbability))
                    storage.updateParameter(
                        name = "${intent.dangerName}/${calculator.params[3]}",
                        value = intent.consequenceProbability.value
                    )
                    dispatch(Message.UpdateParamsList(params))
                }

                is DangerStore.Intent.OnInputDangerProbability -> {
                    val index = params.indexOfFirst { it.first == intent.dangerName }
                    params[index] =
                        params[index].copy(second = params[index].second.copy(dangerProbability = intent.dangerProbability))
                    storage.updateParameter(
                        name = "${intent.dangerName}/${calculator.params[0]}",
                        value = intent.dangerProbability.value
                    )
                    dispatch(Message.UpdateParamsList(params))
                }

                is DangerStore.Intent.OnInputHumanProbability -> {
                    val index = params.indexOfFirst { it.first == intent.dangerName }
                    params[index] =
                        params[index].copy(second = params[index].second.copy(humanProbability = intent.humanProbability))
                    storage.updateParameter(
                        name = "${intent.dangerName}/${calculator.params[2]}",
                        value = intent.humanProbability.value
                    )
                    dispatch(Message.UpdateParamsList(params))
                }

                is DangerStore.Intent.OnInputTime -> {
                    val index = params.indexOfFirst { it.first == intent.dangerName }
                    params[index] =
                        params[index].copy(second = params[index].second.copy(time = intent.time))
                    storage.updateParameter(
                        name = "${intent.dangerName}/${calculator.params[1]}",
                        value = intent.time.toDouble() / 24
                    )
                    dispatch(Message.UpdateParamsList(params))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<DangerStore.State, Message> {
        override fun DangerStore.State.reduce(msg: Message): DangerStore.State = when (msg) {
            is Message.UpdateParamsList -> copy(parameters = msg.params)
        }
    }
}