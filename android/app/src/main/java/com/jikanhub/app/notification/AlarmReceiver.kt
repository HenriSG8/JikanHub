package com.jikanhub.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.jikanhub.app.MainActivity
import com.jikanhub.app.R
import com.jikanhub.app.data.local.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var tokenManager: TokenManager

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_OFFSET = "extra_offset"
        private const val CHANNEL_ID = "jikanhub_reminders"
        private const val CHANNEL_NAME = "Task Reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: return
        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: return

        val notificationManager =
            context.getSystemService(NotificationManager::class.java)

        // Get saved sound URI
        val soundUriString = runBlocking { tokenManager.notificationSoundUri.first() }
        val soundUri = if (soundUriString != null) {
            android.net.Uri.parse(soundUriString)
        } else {
            android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
        }

        // Use a dynamic channel ID based on the sound to force updates on Android 8+
        val dynamicChannelId = "${CHANNEL_ID}_${soundUriString?.hashCode() ?: "default"}"

        // Create channel
        val channel = NotificationChannel(
            dynamicChannelId,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for your scheduled tasks"
            enableVibration(true)
            setSound(soundUri, android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
        }
        
        // Clean up old channels of this app if they exist (optional but cleaner)
        notificationManager.notificationChannels.forEach { existingChannel ->
            if (existingChannel.id.startsWith(CHANNEL_ID) && existingChannel.id != dynamicChannelId) {
                notificationManager.deleteNotificationChannel(existingChannel.id)
            }
        }
        
        notificationManager.createNotificationChannel(channel)

        // Create tap intent
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context, taskId.hashCode(), tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, dynamicChannelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(tapPendingIntent)
            .build()

        val notificationId = taskId.hashCode()
        notificationManager.notify(notificationId, notification)
    }
}

