package com.example.rickandmortyapp.data.dto
//Вспомогательная модель для данных о пагинации.
//Является частью ответа для любого запроса, возвращающего список.

data class PageInfo (
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)
