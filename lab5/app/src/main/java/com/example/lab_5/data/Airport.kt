package com.example.lab_5.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airport")
data class Airport(
    @PrimaryKey val id: Int,
    val iata_code: String,
    val name: String,
    val passengers: Int
) 