package com.example.rickandmortyapp.data.repository

import app.cash.turbine.test
import com.example.rickandmortyapp.data.dto.LocationShort
import com.example.rickandmortyapp.data.dto.Origin
import com.example.rickandmortyapp.data.remote.RickAndMortyApi
import com.example.rickandmortyapp.data.dto.Character
import com.example.rickandmortyapp.data.dto.CharacterResponse
import com.example.rickandmortyapp.data.dto.PageInfo
import com.example.rickandmortyapp.domain.model.CharacterInfo
import com.example.rickandmortyapp.utils.Constants
import com.example.rickandmortyapp.utils.Resource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertFailsWith


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


    @Test
    fun `getCharacters should emit Loading then Error WHEN api call fails with HttpException`() = runTest {

        val errorBody = "Internal Server Error".toResponseBody(null) //Тело фейковой ошибки. Создаем ResponseBody (errorBody).

        val fakeErrorResponse = Response.error<CharacterResponse>(500,errorBody) //Чтобы создать Response, нам нужно ResponseBody

        val httpException = HttpException(fakeErrorResponse) //Чтобы создать HttpException, нам нужен Response

        val expectedErrorMessage = "Ошибка сервера"

        coEvery { mockApi.getCharacters(any(),any(),any(),any(), any(),any())} throws httpException

        repository.getCharacters(1).test {
            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(Resource.Error::class.java)

            val actualMessage = (errorState as Resource.Error).message
            assertThat(actualMessage).contains(expectedErrorMessage)

            awaitComplete()
        }
    }

    @Test
    fun `getCharacters should emit Loading then Error When api calls fails with IoException`() = runTest {

        val ioException = IOException()

        val expectedErrorMessage = "Нет подключения к сети"

        coEvery { mockApi.getCharacters(any(),any(),any(),any(),any(),any()) } throws ioException

        repository.getCharacters(1).test {
            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(Resource.Error::class.java)

            val actualMessage = (errorState as Resource.Error).message
            assertThat(actualMessage).contains(expectedErrorMessage)

            awaitComplete()
        }
    }

    @Test
    fun `getCharacterById should return Success with correct character data`() = runTest {

        val fakeApiResponse = Character(
            id = 1, name = "Rick", status = "Alive", species = "Human", type = "",
            gender = "Male", origin = Origin("Earth", ""), location = LocationShort("", ""),
            image = "url", episode = emptyList(), url = "", created = ""
        )

        val fakeExpectedData = CharacterInfo(
            id = 1, name = "Rick", status = "Alive", species = "Human", type = "",
            gender = "Male", imageUrl = "url"
        )

        coEvery { mockApi.getCharacterById(any()) } returns fakeApiResponse

        val actualResult = repository.getCharacterById(1)

        assertThat(actualResult).isInstanceOf(Resource.Success::class.java)

        val actualData = (actualResult as Resource.Success).data
        assertThat(actualData).isEqualTo(fakeExpectedData)
    }

    @Test
    fun `getCharacterById should return Error WHEN api call fails with HttpException`() = runTest {

        val fakeErrorBody = "Internal Server Error".toResponseBody()
        val fakeResponse = Response.error<CharacterResponse>(505,fakeErrorBody)
        val httpException = HttpException(fakeResponse)

        val expectedData = "Ошибка сервера"

        coEvery { mockApi.getCharacterById(any()) } throws httpException

        val actualResult = repository.getCharacterById(1)

        assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

        val actualData = (actualResult as Resource.Error).message
        assertThat(actualData).contains(expectedData)

    }

    @Test
    fun `getCharacterById should return Error WHEN api call fails with IoException`() = runTest {

        val ioException = IOException()

        val expectedData = "Нет подключения к сети"

        coEvery { mockApi.getCharacterById(any()) } throws ioException

        val actualResult = repository.getCharacterById(1)

        assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

        val actualData = (actualResult as Resource.Error).message
        assertThat(actualData).contains(expectedData)

    }

    @Test
    fun `getMultipleCharacters should return Success with correct data`() = runTest {

        val fakeCharacterDto1 = Character(
            id = 1, name = "Rick", status = "Alive", species = "Human", type = "",
            gender = "Male", origin = Origin("Earth", ""), location = LocationShort("", ""),
            image = "url", episode = emptyList(), url = "", created = ""
        )

        val fakeCharacterDto2 = Character(
            id = 2, name = "Morty", status = "Alive", species = "Human", type = "",
            gender = "Male", origin = Origin("Earth", ""), location = LocationShort("", ""),
            image = "url1", episode = emptyList(), url = "", created = ""
        )

        val fakeDomainCharacter1 = CharacterInfo(
            id = 1, name = "Rick", status = "Alive", species = "Human", type = "",
            gender = "Male", imageUrl = "url"
        )

        val fakeDomainCharacter2 = CharacterInfo(
            id = 2, name = "Morty", status = "Alive", species = "Human", type = "",
            gender = "Male", imageUrl = "url1",
        )

        val fakeApiResponse = listOf(fakeCharacterDto1, fakeCharacterDto2)

        val expectedData = listOf(fakeDomainCharacter1, fakeDomainCharacter2)

        val idsToRequest = listOf(1, 2)

        coEvery { mockApi.getMultipleCharacters(any()) } returns fakeApiResponse

        val actualResult = repository.getMultipleCharacters(idsToRequest)

        assertThat(actualResult).isInstanceOf(Resource.Success::class.java)

        val actualData = (actualResult as Resource.Success).data
        assertThat(actualData).isEqualTo(expectedData)


    }



}


