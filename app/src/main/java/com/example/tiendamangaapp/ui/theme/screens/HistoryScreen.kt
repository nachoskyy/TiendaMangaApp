package com.example.tiendamangaapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendamangaapp.core.ServiceLocator
import com.example.tiendamangaapp.data.local.HistoryEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    var history by remember { mutableStateOf<List<HistoryEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        history = ServiceLocator.db.orderDao().getHistory()
    }

    val fmt = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de compras") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } }
            )
        }
    ) { padding ->
        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aún no tienes compras.")
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                items(history) { h ->
                    ElevatedCard {
                        Column(Modifier.padding(16.dp)) {
                            Text("Orden #${h.orderId}", style = MaterialTheme.typography.titleMedium)
                            Text("Fecha: ${fmt.format(Date(h.createdAt))}")
                            Text("Ítems: ${h.items} · Total: $${h.total}")
                        }
                    }
                }
            }
        }
    }
}
