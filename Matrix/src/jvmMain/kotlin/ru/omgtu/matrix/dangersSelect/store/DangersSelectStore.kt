package ru.omgtu.matrix.dangersSelect.store

import com.arkivanov.mvikotlin.core.store.Store

interface DangersSelectStore : Store<DangersSelectStore.Intent, DangersSelectStore.State, Nothing> {
    sealed interface Intent {
        data class OnSelectDanger(val danger: String) : Intent
        data class OnDeleteDanger(val danger: String) : Intent
        object OnOpenMatrix : Intent
        data class OnSearch(val searchString: String) : Intent
        data class OnSelectProfession(val profession: String) : Intent
    }

    data class State(
        val dangers: List<String>,
        val selectedDangers: List<String>,
        val professions: List<String>,
        val selectProfession: String
    )
}