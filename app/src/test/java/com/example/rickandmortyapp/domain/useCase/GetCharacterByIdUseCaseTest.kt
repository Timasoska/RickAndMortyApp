package com.example.rickandmortyapp.domain.useCase

import com.example.rickandmortyapp.data.repository.CharacterRepositoryImpl
import com.example.rickandmortyapp.domain.model.CharacterInfo
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import com.example.rickandmortyapp.utils.Resource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test


class GetCharacterByIdUseCaseTest {

    private lateinit var mockRepository : CharacterRepository
    private lateinit var useCase : GetCharacterByIdUseCase

    @Before
    fun setUp() {
        mockRepository = mockk<CharacterRepositoryImpl>()
        useCase = GetCharacterByIdUseCase(mockRepository)
    }

    @Test
    fun `invoke should return success resource from repository`() = runTest {

        val fakeCharacterInfo = CharacterInfo (
            id = 1,
            name = "",
            status = "",
            species = "",
            type = "",
            gender = "",
            imageUrl = ""
        )
        val expectedData = Resource.Success(fakeCharacterInfo)

        coEvery { mockRepository.getCharacterById(1) } returns expectedData

        val actualResult = useCase(id = 1)

        assertThat(actualResult).isEqualTo(expectedData)

        coVerify(exactly = 1){ mockRepository.getCharacterById(1)}

    }

}