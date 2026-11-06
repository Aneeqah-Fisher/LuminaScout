package com.example.luminascout

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class GoldenHourReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val type = inputData.getString("reminder_type") ?: "golden_hour"
        val time = inputData.getString("event_time")
            ?: inputData.getString("golden_hour_time") ?: "soon"
        showNotification(type, time)
        return Result.success()
    }

    private fun showNotification(type: String, eventTime: String) {
        val context = applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        val isSunrise = type == "sunrise"
        val channelId = "golden_hour_reminders"
        val manager = context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Photography Light Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Sunrise and golden hour photography alerts" }
            )
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isSunrise) "Sunrise Photography Reminder!" else "Golden Hour Approaching!"
        val message = if (isSunrise) {
            "Sunrise is at $eventTime. Get your camera ready for morning light!"
        } else {
            "Golden hour starts at $eventTime. Get your camera ready for beautiful light!"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(if (isSunrise) 1002 else 1001, notification)
    }
}
