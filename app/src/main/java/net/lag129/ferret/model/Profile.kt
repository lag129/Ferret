package net.lag129.ferret.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Profile(
    @SerialName("id")
    val id: String,

    @SerialName("username")
    val username: String,

    @SerialName("acct")
    val acct: String,

    @SerialName("url")
    val url: String,

    @SerialName("display_name")
    val displayName: String,

    @SerialName("note")
    val note: String,

    @SerialName("avatar")
    val avatar: String,

    @SerialName("header")
    val header: String,

    @SerialName("emojis")
    val emojis: List<Post.Emoji>,

    @SerialName("statuses_count")
    val statusesCount: Int,

    @SerialName("followers_count")
    val followersCount: Int,

    @SerialName("following_count")
    val followingCount: Int,
)
