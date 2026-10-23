package net.lag129.ferret.model.misskey

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class UserLite(
    @SerialName("avatarBlurhash")
    val avatarBlurHash: String?,

    @SerialName("avatarDecorations")
    val avatarDecorations: List<AvatarDecoration>,

    @SerialName("avatarUrl")
    val avatarUrl: String,

    @SerialName("emojis")
    val emojis: Map<String, String>,

    @SerialName("host")
    val host: String?,

    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String?,

    @SerialName("onlineStatus")
    val onlineStatus: OnlineStatus?,

    @SerialName("username")
    val username: String,

    @SerialName("userId")
    val userId: String,

    @SerialName("visibility")
    val visibility: Visibility,
) {
    @Serializable
    data class AvatarDecoration(
        @SerialName("id")
        val id: String,

        @SerialName("url")
        val url: String,

        @SerialName("angle")
        val angle: Int,

        @SerialName("flipH")
        val flipH: Boolean,

        @SerialName("offsetX")
        val offsetX: Int,

        @SerialName("offsetY")
        val offsetY: Int,
    )

    enum class OnlineStatus {
        @SerialName("unknown")
        UNKNOWN,

        @SerialName("online")
        ONLINE,

        @SerialName("active")
        ACTIVE,

        @SerialName("offline")
        OFFLINE,
    }

    enum class Visibility {
        @SerialName("public")
        PUBLIC,

        @SerialName("home")
        HOME,

        @SerialName("followers")
        FOLLOWERS,

        @SerialName("specified")
        SPECIFIED,
    }
}
