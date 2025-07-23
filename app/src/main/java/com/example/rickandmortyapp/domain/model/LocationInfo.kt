package com.example.rickandmortyapp.domain.model

data class LocationInfo (
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residentIds: List<Int>, //Получаем количество жителей
)