package net.lag129.ferret.model.misskey

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Note(
    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("id")
    val id: String,

    @SerialName("reactionAcceptance")
    val reactionAcceptance: ReactionAcceptance?,

    @SerialName("reactionCount")
    val reactionCount: Int,

    @SerialName("reactionEmojis")
    val reactionEmojis: Map<String, String>,

    @SerialName("reactions")
    val reactions: Map<String, String>,

    @SerialName("renoteCount")
    val renoteCount: Int,

    @SerialName("repliesCount")
    val repliesCount: Int,

    @SerialName("text")
    val text: String?,

    @SerialName("user")
    val user: UserLite,

    @SerialName("userId")
    val userId: String,

    @SerialName("visibility")
    val visibility: Visibility,
) {
    enum class ReactionAcceptance {
        @SerialName("likeOnly")
        LIKE_ONLY,

        @SerialName("likeOnlyForRemote")
        LIKE_ONLY_FOR_REMOTE,

        @SerialName("nonSensitiveOnly")
        NON_SENSITIVE_ONLY,

        @SerialName("nonSensitiveOnlyForLocalLikeOnlyForRemote")
        NON_SENSITIVE_ONLY_FOR_LOCAL_LIKE_ONLY_FOR_REMOTE,
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
