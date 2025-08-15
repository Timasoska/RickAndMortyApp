package com.example.rickandmortyapp.domain.useCase

import com.example.rickandmortyapp.domain.repository.CharacterRepository
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(private val repository : CharacterRepository) {
    operator fun invoke(page: Int) = repository.getCharacters(page)
}