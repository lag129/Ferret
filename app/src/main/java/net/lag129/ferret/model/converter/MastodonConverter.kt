package net.lag129.ferret.model.converter

import net.lag129.ferret.model.Post
import net.lag129.ferret.model.Profile
import net.lag129.ferret.model.mastodon.Account
import net.lag129.ferret.model.mastodon.CustomEmoji
import net.lag129.ferret.model.mastodon.Status

object MastodonConverter {
    fun Status.toPost(): Post {
        val target = this.reblog ?: this
        return Post(
            id = id,
            displayName = target.account.displayName,
            userName = target.account.acct,
            avatarUrl = target.account.avatar,
            createdAt = target.createdAt,
            content = target.content,
            profile = target.account.toProfile(),
            card = target.card,
            displayNameEmojis = target.account.emojis.map { it.toPostEmoji() },
            emojis = target.emojis.map { it.toPostEmoji() },
            mediaAttachments = target.mediaAttachments,
            sensitive = target.sensitive,
            spoilerText = target.spoilerText,
            reactions = target.emojiReactions.orEmpty(),
        )
    }

    fun Account.toProfile(): Profile {
        return Profile(
            id = id,
            username = username,
            acct = acct,
            url = url,
            displayName = displayName,
            note = note,
            avatar = avatar,
            header = header,
            emojis = emojis.map { it.toPostEmoji() },
            statusesCount = statusesCount,
            followersCount = followersCount,
            followingCount = followingCount,
        )
    }

    fun CustomEmoji.toPostEmoji(): Post.Emoji {
        return Post.Emoji(
            shortcode = shortcode,
            url = url,
            width = width,
            height = height,
        )
    }
}
