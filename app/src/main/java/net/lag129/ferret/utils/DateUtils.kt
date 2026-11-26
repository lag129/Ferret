package net.lag129.ferret.utils

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
        time: Long,
    ): String

    fun getRelativeTime(
        currentTime: Long,
        postedTime: Long
    ): RelativeTime
}

class DateUtilsImpl : DateUtils {

    override fun formatDateTime(time: Long): String {
        val dateTime =
            Instant.fromEpochMilliseconds(time).toLocalDateTime(TimeZone.currentSystemDefault())

        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')

        return "${dateTime.year}/${dateTime.month.number}/${dateTime.day} ${hour}:${minute}"
    }

    override fun getRelativeTime(
        currentTime: Long,
        postedTime: Long
    ): RelativeTime {

        val diffTime = currentTime - postedTime

        return when {
            diffTime < ONE_MINUTE_MILLIS -> RelativeTime.Now

            diffTime < ONE_HOUR_MILLIS -> RelativeTime.Minutes(diffTime / ONE_MINUTE_MILLIS)

            diffTime < ONE_DAY_MILLIS -> RelativeTime.Hours(diffTime / ONE_HOUR_MILLIS)

            else -> Instant.fromEpochMilliseconds(postedTime)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .let { dateTime ->
                    RelativeTime.Date("${dateTime.year}/${dateTime.month.number}/${dateTime.day}")
                }
        }
    }

    companion object {
        private const val ONE_MINUTE_MILLIS = 60000L
        private const val ONE_HOUR_MILLIS = 3600000L
        private const val ONE_DAY_MILLIS = 86400000L
    }
}
