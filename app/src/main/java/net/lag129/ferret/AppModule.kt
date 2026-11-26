package net.lag129.ferret

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.tink.AeadSerializer
import androidx.room.Room
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplate
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.PredefinedAeadParameters
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import net.lag129.ferret.db.CachedStatusDao
import net.lag129.ferret.db.RoomDatabase
import net.lag129.ferret.ui.MainViewModel
import net.lag129.ferret.ui.screen.login.LoginViewModel
import net.lag129.ferret.ui.screen.profile.MyProfileViewModel
import net.lag129.ferret.ui.screen.profile.ProfileViewModel
import net.lag129.ferret.ui.screen.search.SearchViewModel
import net.lag129.ferret.ui.screen.timeline.TimelineViewModel
import net.lag129.ferret.utils.DateUtils
import net.lag129.ferret.utils.DateUtilsImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<HttpClientEngine> { CIO.create() }

    single<RoomDatabase> {
        Room.databaseBuilder(get(), RoomDatabase::class.java, "ferret_db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single<CachedStatusDao> { get<RoomDatabase>().cachedStatusDao() }

    viewModel { TimelineViewModel(get(), get()) }

    viewModel { ProfileViewModel(get()) }

    viewModel { MyProfileViewModel(get()) }

    viewModel { LoginViewModel(get()) }

    viewModel { MainViewModel(get()) }

    viewModel { SearchViewModel(get()) }

    single<DateUtils> { DateUtilsImpl(get()) }

    single<DataStore<Preferences>> {
        val context = get<Context>()
        val fileName = "preferences"
        AeadConfig.register()

        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, "keyset", "keyset_prefs")
            .withKeyTemplate(KeyTemplate.createFrom(PredefinedAeadParameters.AES256_GCM))
            .withMasterKeyUri("android-keystore://master_key")
            .build()
            .keysetHandle

        DataStoreFactory.create(
            serializer = AeadSerializer(
                aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java),
                wrappedSerializer = PreferencesFileSerializer,
                associatedData = fileName.encodeToByteArray(),
            ),
            produceFile = { context.preferencesDataStoreFile(fileName) }
        )
    }
}
