package com.example.rickandmortyapp.data.repository

import app.cash.turbine.test
import com.example.rickandmortyapp.data.dto.Location
import com.example.rickandmortyapp.data.dto.LocationResponse
import com.example.rickandmortyapp.data.dto.PageInfo
import com.example.rickandmortyapp.data.remote.RickAndMortyApi
import com.example.rickandmortyapp.domain.model.LocationInfo
import com.example.rickandmortyapp.utils.Resource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response



class LocationRepositoryImplTest {

    private lateinit var mockApi: RickAndMortyApi
    private lateinit var repository: LocationRepositoryImpl

    @Before
    fun setUp() {
        mockApi = mockk()
        repository = LocationRepositoryImpl(api = mockApi)
    }

    @Test
    fun `getLocations should emit Loading then Success with correct domain data`() = runTest {
        val fakeDto = Location(
            id = 1,
            name = "Earth",
            type = "someType",
            dimension = "someDimension",
            residents = listOf(
                "https://rickandmortyapi.com/api/character/1",
                "https://rickandmortyapi.com/api/character/2"
            ),
            url = "someUrl",
            created = "created?"
        )

        val fakeApiResponse = LocationResponse(
            info = PageInfo(count = 1, pages = 1, next = "", prev = ""),
            results = listOf(fakeDto)
        )

        val expectedData = LocationInfo(
            id = 1,
            name = "Earth",
            type = "someType",
            dimension = "someDimension",
            residentIds = listOf(1,2)
        )

        coEvery { mockApi.getLocations(any(), any(), any(), any()) } returns fakeApiResponse

        repository.getLocations(1,null,null,null).test {

            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(Resource.Success::class.java)

            val actualData = (successState as Resource.Success).data
            assertThat(actualData).contains(expectedData)

            awaitComplete()

        }
    }

    @Test
    fun `getLocations should emit Loading then Error and catch HttpException`() = runTest {

        val errorBody = "Internal Server Error".toResponseBody()
        val fakeResponse = Response.error<LocationResponse>(505,errorBody)
        val httpException = HttpException(fakeResponse)
        val expectedMessage = "Ошибка сервера"

        coEvery { mockApi.getLocations(any(),any(),any(),any()) } throws httpException

        repository.getLocations(1,null,null,null).test {

            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val actualResult = awaitItem()
            assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

            val actualData = (actualResult as Resource.Error).message
            assertThat(actualData).contains(expectedMessage)

            awaitComplete()
        }
    }

    @Test
    fun `getLocations should emit Loading then Error and catch IoException`() = runTest {

        val ioException = IOException()
        val expectedMessage = "Нет подключения к сети"

        coEvery { mockApi.getLocations(any(),any(),any(),any()) } throws ioException

        repository.getLocations(1,"","","").test {

            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val actualResult = awaitItem()
            assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

            val actualData = actualResult.message
            assertThat(actualData).contains(expectedMessage)

            awaitComplete()
        }
    }

    @Test
    fun `getLocationsById should return Success state with data`() = runTest {

        val fakeApiResponse = Location(
            id = 1,
            name = "name",
            type = "type",
            dimension = "dimension",
            residents = listOf(
                "https://rickandmortyapi.com/api/character/1",
                "https://rickandmortyapi.com/api/character/2"
            ),
            url = "url",
            created = "created"
        )

        val expectedData = LocationInfo(
            id = 1,
            name = "name",
            type = "type",
            dimension = "dimension",
            residentIds = listOf(1,2)
        )

        coEvery { mockApi.getLocationById(any())} returns fakeApiResponse

        val actualResult = repository.getLocationById(1)
        assertThat(actualResult).isInstanceOf(Resource.Success::class.java)

        val actualData = (actualResult as Resource.Success).data
        assertThat(actualData).isEqualTo(expectedData)
    }

    @Test
    fun `getLocationsById should catch HttpException when api call Error`() = runTest {

        val expectedData = "Ошибка сервера"
        val errorBody = "Internal Server Error".toResponseBody()
        val fakeResponse = Response.error<LocationResponse>(505, errorBody)
        val httpException = HttpException(fakeResponse)

        coEvery { mockApi.getLocationById(any()) } throws httpException

        val actualResult = repository.getLocationById(1)
        assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

        val actualData = actualResult.message
        assertThat(actualData).contains(expectedData)
    }

    @Test
    fun `getLocationById should catch IoException when api call Error`() = runTest {

        val expectedData = "Нет подключения к сети"
        val ioException = IOException()

        coEvery { mockApi.getLocationById(any()) } throws ioException

        val actualResult = repository.getLocationById(1)
        assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

        val actualData = repository.getLocationById(1).message
        assertThat(actualData).contains(expectedData)
    }

    @Test
    fun `getMultipleLocations should return Success correct data`() = runTest {

        val ids = listOf(1,2)
        val fakeApiResponse = listOf(Location(
            id = 1,
            name = "",
            type = "type",
            dimension = "dimension",
            residents = listOf("https://rickandmortyapi.com/api/character/1",
                "https://rickandmortyapi.com/api/character/2"),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "created?")
        )
        val expectedData = LocationInfo(
            id = 1,
            name = "",
            type = "type",
            dimension = "dimension",
            residentIds = listOf(1,2)
        )

        coEvery { mockApi.getMultipleLocations(any()) } returns fakeApiResponse

        val actualResult = repository.getMultipleLocations(ids)
        assertThat(actualResult).isInstanceOf(Resource.Success::class.java)

        val actualData = (actualResult as Resource.Success).data
        assertThat(actualData).contains(expectedData)
    }

    @Test
    fun `getMultipleLocations should catch HttpException when api call Error`() = runTest {

        val ids = listOf(1,2)
        val errorBody = "Internal Server Error".toResponseBody()
        val fakeResponse = Response.error<LocationResponse>(505,errorBody)
        val httpException = HttpException(fakeResponse)
        val expectedData = "Ошибка сервера"

        coEvery { mockApi.getMultipleLocations(any()) } throws httpException

        val actualResult = repository.getMultipleLocations(ids)
        assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

        val actualData = (actualResult as Resource.Error).message
        assertThat(actualData).contains(expectedData)
    }

    @Test
    fun `getMultipleLocations should catch IoException when api call Error`() = runTest {

        val ioException = IOException()
        val expectedData = "Нет подключения к сети"
        val ids = listOf(1,2)

        coEvery { mockApi.getMultipleLocations(any()) } throws ioException

        val actualResult = repository.getMultipleLocations(ids)
        assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

        val actualData = (actualResult as Resource.Error).message
        assertThat(actualData).contains(expectedData)

    }


}