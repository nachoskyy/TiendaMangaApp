package com.example.tiendamangaapp.data.local

import androidx.room.*

data class HistoryEntry(
    val orderId: Long,
    val createdAt: Long,
    val total: Int,
    val items: Int
)

@Dao
interface OrderDao {
    @Transaction
    suspend fun placeOrderFromCart(
        cartDao: CartItemDao,
        mangaDao: MangaDao
    ): Long {
        val cart = cartDao.getAll()
        if (cart.isEmpty()) return -1
        val lines = cart.mapNotNull { ci ->
            val m = mangaDao.getById(ci.mangaId) ?: return@mapNotNull null
            OrderLine(orderId = 0, mangaId = m.id, titulo = m.titulo, precio = m.precio, cantidad = ci.cantidad)
        }
        val total = lines.sumOf { it.precio * it.cantidad }
        val orderId = insert(Order(createdAt = System.currentTimeMillis(), total = total))
        insertLines(lines.map { it.copy(orderId = orderId) })
        cartDao.clear()
        return orderId
    }

    @Insert suspend fun insert(order: Order): Long
    @Insert suspend fun insertLines(lines: List<OrderLine>)

    @Query("""
        SELECT o.id AS orderId, o.createdAt AS createdAt, o.total AS total,
               COALESCE(SUM(ol.cantidad),0) AS items
        FROM orders o
        LEFT JOIN order_line ol ON ol.orderId = o.id
        GROUP BY o.id, o.createdAt, o.total
        ORDER BY o.createdAt DESC
    """)
    suspend fun getHistory(): List<HistoryEntry>

    @Query("SELECT * FROM order_line WHERE orderId = :orderId")
    suspend fun getLines(orderId: Long): List<OrderLine>
}
