package com.example.tiendamangaapp.core

import android.content.Context
import com.example.tiendamangaapp.data.local.AppDatabase

object ServiceLocator {
    lateinit var db: AppDatabase
        private set

    fun init(context: Context) {
        if (!this::db.isInitialized) {
            db = AppDatabase.getInstance(context)
        }
    }
}