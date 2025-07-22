package com.example.rickandmortyapp.data.dto

data class EpisodeResponse (
    val info: PageInfo,
    val results: List<Episode>
)

data class Episode (
    val id: Int,
    val name: String,
    val airDate: String,
    val episode: String,
    val characters: List<String>,
    val url: String,
    val created: String
)