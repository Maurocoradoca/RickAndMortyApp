package com.example.myapplication.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://rickandmortyapi.com/api"

fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 10_000
        requestTimeoutMillis = 30_000
    }
}

class KtorRickAndMortyApi(
    private val client: HttpClient = createHttpClient()
) : RickAndMortyApi {

    override suspend fun getAllCharacters(): List<CharacterDto> = fetchAllPages { page ->
        client.get("$BASE_URL/character") { parameter("page", page) }
            .body<PageDto<CharacterDto>>()
    }

    override suspend fun getAllLocations(): List<LocationDto> = fetchAllPages { page ->
        client.get("$BASE_URL/location") { parameter("page", page) }
            .body<PageDto<LocationDto>>()
    }

    // Pide la página 1 para saber cuántas hay y el resto en paralelo
    private suspend fun <T> fetchAllPages(
        fetchPage: suspend (Int) -> PageDto<T>
    ): List<T> = coroutineScope {
        val first = fetchPage(1)
        val others = (2..first.info.pages)
            .map { page -> async { fetchPage(page) } }
            .awaitAll()
        first.results + others.flatMap { it.results }
    }
}