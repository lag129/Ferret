package net.lag129.ferret.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class CollectionItem(
    @SerialName("id")
    val id: String,

    @SerialName("account_id")
    val accountId: String? = null,

    @SerialName("state")
    val state: String,

    @SerialName("created_at")
    val createdAt: String,
)
