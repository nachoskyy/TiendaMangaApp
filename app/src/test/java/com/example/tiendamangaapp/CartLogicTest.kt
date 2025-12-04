package com.example.tiendamangaapp

import com.example.tiendamangaapp.core.CartLogic
import com.example.tiendamangaapp.data.local.CartItem
import com.example.tiendamangaapp.data.local.Manga
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test


class CartLogicTest {

    private fun sampleMangas() = listOf(
        Manga(id = 1, titulo = "One piece 1", autor = "Oda", genero = "Shonen", precio = 10000, stock = 5, sinopsis = "", portadaUrl = ""),
        Manga(id = 2, titulo = "JJK 0", autor = "Gege", genero = "Shonen", precio = 15000, stock = 3, sinopsis = "", portadaUrl = "")
    )

    @Test
    fun `calcular total con un solo item`() {
        val total = CartLogic.calculateCartTotal(
            mangas = sampleMangas(),
            cartItems = listOf(CartItem(id = 1, mangaId = 1, cantidad = 2))
        )
        assertEquals(20000, total)
    }

    @Test
    fun `calcular total con varios items`() {
        val total = CartLogic.calculateCartTotal(
            mangas = listOf(
                Manga(id = 1, titulo = "One piece 1", autor = "Oda", genero = "Shonen", precio = 20000, stock = 5, sinopsis = "", portadaUrl = ""),
                Manga(id = 2, titulo = "JJK 0", autor = "Gege", genero = "Shonen", precio = 15000, stock = 3, sinopsis = "", portadaUrl = "")
            ),
            cartItems = listOf(
                CartItem(id = 1, mangaId = 1, cantidad = 1),
                CartItem(id = 2, mangaId = 2, cantidad = 2)
            )
        )
        assertEquals(50000, total) //10000 + 2*15000
    }

    @Test
    fun `validar que no se exceda el stock`() {
        val ok = CartLogic.hasStock(stockDisponible = 5, cantidadEnCarro = 2, cantidadNueva = 3)
        assertTrue(ok)
    }

    @Test
    fun `rechazar cuando se excede el stock`() {
        val ok = CartLogic.hasStock(stockDisponible = 5, cantidadEnCarro = 4, cantidadNueva = 2)
        assertFalse(ok)
    }

    @Test
    fun `aplicar descuento cuando total es grande`() {
        val totalConDescuento = CartLogic.applyDiscountIfBigOrder(60000)
        assertEquals(54000, totalConDescuento) //10% menos
    }
}