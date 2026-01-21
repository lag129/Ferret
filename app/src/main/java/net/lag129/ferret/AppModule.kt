package net.lag129.ferret

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    viewModel { TimelineViewModel(get()) }

    viewModel { AuthViewModel(get()) }
}
