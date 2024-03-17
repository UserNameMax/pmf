package ru.omgtu.pmf.calculator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.omgtu.pmf.ParameterStorage
import ru.omgtu.pmf.exception.ParameterNotFoundException
import ru.omgtu.pmf.model.Parameter

class IndicatorLevelCompetence(private val parameterStorage: ParameterStorage) : CalculatorWithValidation {
    private val mutableFlow = MutableSharedFlow<Parameter>()
    override val flow: SharedFlow<Parameter> = mutableFlow.asSharedFlow()
    override val params: List<String> =
        listOf(
            "оценка функциональной возможности реализации профессиональных компетенций (или «переработки») СОТ",
            ""
        )

    init {
        startCheckStorage()
    }

    override fun calc(): Parameter {
        val KD = parameterStorage.getParameterValue("Индикатр состояния локальных документов")
        val KH = parameterStorage.getParameterValue("Индикатор устранения нарушений")
        val KK = parameterStorage.getParameterValue("Индикатор контроля за условиями и охраной труда")
        val KM = parameterStorage.getParameterValue("Индикатор планирования и реализации мероприятий")
        val kP = parameterStorage.getParameterValue(params[0])
        val value = (KD + KH + KK + KK)*kP*0.25f
        return Parameter("Индикатр состояния локальных документов", value) // TODO
    }

    override val paramsTypedList: List<Parameter> = params.map {
        Parameter(name = it, value = 0.0, validation = {} )
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