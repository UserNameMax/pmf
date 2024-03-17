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

class PlanningAndImplementation(private val parameterStorage: ParameterStorage) : CalculatorWithValidation {
    private val mutableFlow = MutableSharedFlow<Parameter>()
    override val flow: SharedFlow<Parameter> = mutableFlow.asSharedFlow()
    override val params: List<String> =
        listOf(
            "количество запланированных к проведению мероприятий на основе предписаний",
            "количество соответственно реализованных мероприятий на основе предписаний",
            "количество запланированных к проведению мероприятий в результате происшествий",
            "количество соответственно реализованных мероприятий в результате происшествий",
            "количество запланированных к проведению мероприятий в результате мониторинга СУОТ",
            "количество соответственно реализованных мероприятий в результате мониторинга СУОТ",
            "оценка устранения устных замечаний превентивного характера, сделанных СОТ вне планов"
        )

    init {
        startCheckStorage()
    }

    override fun calc(): Parameter {
        val MH = parameterStorage.getParameterValue(params[0])
        val mH = parameterStorage.getParameterValue(params[1])
        val MVP = parameterStorage.getParameterValue(params[2])
        val mVP = parameterStorage.getParameterValue(params[3])
        val MM = parameterStorage.getParameterValue(params[4])
        val mM = parameterStorage.getParameterValue(params[5])
        val kPR = parameterStorage.getParameterValue(params[6])

        val value = 0.25 * (mH / MH + mVP / MVP + mM / MM + kPR)
        return Parameter("Индикатор планирования и реализации мероприятий", value)
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