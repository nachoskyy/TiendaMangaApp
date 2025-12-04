package com.example.tiendamangaapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import com.example.tiendamangaapp.core.ServiceLocator
import com.example.tiendamangaapp.data.local.CartItem
import com.example.tiendamangaapp.data.local.Manga
import com.example.tiendamangaapp.data.remote.MangaApi
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onLogout: () -> Unit,
    onViewDetail: (Long) -> Unit,
    onOpenCart: () -> Unit
) {
    val mangaDao = ServiceLocator.db.mangaDao()
    val cartDao = ServiceLocator.db.cartDao()

    var mangas by remember { mutableStateOf<List<Manga>>(emptyList()) }
    var cartCount by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var apiMessage by remember { mutableStateOf<String?>(null) }

    val haptics = LocalHapticFeedback.current
    val animatedCount by animateIntAsState(cartCount, label = "cartCount")


    LaunchedEffect(Unit) {
        try {
            mangas = mangaDao.getAll()
            cartCount = cartDao.getAll().sumOf { it.cantidad }
            val apiResult = MangaApi.service.getMangas()
            val firstTitle = apiResult.data.firstOrNull()?.title ?: "Sin Resultados"
            apiMessage = "Ejemplo API: primer manga desde Jikan -> $firstTitle"
            println(">>> API DEBUG: $apiResult")
        } catch (t: Throwable) {
            error = t.message ?: t::class.java.simpleName
            apiMessage = "Error al llamar API: ${t.message}"

            println(">>> API ERROR: ${t.message}")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Catálogo de Mangas") },
                actions = {
                    TextButton(onClick = onOpenCart) { Text("Carrito ($animatedCount)") }
                    TextButton(onClick = onLogout)   { Text("Salir") }
                }
            )
        }
    ) { padding ->
        when {
            error != null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Error al cargar catálogo:\n$error")
            }

            mangas.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando catálogo…")
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {

                    apiMessage?.let { msg ->
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(mangas) { m ->
                            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                                ElevatedCard(
                                    onClick = { onViewDetail(m.id) }
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(m.portadaUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Portada de ${m.titulo}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        Text(m.titulo, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            "${m.autor} · ${m.genero}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            "Precio: $${m.precio} · Stock: ${m.stock}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Row {
                                            OutlinedButton(onClick = { onViewDetail(m.id) }) {
                                                Text("Ver detalle")
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Button(onClick = {
                                                scope.launch {
                                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    val exists = cartDao.findByManga(m.id)
                                                    if (exists == null) cartDao.insert(
                                                        CartItem(
                                                            mangaId = m.id,
                                                            cantidad = 1
                                                        )
                                                    )
                                                    else cartDao.addDelta(m.id, 1)
                                                    cartCount =
                                                        cartDao.getAll().sumOf { it.cantidad }
                                                }
                                            }) { Text("Agregar") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}