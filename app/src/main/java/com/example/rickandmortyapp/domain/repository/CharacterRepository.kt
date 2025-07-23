package com.example.rickandmortyapp.domain.repository

import com.example.rickandmortyapp.data.dto.Character
import com.example.rickandmortyapp.domain.model.CharacterInfo
import com.example.rickandmortyapp.domain.model.LocationInfo
import com.example.rickandmortyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query


interface CharacterRepository{

    fun getCharacters(
        page: Int,
        status: String? = null,
        gender: String? = null,
        name: String? = null,
        type: String? = null,
        species: String? = null,
    ) : Flow<Resource<List<CharacterInfo>>>

    suspend fun getCharacterById(id: Int) : Resource<CharacterInfo>

    suspend fun getMultipleCharacters(ids: List<Int>) : Resource<List<CharacterInfo>>

}