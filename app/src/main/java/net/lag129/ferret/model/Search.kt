package net.lag129.ferret.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Search(
    @SerialName("accounts")
    val accounts: List<Account>,

    @SerialName("statuses")
    val statuses: List<Status>,

    @SerialName("hashtags")
    val hashtags: List<Tag>,

    @SerialName("collections")
    val collections: List<Collection> = emptyList(),
)
