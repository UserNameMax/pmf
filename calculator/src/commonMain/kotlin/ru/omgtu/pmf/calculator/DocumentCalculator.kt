package ru.omgtu.pmf.calculator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.omgtu.pmf.ParameterStorage
import ru.omgtu.pmf.exception.ParameterNotFoundException
import ru.omgtu.pmf.model.Parameter

class DocumentCalculator(private val parameterStorage: ParameterStorage) : Calculator {
    private val mutableFlow = MutableSharedFlow<Parameter>()
    override val flow: SharedFlow<Parameter> = mutableFlow.asSharedFlow()
    override val params: List<String> =
        listOf(
            "общее количество необходимых локальных документов",
            "количество разработанных СОТ локальных документов",
            "количество актуальных локальных документов",
            "оценка важности актуализации документов"
        )

    init {
        startCheckStorage()
    }

    override fun calc(): Parameter {
        val ND = parameterStorage.getParameterValue(params[0])
        val nD = parameterStorage.getParameterValue(params[1])
        val nAD = parameterStorage.getParameterValue(params[2])
        val k = parameterStorage.getParameterValue(params[3])
        val value = (nAD + (1 - k) * (nD - nAD)) / ND
        return Parameter("Индикатр состояния локальных документов", value)
    }

    private fun startCheckStorage() {
        CoroutineScope(Dispatchers.IO).launch {
            parameterStorage.parameters.collect {
                try {
                    mutableFlow.emit(calc())
                } catch (e: ParameterNotFoundException) {
                    println(e.message ?: "ParameterNotFoundException")
                }
            }
        }
    }

}