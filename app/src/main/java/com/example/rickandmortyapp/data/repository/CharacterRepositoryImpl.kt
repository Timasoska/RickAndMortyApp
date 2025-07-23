package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.remote.RickAndMortyApi
import com.example.rickandmortyapp.domain.model.CharacterInfo
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import com.example.rickandmortyapp.mappers.toDomainModel
import com.example.rickandmortyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi
) : CharacterRepository {
    override fun getCharacters(
        page: Int,
        status: String?,
        gender: String?,
        name: String?,
        type: String?,
        species: String?
    ): Flow<Resource<List<CharacterInfo>>> = flow {

        emit(Resource.Loading()) //Эмитим состояние загрузки

        try {
            val ApiResponse = api.getCharacters(
                page = page,
                status = status,
                gender = gender,
                name = name,
                type = type,
                species = species
            )
            val domainData = ApiResponse.results.map { it.toDomainModel() } // мапим список в готовые домен модели
            emit(Resource.Success(domainData)) //Эмитим готовый список

        } catch (e: HttpException) { // Ловим ошибки HTTP (404, 508 и ТД)
            emit(Resource.Error("Ошибка сервера: ${e.message}")) //Эмитим ошибку
        } catch (e: IOException) { // Ловим ошибки сети (нет интернета)
            emit(Resource.Error( "Нет подключения к сети. Проверьте интернет")) //Эмитим ошибку
        }

    }

    override suspend fun getCharacterById(id: Int): Resource<CharacterInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getMultipleCharacters(ids: List<Int>): Resource<List<CharacterInfo>> {
        TODO("Not yet implemented")
    }

}