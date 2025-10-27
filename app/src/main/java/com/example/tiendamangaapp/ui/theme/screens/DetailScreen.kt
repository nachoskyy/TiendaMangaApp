package com.example.tiendamangaapp.ui.theme.screens

import androidx.compose.foundation.layout.*
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
import com.example.tiendamangaapp.data.local.Manga
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.tiendamangaapp.data.local.CartItem
import android.content.Intent
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    mangaId: Long,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var manga by remember { mutableStateOf<Manga?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(mangaId) {
        try {
            manga = ServiceLocator.db.mangaDao().getById(mangaId)
            if (manga == null) error = "No se encontró el manga (id=$mangaId)"
        } catch (t: Throwable) {
            error = t.message ?: t::class.java.simpleName
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } }
            )
        }
    ) { padding ->
        when {
            error != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Error al cargar detalle:\n$error")
            }
            manga == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Cargando detalle…")
            }
            else -> {
                val m = manga!!
                Column(
                    Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(m.portadaUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Portada de ${m.titulo}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(m.titulo, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("${m.autor} · ${m.genero}")
                    Text("Precio: $${m.precio} · Stock: ${m.stock}")
                    Spacer(Modifier.height(12.dp))
                    Text(m.sinopsis, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(20.dp))

                    Button(onClick = {
                        scope.launch{
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            val dao = ServiceLocator.db.cartDao()
                            val exists = dao.findByManga(mangaId)
                            if (exists == null) dao.insert(CartItem(mangaId = mangaId, cantidad = 1))
                            else dao.addDelta(mangaId, 1)
                        }
                    }) { Text("Agregar al carrito") }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(onClick = {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Mira este manga")
                            putExtra(Intent.EXTRA_TEXT, "${m.titulo} – ${m.autor}")
                        }
                        context.startActivity(Intent.createChooser(send, "Compartir con…"))
                    }) { Text("Compartir") }
                }
            }
        }
    }
}
