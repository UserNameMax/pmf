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

class DangerCalculator(
    private val parameterStorage: ParameterStorage,
    private val dangerName: String,
    private val calcHumanFactor: Boolean,
) : CalculatorWithValidation {
    private val mutableFlow = MutableSharedFlow<Parameter>()
    override val flow: SharedFlow<Parameter> = mutableFlow.asSharedFlow()
    override val params: List<String> = listOf(
        "Значение вероятности возникновения опасности",
        "Значение времени попадания в зону опасности",
        "Значение человеческого фактора",
        "Тяжесть возникновения опасности"
    )

    override fun calc(): Parameter {
        val sf = parameterStorage.getParameterValue("$dangerName/${params[0]}")
        val sw = parameterStorage.getParameterValue("$dangerName/${params[1]}")
        val shf = if (calcHumanFactor) parameterStorage.getParameterValue("$dangerName/${params[2]}") else 1.0
        val t = parameterStorage.getParameterValue("$dangerName/${params[3]}")
        val value = (1 - (sf * sw * shf)) * t
        return Parameter("$dangerName ${if (calcHumanFactor) "с ЧФ" else ""}", value)
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

    override val paramsTypedList: List<Parameter> = listOf()

    init {
        startCheckStorage()
    }
}