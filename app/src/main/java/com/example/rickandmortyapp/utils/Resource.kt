package com.example.rickandmortyapp.utils

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)//Сообщаем что данные загрузились и передадим потом их
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)//Сообщим что ошибка и передаем сообщение о ней
    class Loading<T>(data: T? = null) : Resource<T>(data)//Состояние при котором мы сообщим что данные загружены
}