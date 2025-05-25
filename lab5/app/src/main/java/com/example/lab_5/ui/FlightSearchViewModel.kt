package com.example.lab_5.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_5.data.Airport
import com.example.lab_5.data.Favorite
import com.example.lab_5.repository.FlightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed class UiState {
    object Empty : UiState()
    data class Suggestions(val airports: List<Airport>) : UiState()
    data class Flights(val departure: Airport, val destinations: List<Airport>, val favorites: List<Favorite>) : UiState()
    data class Favorites(val favorites: List<Triple<Favorite, Airport?, Airport?>>) : UiState()
}

class FlightSearchViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = FlightRepository(app)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        repository.getSearchQueryFlow().onEach { query ->
            query?.let { setSearchQuery(it, save = false) }
        }.launchIn(viewModelScope)
    }

    fun setSearchQuery(query: String, save: Boolean = true) {
        _searchQuery.value = query
        if (save) {
            viewModelScope.launch { repository.saveSearchQuery(query) }
        }
        if (query.isBlank()) {
            loadFavorites()
        } else {
            viewModelScope.launch {
                val suggestions = repository.searchAirports(query)
                _uiState.value = UiState.Suggestions(suggestions)
            }
        }
    }

    fun selectAirport(airport: Airport) {
        viewModelScope.launch {
            val allAirports = repository.getAllAirportsSorted().filter { it.iata_code != airport.iata_code }
            val favorites = repository.getAllFavorites().filter { it.departure_code == airport.iata_code }
            _uiState.value = UiState.Flights(airport, allAirports, favorites)
        }
    }

    fun addFavorite(departure: Airport, destination: Airport) {
        viewModelScope.launch {
            val favorite = Favorite(departure_code = departure.iata_code, destination_code = destination.iata_code)
            repository.insertFavorite(favorite)
            selectAirport(departure)
        }
    }

    fun removeFavorite(favorite: Favorite, departure: Airport) {
        viewModelScope.launch {
            repository.deleteFavorite(favorite)
            selectAirport(departure)
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            val favorites = repository.getAllFavorites()
            val pairs = favorites.map { fav ->
                val dep = repository.getAirportByCode(fav.departure_code)
                val dest = repository.getAirportByCode(fav.destination_code)
                Triple(fav, dep, dest)
            }
            _uiState.value = UiState.Favorites(pairs)
        }
    }
} 