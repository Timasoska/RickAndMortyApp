package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.remote.RickAndMortyApi
import com.example.rickandmortyapp.domain.model.EpisodeInfo
import com.example.rickandmortyapp.domain.repository.EpisodeRepository
import com.example.rickandmortyapp.mappers.toEpisodeInfo
import com.example.rickandmortyapp.utils.Constants
import com.example.rickandmortyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class EpisodeRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi
) : EpisodeRepository {


    override fun getEpisodes(
        page: Int,
        name: String?,
        episode: String?
    ): Flow<Resource<List<EpisodeInfo>>> = flow {

        emit(Resource.Loading())

        try{
            val apiResponse = api.getEpisodes(
                page = page,
                name = name,
                episode = episode
            )
            val getEpisodesData = apiResponse.results.map { it.toEpisodeInfo() }
            emit(Resource.Success(getEpisodesData))

        } catch (e: HttpException){
            emit(Resource.Error("${Constants.ServerErrorMessage} ${e.message}"))
        } catch (e: IOException){
            emit(Resource.Error("${Constants.NetworkErrorMessage} ${e.message}"))
        }
    }

    override suspend fun getEpisodeById(id: Int): Resource<EpisodeInfo> {
        return try {
            val apiResponse = api.getEpisodeById(id)
            val getEpisodeByIdData = apiResponse.toEpisodeInfo()

            Resource.Success(getEpisodeByIdData)

        } catch (e: HttpException) {
            Resource.Error("${Constants.ServerErrorMessage} ${e.message}")
        } catch (e: IOException) {
            Resource.Error("${Constants.NetworkErrorMessage} ${e.message}")
        }
    }

    override suspend fun getMultipleEpisodes(ids: List<Int>): Resource<List<EpisodeInfo>> {
        return try {
            val idsAsString = ids.joinToString(separator = ",")
            val getMultipleEpisodes = api.getMultipleEpisodes(idsAsString)
            val domainEpisodes = getMultipleEpisodes.map { it.toEpisodeInfo() }

            Resource.Success(domainEpisodes)

        } catch (e: HttpException) {
            Resource.Error("${Constants.ServerErrorMessage} ${e.message}")
        } catch (e: IOException) {
            Resource.Error("${Constants.NetworkErrorMessage} ${e.message}")
        }
    }

}