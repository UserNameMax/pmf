package ru.omgtu.pmf.di

import org.koin.dsl.module
import ru.omgtu.pmf.ParameterStorage
import ru.omgtu.pmf.calculator.Calculator
import ru.omgtu.pmf.calculator.DocumentCalculator
import ru.omgtu.pmf.calculator.EliminationCalculator
import ru.omgtu.pmf.calculator.СompositionCalculator

val diModule = module {
    single<ParameterStorage> { ParameterStorage() }
    single<Calculator> {
        СompositionCalculator(
            listOf(
                DocumentCalculator(get()),
                EliminationCalculator(get())
            )
        )
    }
}