package ru.omgtu.matrix.matrix.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import io.kanro.compose.jetbrains.expui.control.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.omgtu.matrix.matrix.store.DangerStore
import kotlin.math.roundToInt

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
        },
        onBackPress = { store.accept(DangerStore.Intent.OnBackButtonPress) },
        profession = state.profession
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
    onBackPress: () -> Unit,
    profession: String
) {
    Column(modifier.padding(10.dp)) {
        Label(profession)
        Spacer(Modifier.height(5.dp))
        LazyColumn(Modifier.weight(1f)) {
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
                Spacer(Modifier.height(20.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlineButton(onBackPress) { Label("Назад") }
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
    Column(modifier = modifier) {
        Label(name)
        Spacer(Modifier.height(10.dp))
        Column {
            Row {
                DropdownSelect(
                    modifier = Modifier.weight(1f).padding(horizontal = 5.dp),
                    selectedValue = params.dangerProbability?.let { "${it.str} (${it.value * 5})" } ?: "",
                    namedValues = DangerStore.DangerProbability.entries.map { Pair(it, "${it.str} (${it.value * 5})") },
                    onSelect = onSelectDangerProbability,
                    title = "Значение вероятности возникновения опасности"
                )
                Spacer(Modifier.width(10.dp))
                DropdownSelect(
                    modifier = Modifier.weight(1f).padding(horizontal = 5.dp),
                    selectedValue = params.time?.toString() ?: "",
                    namedValues = List(24) { Pair(it + 1, (it + 1).toString()) },
                    onSelect = onSelectTime,
                    title = "Значение времени попадания в зону опасности"
                )
                Spacer(Modifier.width(10.dp))
                DropdownSelect(
                    modifier = Modifier.weight(1f).padding(horizontal = 5.dp),
                    selectedValue = params.humanProbability?.let { "${it.str} (${it.value * 5})" } ?: "",
                    namedValues = DangerStore.HumanProbability.entries.map { Pair(it, "${it.str} (${it.value * 5})") },
                    onSelect = onSelectHumanProbability,
                    title = "Значение человеческого фактора"
                )
                Spacer(Modifier.width(10.dp))
                DropdownSelect(
                    modifier = Modifier.weight(1f).padding(horizontal = 5.dp),
                    selectedValue = params.consequenceProbability?.let { "${it.str} (${it.value.roundToInt() * 5})" }
                        ?: "",
                    namedValues = DangerStore.ConsequenceProbability.entries.map {
                        Pair(
                            it,
                            "${it.str} (${it.value * 5})"
                        )
                    },
                    onSelect = onSelectConsequenceProbability,
                    title = "Тяжесть возникновения опасности"
                )
            }
            Spacer(Modifier.height(10.dp))

            Label(params.dangerValueWithHuman?.run { "ПР с ЧФ: ${String.format("%.2f", value)} ($str)" } ?: "ПР с ЧФ:")
            Spacer(Modifier.height(5.dp))
            Label(params.dangerValue?.run { "ПР: ${String.format("%.2f", value)} ($str)" } ?: "ПР:")
        }

    }
}

@Composable
fun <T> DropdownSelect(
    modifier: Modifier = Modifier,
    selectedValue: String,
    namedValues: List<Pair<T, String>>,
    onSelect: (T) -> Unit,
    title: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.width(IntrinsicSize.Min)) {
        if (title != null) {
            Label(title)
        }
        TextField(
            value = selectedValue,
            onValueChange = {},
            modifier = Modifier.clickable { expanded = !expanded }.fillMaxWidth(),
            readOnly = true,
            enabled = false,
            colors = LocalTextFieldColors.current.run { copy(disabledAreaColors = normalAreaColors) }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for ((value, name) in namedValues) {
                DropdownMenuItem(onClick = {
                    onSelect(value)
                    expanded = false
                }) {
                    Label(name)
                }
            }
        }
    }
}