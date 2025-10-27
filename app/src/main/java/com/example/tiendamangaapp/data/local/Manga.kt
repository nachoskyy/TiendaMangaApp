package com.example.tiendamangaapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga")
data class Manga(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val autor: String,
    val genero: String,
    val precio: Int,
    val stock: Int,
    val sinopsis: String,
    val portadaUrl: String
)
