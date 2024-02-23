package ru.omgtu.pmf.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.omgtu.pmf.store.PmfStore

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PmfScreen(store: PmfStore) {
    val state by store.stateFlow.collectAsState()
    PmfScreen(
        parameters = state.parametersName,
        document = state.documentValue,
        elimination = state.eliminationValue,
        onInputParameter = { name, value -> store.accept(PmfStore.Intent.UpdateParameter(name, value)) })
}

@Composable
fun PmfScreen(
    parameters: List<String>,
    document: Double,
    elimination: Double,
    onInputParameter: (name: String, value: String) -> Unit
) {
    Column {
        ParamView("document", document.toString())
        ParamView("elimination", elimination.toString())
        LazyColumn {
            items(parameters) {
                ParamView(it, "", onInputParameter)
            }
        }
    }
}

@Composable
fun ParamView(name: String, value: String, onInputParameter: (name: String, value: String) -> Unit) {
    var inputValue by remember { mutableStateOf(value) }
    Row {
        Text(name)
        TextField(inputValue, { newValue ->
            inputValue = newValue
            onInputParameter(name, newValue)
        })
    }
}

@Composable
fun ParamView(name: String, value: String) {
    Row {
        Text(name)
        Text(value)
    }
}