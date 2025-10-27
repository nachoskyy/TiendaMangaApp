package com.example.tiendamangaapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga ORDER BY titulo ASC")
    fun watchAll(): Flow<List<Manga>>
    @Query("SELECT * FROM manga ORDER BY titulo ASC")
    suspend fun getAll(): List<Manga>

    @Query("SELECT * FROM manga WHERE id = :id")
    suspend fun getById(id: Long): Manga?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Manga>)

    @Query("DELETE FROM manga")
    suspend fun clear()
}