package ru.omgtu.pmf

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.omgtu.pmf.exception.ParameterNotFoundException
import ru.omgtu.pmf.model.Parameter

class ParameterStorage {
    private val mutableParameters: MutableStateFlow<List<Parameter>> = MutableStateFlow(listOf())
    val parameters = mutableParameters.asStateFlow()
    fun getParameterValueOrNull(name: String) = mutableParameters.value.firstOrNull { it.name == name }?.value
    fun getParameterValue(name: String) = getParameterValueOrNull(name) ?: throw ParameterNotFoundException(name)

    fun updateParameter(name: String, value: Double) {
        with(mutableParameters.value) {
            val index = indexOfFirst { it.name == name }
            if (index >= 0) {
                mutableParameters.update {
                        toMutableList().apply { this[index] = this[index].copy(value = value)
                    }
                }
            } else {
                mutableParameters.update {
                    it + Parameter(name = name, value = value)
                }
            }
        }
    }

    fun addParameterToStorage(parameter: Parameter) {
        mutableParameters.update {
            it + parameter
        }
    }
}