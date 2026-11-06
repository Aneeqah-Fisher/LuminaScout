package com.example.luminascout

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.luminascout.Data.RetrofitClient
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/** Sunrise and evening golden-hour notifications for Cape Town only. */
object GoldenHourReminderScheduler {
    private const val TAG = "CapeTownReminders"
    private const val SUNRISE_WORK = "sunrise_reminder"
    private const val SUNSET_WORK = "golden_hour_reminder"
    private val capeTownZone = ZoneId.of("Africa/Johannesburg")
    private val timeFormat = DateTimeFormatter.ofPattern("h:mm a")

    /** Call from a coroutine when the app opens. Fetches Cape Town's actual sun times. */
    suspend fun scheduleCapeTown(context: Context) {
        try {
            val weather = RetrofitClient.apiService.getCurrentWeather(
                lat = -33.9249,
                lon = 18.4241
            )
            val daily = weather.daily ?: return
            scheduleNext(
                context = context,
                rawTimes = daily.sunrise.orEmpty(),
                minutesBefore = 60,
                uniqueName = SUNRISE_WORK,
                reminderType = "sunrise"
            )
            scheduleNext(
                context = context,
                rawTimes = daily.sunset.orEmpty(),
                minutesBefore = 90,
                uniqueName = SUNSET_WORK,
                reminderType = "golden_hour"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unable to fetch Cape Town sunrise/sunset", e)
        }
    }

    private fun scheduleNext(
        context: Context,
        rawTimes: List<String>,
        minutesBefore: Long,
        uniqueName: String,
        reminderType: String
    ) {
        val now = Instant.now()
        val next = rawTimes.mapNotNull { raw ->
            runCatching { LocalDateTime.parse(raw).atZone(capeTownZone) }.getOrNull()
        }.firstOrNull { it.minusMinutes(minutesBefore).toInstant().isAfter(now) }

        if (next == null) {
            Log.d(TAG, "No future $reminderType alert found in forecast")
            return
        }

        val alertAt = next.minusMinutes(minutesBefore).toInstant()
        val delay = Duration.between(now, alertAt).toMillis()
        if (delay <= 0) return

        // The updated worker distinguishes sunrise and golden-hour notifications.
        val displayTime = if (reminderType == "golden_hour") {
            next.minusMinutes(60).format(timeFormat)
        } else {
            next.format(timeFormat)
        }
        val data = Data.Builder()
            .putString("reminder_type", reminderType)
            .putString("event_time", displayTime)
            .putString("golden_hour_time", displayTime)
            .build()

        val request = OneTimeWorkRequestBuilder<GoldenHourReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueName,
            ExistingWorkPolicy.REPLACE,
            request
        )
        Log.d(TAG, "Scheduled Cape Town $reminderType for $alertAt")
    }
}
