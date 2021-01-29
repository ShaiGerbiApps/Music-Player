package com.shaigerbi.musicplayer.extensions

import android.app.Activity
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.shaigerbi.musicplayer.dialogs.SelectPlaylistDialog
import com.shaigerbi.musicplayer.helpers.RoomHelper
import com.shaigerbi.musicplayer.models.Track
import com.shaigerbi.musicplayer.services.MusicService

fun Activity.addTracksToPlaylist(tracks: List<Track>, callback: () -> Unit) {
    SelectPlaylistDialog(this) { playlistId ->
        val tracksToAdd = ArrayList<Track>()
        tracks.forEach {
            it.id = 0
            it.playListId = playlistId
            tracksToAdd.add(it)
        }

        ensureBackgroundThread {
            RoomHelper(this).insertTracksWithPlaylist(tracksToAdd)

            runOnUiThread {
                callback()
            }
        }
    }
}

fun Activity.addTracksToQueue(tracks: List<Track>, callback: () -> Unit) {
    addQueueItems(tracks) {
        tracks.forEach { track ->
            if (MusicService.mTracks.none { it.mediaStoreId == track.mediaStoreId }) {
                MusicService.mTracks.add(track)
            }
        }

        runOnUiThread {
            callback()
        }
    }
}
