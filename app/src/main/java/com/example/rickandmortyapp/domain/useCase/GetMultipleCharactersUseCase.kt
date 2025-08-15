package com.example.rickandmortyapp.domain.useCase

import com.example.rickandmortyapp.domain.model.CharacterInfo
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import com.example.rickandmortyapp.utils.Resource
import javax.inject.Inject

class GetMultipleCharactersUseCase @Inject constructor(private val repository : CharacterRepository) {
    suspend operator fun invoke(ids: List<Int>) = repository.getMultipleCharacters(ids)
}