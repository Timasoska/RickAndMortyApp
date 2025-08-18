package com.example.rickandmortyapp.data.repository

import app.cash.turbine.test
import com.example.rickandmortyapp.data.dto.LocationShort
import com.example.rickandmortyapp.data.dto.Origin
import com.example.rickandmortyapp.data.remote.RickAndMortyApi
import com.example.rickandmortyapp.data.dto.Character
import com.example.rickandmortyapp.data.dto.CharacterResponse
import com.example.rickandmortyapp.data.dto.PageInfo
import com.example.rickandmortyapp.domain.model.CharacterInfo
import com.example.rickandmortyapp.utils.Resource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class CharacterRepositoryImplTest {

    private lateinit var mockApi: RickAndMortyApi
    private lateinit var repository: CharacterRepositoryImpl

    @Before
    fun setUp(){
        mockApi = mockk()
        repository = CharacterRepositoryImpl(api = mockApi)
    }

    @Test
    fun `getCharacters should emit loading state then success with data`() = runTest {

        val fakeCharacterDto = Character( //Сырые данные, копия json ответа api
            id = 1, name = "Rick", status = "Alive", species = "Human", type = "",
            gender = "Male", origin = Origin("Earth", ""), location = LocationShort("", ""),
            image = "url", episode = emptyList(), url = "", created = ""
        )

        val fakeApiResponse = CharacterResponse( //Фейковый ответ api, то что ожидает получить репозиторий
            info = PageInfo(1, 1, null, null),
            results = listOf(fakeCharacterDto)
        )

        val expectedData = CharacterInfo( //Фейковая доменная модель
            id = 1, name = "Rick", status = "Alive", species = "Human", type = "",
            gender = "Male", imageUrl = "url"
        )

        coEvery { mockApi.getCharacters(any(),any(),any(),any(),any(),any()) } returns fakeApiResponse

        repository.getCharacters(1).test {

            val loadingState = awaitItem() //Проверяем состояние загрузки
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(Resource.Success::class.java)

            val actualData = (successState as Resource.Success).data //Проверяем данные
            assertThat(actualData).containsExactly(expectedData)

            awaitComplete()
        }
    }
}

