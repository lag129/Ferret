package net.lag129.ferret

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.lag129.ferret.utils.DateUtils
import net.lag129.ferret.utils.DateUtilsImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<HttpClient> {
        val preferencesRepository = get<PreferencesRepository>()
        val serverName = runBlocking { preferencesRepository.serverName.first() }
        HttpClient(CIO) {
            defaultRequest { url("https://$serverName/") }
            install(Auth) {
                bearer {
                    loadTokens {
                        val bearerToken = preferencesRepository.bearerToken.first()
                        BearerTokens(bearerToken, bearerToken)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpCache)
        }
    }

    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    single<MastodonRepository> { MastodonRepositoryImpl(get()) }

    viewModel { TimelineViewModel(get()) }

    viewModel { ProfileViewModel(get()) }

    viewModel { AuthViewModel(get()) }

    single<DateUtils> { DateUtilsImpl(get()) }
}
