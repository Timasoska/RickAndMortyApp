package com.example.rickandmortyapp.data.dto

data class CharacterResponse(
    val info: PageInfo,
    val results: List<Character>
)

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Origin,
    val location: LocationShort,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

data class Origin(
    val name: String,
    val url: String
)

data class LocationShort(
    val name: String,
    val url: String
)

