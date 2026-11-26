package net.lag129.ferret

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.lag129.ferret.repository.FediverseRepository
import net.lag129.ferret.repository.MastodonRepositoryImpl
import net.lag129.ferret.repository.MisskeyRepositoryImpl
import net.lag129.ferret.repository.Platform
import net.lag129.ferret.repository.PreferencesRepository
import net.lag129.ferret.repository.PreferencesRepositoryImpl
import org.koin.dsl.module

val sharedModule = module {

    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    factory<FediverseRepository> {
        when (runBlocking { get<PreferencesRepository>().platform.first() }) {
            Platform.MASTODON -> MastodonRepositoryImpl(get())
            Platform.MISSKEY -> MisskeyRepositoryImpl(get())
        }
    }
}
