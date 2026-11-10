package com.example.myapplication.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://rickandmortyapi.com/api"
private const val MAX_RETRIES = 2
private const val RETRY_DELAY_MS = 30_000L

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

    private suspend fun <T> fetchAllPages(
        fetchPage: suspend (Int) -> PageDto<T>
    ): List<T> {
        val first = withRetry { fetchPage(1) }
        val all = first.results.toMutableList()
        for (page in 2..first.info.pages) {
            all += withRetry { fetchPage(page) }.results
        }
        return all
    }

    private suspend fun <R> withRetry(block: suspend () -> R): R {
        var attempt = 0
        while (true) {
            try {
                return block()
            } catch (e: ClientRequestException) {
                if (e.response.status.value != 429 || attempt >= MAX_RETRIES) throw e
                attempt++
                delay(RETRY_DELAY_MS)
            }
        }
    }
}