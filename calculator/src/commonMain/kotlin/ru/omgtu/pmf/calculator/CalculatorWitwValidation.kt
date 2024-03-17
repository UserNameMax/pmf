package ru.omgtu.pmf.calculator

import ru.omgtu.pmf.model.Parameter

interface CalculatorWithValidation: Calculator {
    val paramsTypedList: List<Parameter>
}