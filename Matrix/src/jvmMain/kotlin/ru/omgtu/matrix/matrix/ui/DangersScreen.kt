package ru.omgtu.matrix.matrix.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.omgtu.matrix.matrix.store.DangerStore

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun DangersScreen(store: DangerStore) {
    val state by store.stateFlow.collectAsState()
    DangersView(
        dangers = state.parameters,
        onSelectDangerProbability = { dangerProbability, p ->
            store.accept(
                DangerStore.Intent.OnInputDangerProbability(
                    dangerProbability,
                    p
                )
            )
        },
        onSelectTime = { time, name -> store.accept(DangerStore.Intent.OnInputTime(time, name)) },
        onSelectHumanProbability = { humanProbability, name ->
            store.accept(
                DangerStore.Intent.OnInputHumanProbability(
                    humanProbability,
                    name
                )
            )
        },
        onSelectConsequenceProbability = { consequenceProbability, name ->
            store.accept(
                DangerStore.Intent.OnInputConsequenceProbability(
                    consequenceProbability,
                    name
                )
            )
        }
    )
}

@Composable
fun DangersView(
    modifier: Modifier = Modifier,
    dangers: List<Pair<String, DangerStore.DangerParameters>>,
    onSelectDangerProbability: (DangerStore.DangerProbability, String) -> Unit,
    onSelectTime: (Int, String) -> Unit,
    onSelectHumanProbability: (DangerStore.HumanProbability, String) -> Unit,
    onSelectConsequenceProbability: (DangerStore.ConsequenceProbability, String) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(dangers) {
            val (dangerName, dangerParams) = it
            DangerView(
                name = dangerName,
                params = dangerParams,
                onSelectDangerProbability = { onSelectDangerProbability(it, dangerName) },
                onSelectTime = { onSelectTime(it, dangerName) },
                onSelectHumanProbability = { onSelectHumanProbability(it, dangerName) },
                onSelectConsequenceProbability = { onSelectConsequenceProbability(it, dangerName) }
            )
        }
    }
}

@Composable
fun DangerView(
    modifier: Modifier = Modifier,
    name: String,
    params: DangerStore.DangerParameters,
    onSelectDangerProbability: (DangerStore.DangerProbability) -> Unit,
    onSelectTime: (Int) -> Unit,
    onSelectHumanProbability: (DangerStore.HumanProbability) -> Unit,
    onSelectConsequenceProbability: (DangerStore.ConsequenceProbability) -> Unit,
) {
    Row {
        Text(name)
        DropdownSelect(
            namedValues = DangerStore.DangerProbability.entries.map { Pair(it, it.str) },
            onSelect = onSelectDangerProbability,
            selectedValue = params.dangerProbability.str
        )
        DropdownSelect(
            namedValues = List(24) { Pair(it + 1, (it + 1).toString()) },
            onSelect = onSelectTime,
            selectedValue = params.time.toString()
        )
        DropdownSelect(
            namedValues = DangerStore.HumanProbability.entries.map { Pair(it, it.str) },
            onSelect = onSelectHumanProbability,
            selectedValue = params.humanProbability.str
        )
        DropdownSelect(
            namedValues = DangerStore.ConsequenceProbability.entries.map { Pair(it, it.str) },
            onSelect = onSelectConsequenceProbability,
            selectedValue = params.consequenceProbability.str
        )
        Text(params.dangerValueWithHuman.str)
        Text(params.dangerValue.str)
    }
}

@Composable
fun <T> DropdownSelect(
    modifier: Modifier = Modifier,
    selectedValue: String,
    namedValues: List<Pair<T, String>>,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        TextField(
            value = selectedValue,
            onValueChange = {},
            modifier = Modifier.clickable { expanded = !expanded },
            readOnly = true,
            enabled = false
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for ((value, name) in namedValues) {
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = { onSelect(value) })
            }
        }
    }
}