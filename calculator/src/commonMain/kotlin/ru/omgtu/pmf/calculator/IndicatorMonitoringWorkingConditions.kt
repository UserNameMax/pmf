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

class IndicatorMonitoringWorkingConditions(
    private val parameterStorage: ParameterStorage
) : CalculatorWithValidation {
    private val mutableFlow = MutableSharedFlow<Parameter>()
    override val flow: SharedFlow<Parameter> = mutableFlow.asSharedFlow()
    override val params: List<String> =
        listOf(
            "количество запланированных контрольных мероприятий за условиями и охраной труда, а также профессиональными рисками на рабочих местах",
            "количество реализованных контрольных мероприятий",
            "количество устраненных нарушений (nН) из числа NН",
            "общее число нарушений",
            "оценка регулярности проверок",
        )

    init {
        startCheckStorage()
    }

    override val paramsTypedList: List<Parameter> = params.map {
        Parameter(name = it, value = 0.0, validation = {} )
    }

    override fun calc(): Parameter {
        val MP = parameterStorage.getParameterValue(params[0])
        val mP = parameterStorage.getParameterValue(params[1])
        val nH = parameterStorage.getParameterValue(params[2])
        val NH = parameterStorage.getParameterValue(params[3])
        val kP = parameterStorage.getParameterValue(params[4])

        val value = 0.5*(mP/MP + nH/NH)*kP
        return Parameter(" Индикатор контроля за условиями и охраной труда", value)
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