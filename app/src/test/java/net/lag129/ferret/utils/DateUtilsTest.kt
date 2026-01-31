package net.lag129.ferret.utils

import android.content.Context
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DateUtilsTest : StringSpec() {

    val mockContext = mockk<Context>()
    val dateUtils = DateUtilsImpl(mockContext)

    init {

        "now" {
            every { mockContext.getString(any()) } returns "now"
            dateUtils.getRelativeTimeSpanString(
                CURRENT_TIME,
                CURRENT_TIME
            ) shouldBe "now"
        }

        "3 minutes ago" {
            every { mockContext.getString(any(), any()) } answers {
                val format = "%sm"
                val args = secondArg<Array<Any>>()
                format.format(*args)
            }
            dateUtils.getRelativeTimeSpanString(
                CURRENT_TIME,
                CURRENT_TIME - 3L * ONE_MINUTE_MILLIS
            ) shouldBe "3m"
        }

        "3 hours ago" {
            every { mockContext.getString(any(), any()) } answers {
                val format = "%sh"
                val args = secondArg<Array<Any>>()
                format.format(*args)
            }
            dateUtils.getRelativeTimeSpanString(
                CURRENT_TIME,
                CURRENT_TIME - 3L * ONE_HOUR_MILLIS
            ) shouldBe "3h"
        }

        "3 days ago" {
            dateUtils.getRelativeTimeSpanString(
                CURRENT_TIME,
                CURRENT_TIME - 3L * ONE_DAY_MILLIS
            ) shouldBe "2025/12/29"
        }
    }

    companion object {
        /**
         * 2026/01/01 00:00:00
         */
        private const val CURRENT_TIME = 1767193200000L
        private const val ONE_MINUTE_MILLIS = 60000L
        private const val ONE_HOUR_MILLIS = 3600000L
        private const val ONE_DAY_MILLIS = 86400000L
    }
}
