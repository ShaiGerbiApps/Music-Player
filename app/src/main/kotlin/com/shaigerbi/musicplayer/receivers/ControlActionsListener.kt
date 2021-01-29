package com.shaigerbi.musicplayer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shaigerbi.musicplayer.extensions.sendIntent
import com.shaigerbi.musicplayer.helpers.FINISH
import com.shaigerbi.musicplayer.helpers.NEXT
import com.shaigerbi.musicplayer.helpers.PLAYPAUSE
import com.shaigerbi.musicplayer.helpers.PREVIOUS

class ControlActionsListener : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            PREVIOUS, PLAYPAUSE, NEXT, FINISH -> context.sendIntent(action)
        }
    }
}
