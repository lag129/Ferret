package net.lag129.ferret

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FerretApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FerretApp)
            modules(appModule)
        }
    }
}
