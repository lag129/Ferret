package net.lag129.ferret

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.lag129.ferret.repository.FediverseRepository
import net.lag129.ferret.repository.MastodonRepositoryImpl
import net.lag129.ferret.repository.MisskeyRepositoryImpl
import net.lag129.ferret.repository.Platform
import net.lag129.ferret.repository.PreferencesRepository
import net.lag129.ferret.repository.PreferencesRepositoryImpl
import org.koin.dsl.module

val sharedModule = module {

    single<HttpClient> {
        val preferencesRepository = get<PreferencesRepository>()
        HttpClient(get<HttpClientEngine>()) {
            install(Auth) {
                bearer {
                    loadTokens {
                        val bearerToken = preferencesRepository.bearerToken.first()
                        BearerTokens(bearerToken, null)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(createClientPlugin("BaseUrl") {
                onRequest { request, _ ->
                    val serverName = preferencesRepository.serverName.first()
                    request.url {
                        protocol = URLProtocol.HTTPS
                        host = serverName
                    }
                }
            })
        }
    }

    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    factory<FediverseRepository> {
        when (runBlocking { get<PreferencesRepository>().platform.first() }) {
            Platform.MASTODON -> MastodonRepositoryImpl(get())
            Platform.MISSKEY -> MisskeyRepositoryImpl(get())
        }
    }
}
