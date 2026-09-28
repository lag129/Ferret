package net.lag129.ferret.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import net.lag129.ferret.model.Account
import net.lag129.ferret.ui.compose.StatusCardData

@Serializable
data class Detail(val data: StatusCardData) : NavKey

@Serializable
data object Home : NavKey

@Serializable
data object Login : NavKey

@Serializable
data class Media(val url: String, val description: String?) : NavKey

@Serializable
data class Profile(val id: String, val account: Account?) : NavKey

@Serializable
data object Setting : NavKey

@Serializable
data object Splash : NavKey

@Serializable
data class TimelineProfile(val id: String, val account: Account?) : NavKey
