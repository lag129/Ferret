package net.lag129.ferret.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Attachment {

    abstract val attachmentId: String

    abstract val url: String

    abstract val previewUrl: String?

    abstract val remoteUrl: String?

    abstract val previewRemoteUrl: String?

    abstract val textUrl: String?

    abstract val description: String?

    abstract val blurHash: String?

    @Serializable
    @SerialName("image")
    data class Image(

        @SerialName("id")
        override val attachmentId: String,

        @SerialName("url")
        override val url: String,

        @SerialName("preview_url")
        override val previewUrl: String? = null,

        @SerialName("remote_url")
        override val remoteUrl: String? = null,

        @SerialName("preview_remote_url")
        override val previewRemoteUrl: String? = null,

        @SerialName("text_url")
        override val textUrl: String? = null,

        @SerialName("description")
        override val description: String? = null,

        @SerialName("blurhash")
        override val blurHash: String? = null,

        @SerialName("meta")
        val meta: Meta = Meta()

    ) : Attachment() {

        @Serializable
        data class Meta(

            @SerialName("width")
            val width: Long? = null,

            @SerialName("height")
            val height: Long? = null,

            @SerialName("aspect")
            val aspect: Double? = null,

            // @SerialName("focus")
            // val focalPoint: FocalPoint? = null,

            @SerialName("original")
            val original: Meta? = null,

            @SerialName("small")
            val small: Meta? = null
        )
    }
}
