package ru.omgtu.matrix.dangersSelect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.omgtu.matrix.dangersSelect.store.DangersSelectStore

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
        onDone = { store.accept(DangersSelectStore.Intent.OnOpenMatrix) })
}

@Composable
fun DangersSelectView(
    dangers: List<String>,
    selectedDangers: List<String>,
    onInputSearchText: (String) -> Unit,
    onSelectDanger: (String) -> Unit,
    onDone: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    Column {
        Row {
            TextField(value = searchText, onValueChange = {
                searchText = it
                onInputSearchText(it)
            })
            Button(onDone) { Text("Done") }
        }

        LazyColumn {
            items(dangers) { danger ->
                DangerItemView(
                    modifier = Modifier,
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