package com.shaigerbi.musicplayer.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simplemobiletools.commons.extensions.getFormattedDuration
import com.shaigerbi.musicplayer.R
import com.shaigerbi.musicplayer.adapters.AlbumsTracksAdapter
import com.shaigerbi.musicplayer.extensions.getAlbumTracksSync
import com.shaigerbi.musicplayer.extensions.getAlbums
import com.shaigerbi.musicplayer.extensions.resetQueueItems
import com.shaigerbi.musicplayer.helpers.ALBUM
import com.shaigerbi.musicplayer.helpers.ARTIST
import com.shaigerbi.musicplayer.helpers.RESTART_PLAYER
import com.shaigerbi.musicplayer.helpers.TRACK
import com.shaigerbi.musicplayer.models.*
import com.shaigerbi.musicplayer.services.MusicService
import kotlinx.android.synthetic.main.activity_albums.*
import kotlinx.android.synthetic.main.view_current_track_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

// Artists -> Albums -> Tracks
class AlbumsActivity : SimpleActivity() {
    private var bus: EventBus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_albums)

        bus = EventBus.getDefault()
        bus!!.register(this)

        val artistType = object : TypeToken<Artist>() {}.type
        val artist = Gson().fromJson<Artist>(intent.getStringExtra(ARTIST), artistType)
        title = artist.title

        getAlbums(artist) { albums ->
            val listItems = ArrayList<ListItem>()
            val albumsSectionLabel = resources.getQuantityString(R.plurals.albums_plural, albums.size, albums.size)
            listItems.add(AlbumSection(albumsSectionLabel))
            listItems.addAll(albums)

            var trackFullDuration = 0
            val tracksToAdd = ArrayList<Track>()
            albums.forEach {
                val tracks = getAlbumTracksSync(it.id)
                tracks.sortWith(compareBy({ it.trackId }, { it.title.toLowerCase() }))
                trackFullDuration += tracks.sumBy { it.duration }
                tracksToAdd.addAll(tracks)
            }

            var tracksSectionLabel = resources.getQuantityString(R.plurals.tracks_plural, tracksToAdd.size, tracksToAdd.size)
            tracksSectionLabel += " • ${trackFullDuration.getFormattedDuration(true)}"
            listItems.add(AlbumSection(tracksSectionLabel))
            listItems.addAll(tracksToAdd)

            runOnUiThread {
                val adapter = AlbumsTracksAdapter(this, listItems, albums_list, albums_fastscroller) {
                    if (it is Album) {
                        Intent(this, TracksActivity::class.java).apply {
                            putExtra(ALBUM, Gson().toJson(it))
                            startActivity(this)
                        }
                    } else {
                        resetQueueItems(tracksToAdd) {
                            Intent(this, TrackActivity::class.java).apply {
                                putExtra(TRACK, Gson().toJson(it))
                                putExtra(RESTART_PLAYER, true)
                                startActivity(this)
                            }
                        }
                    }
                }.apply {
                    albums_list.adapter = this
                }

                albums_fastscroller.setViews(albums_list) {
                    val item = adapter.items.getOrNull(it)
                    if (item is Track) {
                        albums_fastscroller.updateBubbleText(item.title)
                    } else if (item is Album) {
                        albums_fastscroller.updateBubbleText(item.title)
                    }
                }
            }
        }

        current_track_bar.setOnClickListener {
            Intent(this, TrackActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateCurrentTrackBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        bus?.unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        updateMenuItemColors(menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateCurrentTrackBar() {
        current_track_bar.updateColors()
        current_track_bar.updateCurrentTrack(MusicService.mCurrTrack)
        current_track_bar.updateTrackState(MusicService.getIsPlaying())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun trackChangedEvent(event: Events.TrackChanged) {
        current_track_bar.updateCurrentTrack(event.track)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun trackStateChanged(event: Events.TrackStateChanged) {
        current_track_bar.updateTrackState(event.isPlaying)
    }
}
