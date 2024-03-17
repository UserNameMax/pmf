package ru.omgtu.pmf.model

data class Parameter(
    val name: String,
    val value: Double,
    val validation: ((Double) -> Any)? = null // Boolean or String
)
