package com.example.tiendamangaapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_item")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mangaId: Long,
    val cantidad: Int
)