package com.example.lab_5.repository

import android.content.Context
import com.example.lab_5.data.Airport
import com.example.lab_5.data.AirportDao
import com.example.lab_5.data.Favorite
import com.example.lab_5.data.FavoriteDao
import com.example.lab_5.data.FlightSearchDatabase
import com.example.lab_5.data.PreferencesManager
import kotlinx.coroutines.flow.Flow

class FlightRepository(context: Context) {
    private val db = FlightSearchDatabase.getDatabase(context)
    private val airportDao: AirportDao = db.airportDao()
    private val favoriteDao: FavoriteDao = db.favoriteDao()
    private val preferencesManager = PreferencesManager(context)

    fun getSearchQueryFlow(): Flow<String?> = preferencesManager.searchQueryFlow
    suspend fun saveSearchQuery(query: String) = preferencesManager.saveSearchQuery(query)

    suspend fun searchAirports(query: String): List<Airport> =
        airportDao.searchAirports("%$query%")

    suspend fun getAllAirportsSorted(): List<Airport> = airportDao.getAllAirportsSorted()
    suspend fun getAirportByCode(iataCode: String): Airport? = airportDao.getAirportByCode(iataCode)

    suspend fun getAllFavorites(): List<Favorite> = favoriteDao.getAllFavorites()
    suspend fun insertFavorite(favorite: Favorite) = favoriteDao.insertFavorite(favorite)
    suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavorite(favorite)
    suspend fun getFavorite(departure: String, destination: String): Favorite? = favoriteDao.getFavorite(departure, destination)
} 