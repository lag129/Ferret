package net.lag129.ferret

import net.lag129.ferret.utils.DateUtils
import net.lag129.ferret.utils.DateUtilsImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    viewModel { TimelineViewModel(get()) }

    viewModel { AuthViewModel(get()) }

    single<DateUtils> { DateUtilsImpl(get()) }
}
