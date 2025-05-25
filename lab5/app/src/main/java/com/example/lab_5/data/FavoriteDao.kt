package com.example.lab_5.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    suspend fun getAllFavorites(): List<Favorite>

    @Insert
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite WHERE departure_code = :departure AND destination_code = :destination LIMIT 1")
    suspend fun getFavorite(departure: String, destination: String): Favorite?
} 