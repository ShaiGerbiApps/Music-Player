package com.shaigerbi.musicplayer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shaigerbi.musicplayer.services.MusicService

class NotificationDismissedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Intent(context, MusicService::class.java).apply {
            context.stopService(this)
        }
    }
}
