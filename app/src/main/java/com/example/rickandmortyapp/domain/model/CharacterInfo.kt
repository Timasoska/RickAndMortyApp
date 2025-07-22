package com.example.rickandmortyapp.domain.model

/*
  Чистая и легковесная модель данных для отображения ОДНОГО элемента
  в СПИСКЕ персонажей. Содержит только необходимую информацию.
*/
data class CharacterInfo (
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val imageUrl: String,
)