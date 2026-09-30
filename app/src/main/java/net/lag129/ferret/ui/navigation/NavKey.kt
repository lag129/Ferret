package net.lag129.ferret.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import net.lag129.ferret.model.Account
import net.lag129.ferret.ui.compose.StatusCardData

@Serializable
sealed interface FerretNavKey : NavKey

@Serializable
data class Detail(val data: StatusCardData) : FerretNavKey

@Serializable
data object Home : FerretNavKey

@Serializable
data object Login : FerretNavKey

@Serializable
data class Media(val url: String, val description: String?) : FerretNavKey

@Serializable
data object MyProfile : FerretNavKey

@Serializable
data object Setting : FerretNavKey

@Serializable
data class TimelineProfile(val id: String, val account: Account?) : FerretNavKey
