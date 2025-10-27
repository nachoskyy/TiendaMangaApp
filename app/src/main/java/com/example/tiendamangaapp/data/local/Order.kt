package com.example.tiendamangaapp.data.local

import androidx.room.*

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val total: Int
)

@Entity(
    tableName = "order_line",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class OrderLine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val mangaId: Long,
    val titulo: String,
    val precio: Int,
    val cantidad: Int
)
