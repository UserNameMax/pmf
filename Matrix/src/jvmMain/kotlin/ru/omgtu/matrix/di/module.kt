package ru.omgtu.matrix.di

import org.koin.dsl.module
import ru.omgtu.pmf.ParameterStorage
import ru.omgtu.pmf.calculator.Calculator
import ru.omgtu.pmf.calculator.DangerCalculator
import ru.omgtu.pmf.calculator.СompositionCalculator

val diModule = module {
    single<ParameterStorage> { ParameterStorage() }
    factory<Calculator> { params ->
        val calculatedParameters = params.get<List<String>>()
        val calculators =
            calculatedParameters.map {
                listOf(
                    DangerCalculator(
                        parameterStorage = get(),
                        dangerName = it,
                        calcHumanFactor = false
                    ), DangerCalculator(
                        parameterStorage = get(),
                        dangerName = it,
                        calcHumanFactor = true
                    )
                )
            }.flatten()
        СompositionCalculator(calculators)
    }
}