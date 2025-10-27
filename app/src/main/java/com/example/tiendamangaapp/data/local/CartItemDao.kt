package com.example.tiendamangaapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update


data class CartLine(
    val cartId: Long,
    val mangaId: Long,
    val titulo: String,
    val precio: Int,
    val cantidad: Int
)

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_item")
    suspend fun getAll(): List<CartItem>

    @Query("""
        SELECT ci.id AS cartId, m.id AS mangaId, m.titulo AS titulo, m.precio AS precio, ci.cantidad AS cantidad
        FROM cart_item ci
        JOIN manga m ON m.id = ci.mangaId
        ORDER BY m.titulo ASC
    """)
    suspend fun getCartLines(): List<CartLine>

    @Query("SELECT * FROM cart_item WHERE mangaId = :mangaId LIMIT 1")
    suspend fun findByManga(mangaId: Long): CartItem?

    @Insert
    suspend fun insert(item: CartItem): Long

    @Update
    suspend fun update(item: CartItem)

    @Query("UPDATE cart_item SET cantidad = cantidad + :delta WHERE mangaId = :mangaId")
    suspend fun addDelta(mangaId: Long, delta: Int)

    @Query("DELETE FROM cart_item WHERE mangaId = :mangaId")
    suspend fun deleteByManga(mangaId: Long)

    @Query("DELETE FROM cart_item")
    suspend fun clear()
}
