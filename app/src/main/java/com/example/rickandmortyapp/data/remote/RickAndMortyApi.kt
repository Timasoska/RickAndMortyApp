package com.example.rickandmortyapp.data.remote

import com.example.rickandmortyapp.data.dto.CharacterResponse
import com.example.rickandmortyapp.data.dto.Episode
import com.example.rickandmortyapp.data.dto.EpisodeResponse
import com.example.rickandmortyapp.data.dto.Location
import com.example.rickandmortyapp.data.dto.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    //Для Character

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int? = null,
        @Query("status") status: String? = null,
        @Query("gender") gender: String? = null,
        @Query("name") name: String? = null,
        @Query("type") type: String? = null,
        @Query("species") species: String? = null,
    ): CharacterResponse //По странице получаем персонажа

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): Character

    @GET("character/{ids}")
    suspend fun getMultipleCharacters(@Path("ids") ids: String): List<Character>

    //Для location

    @GET("location")
    suspend fun getLocations(
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("type") type: String? = null,
        @Query("dimension") dimension: String? = null,
    ): LocationResponse

    @GET("location/{id}")
    suspend fun getLocationById(@Path("id") id: Int) : Location

    @GET("location/{ids}")
    suspend fun getMultipleLocations(@Path("ids")ids: String): List<Location>

    //Для Episode

    @GET("episode")
    suspend fun getEpisodes(
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("episode") episode: String? = null,
    ) : EpisodeResponse

    @GET("episode/{id}")
    suspend fun  getEpisodeById(
        @Path("id") id: Int,
    ) : Episode

    @GET("episode/{ids}")
    suspend fun getMultipleEpisodes(
        @Path("ids") ids: String
    ) : List<Episode>


}