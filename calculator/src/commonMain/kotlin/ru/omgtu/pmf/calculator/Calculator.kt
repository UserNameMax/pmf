package ru.omgtu.pmf.calculator

import kotlinx.coroutines.flow.SharedFlow
import ru.omgtu.pmf.model.Parameter

interface Calculator {
    val flow: SharedFlow<Parameter>
    val params: List<String>
    fun calc(): Parameter
}