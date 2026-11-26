package net.lag129.ferret.utils

import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

sealed interface RelativeTime {
    data object Now : RelativeTime
    data class Minutes(val value: Long) : RelativeTime
    data class Hours(val value: Long) : RelativeTime
    data class Date(val text: String) : RelativeTime
}

interface DateUtils {

    fun formatDateTime(
        time: Instant,
    ): String

    fun getRelativeTime(
        currentTime: Instant,
        postedTime: Instant
    ): RelativeTime
}

internal class DateUtilsImpl(
    private val timeZone: () -> TimeZone = { TimeZone.currentSystemDefault() }
) : DateUtils {

    override fun formatDateTime(time: Instant): String {
        val dateTime = time.toLocalDateTime(timeZone())
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')

        return "${dateTime.year}/${dateTime.month.number}/${dateTime.day} ${hour}:${minute}"
    }

    override fun getRelativeTime(
        currentTime: Instant,
        postedTime: Instant
    ): RelativeTime {

        val diffTime = currentTime - postedTime

        return when {
            diffTime < 1.minutes -> RelativeTime.Now
            diffTime < 1.hours -> RelativeTime.Minutes(diffTime.inWholeMinutes)
            diffTime < 1.days -> RelativeTime.Hours(diffTime.inWholeHours)

            else -> postedTime.toLocalDateTime(timeZone()).let { dateTime ->
                RelativeTime.Date("${dateTime.year}/${dateTime.month.number}/${dateTime.day}")
            }
        }
    }
}
