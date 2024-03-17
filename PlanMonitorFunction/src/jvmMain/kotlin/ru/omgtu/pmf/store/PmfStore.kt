package ru.omgtu.pmf.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.omgtu.pmf.model.Parameter

interface PmfStore : Store<PmfStore.Intent, PmfStore.State, Nothing> {
    sealed interface Intent {
        data class UpdateParameter(val name: String, val value: String) : Intent
    }

    data class State(
        val documentValue: Double,
        val eliminationValue: Double,
        val parametersName: List<Parameter>
    ) {
        companion object {
            val defaultState = State(
                documentValue = Double.NaN,
                eliminationValue = Double.NaN,
                parametersName = listOf()
            )
        }
    }
}