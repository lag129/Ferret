package net.lag129.ferret.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Collection(
    @SerialName("id")
    val id: String,

    @SerialName("account_id")
    val accountId: String,

    @SerialName("uri")
    val uri: String,

    @SerialName("url")
    val url: String? = null,

    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String,

    @SerialName("language")
    val language: String? = null,

    @SerialName("local")
    val local: Boolean,

    @SerialName("sensitive")
    val sensitive: Boolean,

    @SerialName("discoverable")
    val discoverable: Boolean,

    @SerialName("tag")
    val tag: ShallowTag? = null,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("item_count")
    val itemCount: Int,

    @SerialName("item")
    val items: List<CollectionItem>,
)
