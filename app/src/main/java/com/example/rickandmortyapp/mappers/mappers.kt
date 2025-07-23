package com.example.rickandmortyapp.mappers

import com.example.rickandmortyapp.data.dto.Character
import com.example.rickandmortyapp.data.dto.Episode
import com.example.rickandmortyapp.data.dto.Location
import com.example.rickandmortyapp.domain.model.CharacterInfo
import com.example.rickandmortyapp.domain.model.EpisodeInfo
import com.example.rickandmortyapp.domain.model.LocationInfo


fun Character.toDomainModel(): CharacterInfo{
    return CharacterInfo(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        type = this.type,
        gender = this.gender,
        imageUrl = this.image
    )
}

fun Episode.toDomainModel(): EpisodeInfo {
    return EpisodeInfo(
        id = this.id,
        name = this.name,
        airDate = this.airDate,
        episode = this.episode
    )
}

fun Location.toDomainModel(): LocationInfo {
    return LocationInfo(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension,
        residentIds = this.residents.mapNotNull { url -> //Парсим url чтобы получить количество
            if (url.isNotBlank()) {
                url.substringAfterLast('/').toIntOrNull()
            } else {
                null
            }
        }
    )
}