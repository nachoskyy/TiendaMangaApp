package com.example.tiendamangaapp.core

import com.example.tiendamangaapp.data.local.CartItem
import com.example.tiendamangaapp.data.local.Manga

object CartLogic {

    fun calculateCartTotal(mangas: List<Manga>, cartItems: List<CartItem>): Int {
        val priceById = mangas.associateBy({ it.id }, { it.precio })
        return cartItems.sumOf { item ->
            val price = priceById[item.mangaId] ?: 0
            price * item.cantidad
        }
    }

    fun hasStock(stockDisponible: Int, cantidadEnCarro: Int, cantidadNueva: Int): Boolean {
        return cantidadEnCarro + cantidadNueva <= stockDisponible
    }

    fun applyDiscountIfBigOrder(total: Int): Int {
        // 10% de descuento si el total es >= 50000
        return if (total >= 50_000) (total * 0.9).toInt() else total
    }
}