package ru.omgtu.pmf.exception

class ParameterNotFoundException(parameterName: String) : Exception("Parameter $parameterName not found") {
}