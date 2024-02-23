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

class EliminationCalculator(private val parameterStorage: ParameterStorage) : Calculator {
    private val mutableFlow = MutableSharedFlow<Parameter>()
    override val flow: SharedFlow<Parameter> = mutableFlow.asSharedFlow()
    override val params: List<String> =
        listOf(
            "общее количество работников в организации",
            "количество работающих с нарушениями",
            "комплект (перечень) нормативно-правовых актов содержащих требования",
            "число нарушенных нормативно-правовых актов"
        )

    init {
        startCheckStorage()
    }

    override fun calc(): Parameter {
        val po = parameterStorage.getParameterValue(params[0])
        val pn = parameterStorage.getParameterValue(params[1])
        val no = parameterStorage.getParameterValue(params[2])
        val nn = parameterStorage.getParameterValue(params[3])
        val value = 0.5 * ((po - pn) / po + nn / no)
        return Parameter(name = "Индикатор устранения нарушений", value = value)
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