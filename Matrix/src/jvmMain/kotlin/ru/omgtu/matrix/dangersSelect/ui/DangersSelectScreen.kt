package ru.omgtu.matrix.dangersSelect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import io.kanro.compose.jetbrains.expui.control.OutlineButton
import io.kanro.compose.jetbrains.expui.control.TextField
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.omgtu.matrix.dangersSelect.store.DangersSelectStore
import ru.omgtu.matrix.matrix.ui.DropdownSelect

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun DangersSelectScreen(store: DangersSelectStore) {
    val state by store.stateFlow.collectAsState()
    DangersSelectView(
        dangers = state.dangers,
        selectedDangers = state.selectedDangers,
        onInputSearchText = { store.accept(DangersSelectStore.Intent.OnSearch(it)) },
        onSelectDanger = {
            val intent =
                if (state.selectedDangers.contains(it)) {
                    DangersSelectStore.Intent.OnDeleteDanger(it)
                } else {
                    DangersSelectStore.Intent.OnSelectDanger(it)
                }
            store.accept(intent)
        },
        onDone = { store.accept(DangersSelectStore.Intent.OnOpenMatrix) },
        selectedProfession = state.selectProfession,
        professions = state.professions,
        onProfessionSelect = { store.accept(DangersSelectStore.Intent.OnSelectProfession(it)) }
    )
}

@Composable
fun DangersSelectView(
    dangers: List<String>,
    selectedDangers: List<String>,
    onInputSearchText: (String) -> Unit,
    onSelectDanger: (String) -> Unit,
    onDone: () -> Unit,
    selectedProfession: String,
    professions: List<String>,
    onProfessionSelect: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    Column(Modifier.padding(5.dp)) {
        DropdownSelect(
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            selectedValue = selectedProfession,
            namedValues = professions.map { Pair(it, it) },
            onSelect = onProfessionSelect
        )
        Row(Modifier.fillMaxWidth().padding(5.dp)) {
            TextField(modifier = Modifier.weight(1f), value = searchText, onValueChange = {
                searchText = it
                onInputSearchText(it)
            })
            Spacer(Modifier.width(10.dp))
            OutlineButton(modifier = Modifier, onClick = onDone) { Text("Done") }
        }

        LazyColumn {
            items(dangers) { danger ->
                DangerItemView(
                    modifier = Modifier.fillMaxWidth(),
                    danger = danger,
                    isSelected = selectedDangers.contains(danger),
                    onClick = { onSelectDanger(danger) }
                )
            }
        }
    }
}

@Composable
fun DangerItemView(modifier: Modifier = Modifier, danger: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.Green.copy(alpha = 0.5f) else Color.White
    Row(modifier = modifier.background(backgroundColor).clickable { onClick() }) {
        Text(danger)
    }
}