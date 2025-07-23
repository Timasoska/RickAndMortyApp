package com.example.rickandmortyapp.domain.repository

import com.example.rickandmortyapp.domain.model.EpisodeInfo
import com.example.rickandmortyapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {

    fun getEpisodes(
        page: Int,
        name: String? = null,
        episode: String? = null
    ) : Flow<Resource<List<EpisodeInfo>>>

    suspend fun getEpisodeById(id: Int) : Resource<EpisodeInfo>

    suspend fun getMultipleEpisodes(ids: List<Int>) : Resource<List<EpisodeInfo>>

}