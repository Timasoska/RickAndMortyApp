package com.example.rickandmortyapp.domain.repository

import com.example.rickandmortyapp.domain.model.LocationInfo
import com.example.rickandmortyapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocations(
        page: Int,
        name: String? = null,
        type: String? = null,
        dimension: String? = null
    ) : Flow<Resource<List<LocationInfo>>>

    suspend fun getLocationById(id: Int) : Resource<LocationInfo>

    suspend fun getMultipleLocations(ids: List<Int>) : Resource<List<LocationInfo>>


}