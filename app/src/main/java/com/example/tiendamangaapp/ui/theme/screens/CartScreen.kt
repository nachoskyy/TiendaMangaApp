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
import com.example.tiendamangaapp.data.local.CartLine
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.animation.AnimatedContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBack: () -> Unit,
    onOpenHistory: () -> Unit
) {
    var lines by remember { mutableStateOf<List<CartLine>>(emptyList()) }
    var total by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val dao = ServiceLocator.db.cartDao()
    val snackbarHostState = remember { SnackbarHostState() }
    val orderDao = ServiceLocator.db.orderDao()

    suspend fun queryAndSet() {
        val l = dao.getCartLines()
        lines = l
        total = l.sumOf { it.precio * it.cantidad }
    }

    LaunchedEffect(Unit) { queryAndSet() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tu carrito") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } },
                actions = { TextButton(onClick = onOpenHistory) { Text("Historial de Compras") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomAppBar {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total: $$total", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {
                            scope.launch {
                                if (lines.isNotEmpty()) {
                                    val id = orderDao.placeOrderFromCart(
                                        ServiceLocator.db.cartDao(),
                                        ServiceLocator.db.mangaDao()
                                    )
                                    if (id > 0) {
                                        queryAndSet()
                                        snackbarHostState.showSnackbar("Tu compra se ha realizado correctamente (#$id)")
                                    }
                                }
                            }
                    }, enabled = lines.isNotEmpty()) { Text("Finalizar compra") }
                }
            }
        }
    ) { padding ->
        if (lines.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Tu carrito está vacío") }
        } else {
            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                items(lines) { line ->
                    ElevatedCard {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(line.titulo, style = MaterialTheme.typography.titleMedium)
                                Text("Precio: $${line.precio}  ·  Cantidad: ")
                                AnimatedContent(targetState = line.cantidad, label = "qty") { q -> Text("$q")}
                                Text("Subtotal: $${line.precio * line.cantidad}")
                            }

                            val up: () -> Unit = {
                                scope.launch {
                                    dao.addDelta(line.mangaId, 1)
                                    queryAndSet()
                                }
                            }
                            val down: () -> Unit = {
                                scope.launch {
                                    if (line.cantidad > 1) {
                                        dao.addDelta(line.mangaId, -1)
                                    } else {
                                        dao.deleteByManga(line.mangaId)
                                    }
                                    queryAndSet()
                                }
                            }

                            OutlinedButton(onClick = down) { Text("-") }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = up) { Text("+") }
                        }
                    }
                }
            }
        }
    }
}
