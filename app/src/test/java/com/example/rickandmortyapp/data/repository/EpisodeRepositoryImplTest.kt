package com.example.rickandmortyapp.data.repository

import androidx.compose.animation.core.animateDecay
import androidx.compose.ui.geometry.Rect
import app.cash.turbine.test
import com.example.rickandmortyapp.data.dto.Episode
import com.example.rickandmortyapp.data.dto.EpisodeResponse
import com.example.rickandmortyapp.data.dto.PageInfo
import com.example.rickandmortyapp.data.remote.RickAndMortyApi
import com.example.rickandmortyapp.domain.model.EpisodeInfo
import com.example.rickandmortyapp.utils.Constants
import com.example.rickandmortyapp.utils.Resource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Before
import retrofit2.HttpException
import retrofit2.Response
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.expect


class EpisodeRepositoryImplTest {
    private lateinit var repository : EpisodeRepositoryImpl
    private lateinit var mockApi : RickAndMortyApi

    @Before
    fun setUp(){
        mockApi = mockk()
        repository = EpisodeRepositoryImpl(mockApi)
    }

    @Test
    fun `getEpisodes should emit Loading then Success with correct data`() = runTest {

        val fakeEpisodeDto = Episode(
            id = 1,
            name = "Episode 1",
            airDate = "airDate?",
            episode = "1",
            characters = listOf("character1","character2"),
            url = "SomeUrl",
            created = "created?"
        )

        val fakeApiResponse = EpisodeResponse(
            info = PageInfo(
                count = 1,
                pages = 1,
                next = "",
                prev = ""
            ),
            results = listOf(fakeEpisodeDto)
        )

        val expectedData = EpisodeInfo(
            id = 1,
            name = "Episode 1",
            airDate = "airDate?",
            episode = "1"
        )

        coEvery { mockApi.getEpisodes(any(),any(),any()) } returns fakeApiResponse

        repository.getEpisodes(1).test {

            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(Resource::class.java)

            val actualData = (successState as Resource.Success).data
            assertThat(actualData).containsExactly(expectedData)

            awaitComplete()


        }

    }

    @Test
    fun `getEpisodes should return Error when api catch HttpException`() = runTest{

        val errorBody = ("Server Error?").toResponseBody()

        val fakeResponse = Response.error<EpisodeResponse>(404,errorBody)

        val httpException = HttpException(fakeResponse)

        val expectedError = "Ошибка сервера"

        coEvery { mockApi.getEpisodes(any(),any(),any()) } throws  httpException

        repository.getEpisodes(1).test {

            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(Resource.Error::class.java)

            val actualData = (errorState as Resource.Error).message
            assertThat(actualData).contains(expectedError)

            awaitComplete()

        }

    }

    @Test
    fun `getEpisodes should return Error when api catch IoException`() = runTest {

        val ioException = IOException()

        val expectedError = "Нет подключения к сети"

        coEvery { mockApi.getEpisodes(any()) } throws ioException

        repository.getEpisodes(1).test {

            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(Resource.Error::class.java)

            val actualMessage = (errorState as Resource.Error).message
            assertThat(actualMessage).contains(expectedError)

            awaitComplete()
        }

    }

    @Test
    fun `getEpisodeById should return Success with correctData`() = runTest {

        val episodeApiResponse = Episode(
            id = 1,
            name = "Episode 1",
            airDate = "airDate?",
            episode = "episode",
            characters = listOf("character1", "character2"),
            url = "someUrl",
            created = "created?"
        )

        val fakeExpectedData = EpisodeInfo(
            id = 1,
            name = "Episode 1",
            airDate = "airDate?",
            episode = "episode",
        )

        coEvery { mockApi.getEpisodeById(any()) } returns episodeApiResponse

        val actualResult = repository.getEpisodeById(1)

        assertThat(actualResult).isInstanceOf(Resource.Success::class.java)

        val actualData = (actualResult as Resource.Success).data
        assertThat(actualData).isEqualTo(fakeExpectedData)

    }

    @Test
    fun `getEpisodeById should return Error when api catch HttpException`() = runTest {

        val expectedError = "Ошибка сервера"

        val errorBody = "Internal server error".toResponseBody()

        val fakeResponse = Response.error<EpisodeResponse>(505,errorBody)
        val httpException = HttpException(fakeResponse)

        coEvery { mockApi.getEpisodeById(1) } throws httpException

        val actualResult = repository.getEpisodeById(1)

        assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

        val actualData = (actualResult as Resource.Error).message
        assertThat(actualData).contains(expectedError)
    }

    @Test
    fun `getEpisodeById should return Error when api catch IoException`() = runTest {

        val ioException = IOException()
        val expectedError = "Нет подключения к сети"

        coEvery { mockApi.getEpisodeById(any()) } throws ioException

        val actualResult = repository.getEpisodeById(1)
        assertThat(actualResult).isInstanceOf(Resource.Error::class.java)

        val actualData = (actualResult as Resource.Error).message
        assertThat(actualData).contains(expectedError)

    }

}