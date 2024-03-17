package ru.omgtu.pmf.calculator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.omgtu.pmf.model.Parameter

class СompositionCalculator(private val calculators: List<CalculatorWithValidation>) : CalculatorWithValidation {
    override val flow: SharedFlow<Parameter> =
        calculators.map { it.flow }.fold(emptyFlow<Parameter>()) { acc, sharedFlow -> merge(acc, sharedFlow) }.shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily
        )
    override val params: List<String> = calculators.map { it.params }.flatten()

    override val paramsTypedList: List<Parameter>
        get() = calculators.map { it.paramsTypedList }.flatten()

    override fun calc(): Parameter {
        return calculators.first().calc()
    }
}