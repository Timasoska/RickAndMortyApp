package com.example.rickandmortyapp.data.dto

data class LocationResponse(
    val info: PageInfo,
    val results: List<Location>
)

data class Location(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>,
    val url: String,
    val created: String
)

