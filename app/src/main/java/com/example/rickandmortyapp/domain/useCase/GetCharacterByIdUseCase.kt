package com.example.rickandmortyapp.domain.useCase

import com.example.rickandmortyapp.domain.repository.CharacterRepository
import javax.inject.Inject

class GetCharacterByIdUseCase @Inject constructor(private val repository: CharacterRepository) {
    suspend operator fun invoke(id: Int) = repository.getCharacterById(id)
}