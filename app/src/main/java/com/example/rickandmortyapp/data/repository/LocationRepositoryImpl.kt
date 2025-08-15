package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.remote.RickAndMortyApi
import com.example.rickandmortyapp.domain.model.LocationInfo
import com.example.rickandmortyapp.domain.repository.LocationRepository
import com.example.rickandmortyapp.mappers.toLocationInfo
import com.example.rickandmortyapp.utils.Constants
import com.example.rickandmortyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi
) : LocationRepository {
    override fun getLocations(
        page: Int,
        name: String?,
        type: String?,
        dimension: String?
    ): Flow<Resource<List<LocationInfo>>> = flow {

        emit(Resource.Loading())

        try {
            val apiResponse = api.getLocations(
                page = page,
                name = name,
                type = type,
                dimension = dimension,
            )
            val getLocationsDomainData = apiResponse.results.map { it.toLocationInfo() }
            emit(Resource.Success(getLocationsDomainData))

        } catch (e: HttpException){
            emit(Resource.Error("${Constants.ServerErrorMessage} ${e.message}"))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.NetworkErrorMessage} ${e.message}"))
        }
    }

    override suspend fun getLocationById(id: Int): Resource<LocationInfo> {
        return try {
            val apiResponse = api.getLocationById(id)
            val domainLocation = apiResponse.toLocationInfo()
            Resource.Success(domainLocation)

        } catch (e: HttpException){
            Resource.Error("${Constants.ServerErrorMessage} ${e.message}")

        } catch (e: IOException) {
            Resource.Error("${Constants.NetworkErrorMessage} ${e.message}")

        }
    }

    override suspend fun getMultipleLocations(ids: List<Int>): Resource<List<LocationInfo>> {
        return try {
            val idsAsString = ids.joinToString(separator = ",")
            val apiResponse = api.getMultipleLocations(idsAsString)
            val domainMultipleLocations = apiResponse.map { it.toLocationInfo() }

            Resource.Success(domainMultipleLocations)
        } catch (e: HttpException){
            Resource.Error("${Constants.ServerErrorMessage} ${e.message}")

        } catch (e: IOException) {
            Resource.Error("${Constants.NetworkErrorMessage} ${e.message}")

        }
    }

}