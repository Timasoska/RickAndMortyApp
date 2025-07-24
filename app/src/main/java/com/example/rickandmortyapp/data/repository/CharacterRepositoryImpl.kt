package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.remote.RickAndMortyApi
import com.example.rickandmortyapp.domain.model.CharacterInfo
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import com.example.rickandmortyapp.mappers.toCharacterInfo
import com.example.rickandmortyapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
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
            val apiResponse = api.getCharacters(
                page = page,
                status = status,
                gender = gender,
                name = name,
                type = type,
                species = species
            )
            val getCharacterData = apiResponse.results.map { it.toCharacterInfo() } // мапим список в готовые домен модели
            emit(Resource.Success(getCharacterData)) //Эмитим готовый список

        } catch (e: HttpException) { // Ловим ошибки HTTP (404, 508 и ТД)
            emit(Resource.Error("Ошибка сервера: ${e.message}")) //Эмитим ошибку
        } catch (e: IOException) { // Ловим ошибки сети (нет интернета)
            emit(Resource.Error( "Нет подключения к сети. Проверьте интернет")) //Эмитим ошибку
        }

    }

    override suspend fun getCharacterById(id: Int): Resource<CharacterInfo> {
        return try {
            val apiResponse = api.getCharacterById(id)
            val getCharacterByIdData = apiResponse.toCharacterInfo()

            Resource.Success(getCharacterByIdData)

        } catch (e: HttpException) {
            Resource.Error("Ошибка сервера: ${e.message}")
        } catch (e: IOException) {
            Resource.Error("Нет подключения к сети. Проверьте интернет")
        }
    }

    override suspend fun getMultipleCharacters(ids: List<Int>): Resource<List<CharacterInfo>> {
        return try {
            val idsAsString = ids.joinToString(separator = ",") //Подготавливаем данные в строку для апи
            val getMultipleCharactersData = api.getMultipleCharacters(idsAsString) // Вызываем апи
            val domainCharacters = getMultipleCharactersData.map { it.toCharacterInfo() } //Маппим их из листа в доменную модель

            Resource.Success(domainCharacters)

        } catch (e: HttpException) {
            Resource.Error("Ошибка сервера: ${e.message}")
        } catch (e: IOException) {
            Resource.Error("Нет подключения к сети. Проверьте интернет")
        }

    }

}