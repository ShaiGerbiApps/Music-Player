package com.shaigerbi.musicplayer.fragments

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import com.google.gson.Gson
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.shaigerbi.musicplayer.activities.SimpleActivity
import com.shaigerbi.musicplayer.activities.TracksActivity
import com.shaigerbi.musicplayer.adapters.AlbumsAdapter
import com.shaigerbi.musicplayer.dialogs.ChangeSortingDialog
import com.shaigerbi.musicplayer.extensions.config
import com.shaigerbi.musicplayer.extensions.getAlbumsSync
import com.shaigerbi.musicplayer.extensions.getArtistsSync
import com.shaigerbi.musicplayer.helpers.ALBUM
import com.shaigerbi.musicplayer.helpers.TAB_ALBUMS
import com.shaigerbi.musicplayer.models.Album
import kotlinx.android.synthetic.main.fragment_albums.view.*

// Artists -> Albums -> Tracks
class AlbumsFragment(context: Context, attributeSet: AttributeSet) : MyViewPagerFragment(context, attributeSet) {
    private var albumsIgnoringSearch = ArrayList<Album>()

    override fun setupFragment(activity: SimpleActivity) {
        ensureBackgroundThread {
            val albums = ArrayList<Album>()

            val artists = activity.getArtistsSync()
            artists.forEach { artist ->
                albums.addAll(activity.getAlbumsSync(artist))
            }

            Album.sorting = activity.config.albumSorting
            albums.sort()

            activity.runOnUiThread {
                albums_placeholder.beVisibleIf(albums.isEmpty())
                val adapter = AlbumsAdapter(activity, albums, albums_list, albums_fastscroller) {
                    Intent(activity, TracksActivity::class.java).apply {
                        putExtra(ALBUM, Gson().toJson(it))
                        activity.startActivity(this)
                    }
                }.apply {
                    albums_list.adapter = this
                }

                albums_fastscroller.setViews(albums_list) {
                    val album = adapter.albums.getOrNull(it)
                    albums_fastscroller.updateBubbleText(album?.getBubbleText() ?: "")
                }
            }
        }

        albums_fastscroller.updatePrimaryColor()
        albums_fastscroller.updateBubbleColors()
    }

    override fun finishActMode() {
        (albums_list.adapter as? MyRecyclerViewAdapter)?.finishActMode()
    }

    override fun onSearchQueryChanged(text: String) {
        val filtered = albumsIgnoringSearch.filter { it.title.contains(text, true) }.toMutableList() as ArrayList<Album>
        (albums_list.adapter as? AlbumsAdapter)?.updateItems(filtered, text)
        albums_placeholder.beVisibleIf(filtered.isEmpty())
    }

    override fun onSearchOpened() {
        albumsIgnoringSearch = (albums_list?.adapter as? AlbumsAdapter)?.albums ?: ArrayList()
    }

    override fun onSearchClosed() {
        (albums_list.adapter as? AlbumsAdapter)?.updateItems(albumsIgnoringSearch)
        albums_placeholder.beGoneIf(albumsIgnoringSearch.isNotEmpty())
    }

    override fun onSortOpen(activity: SimpleActivity) {
        ChangeSortingDialog(activity, TAB_ALBUMS) {
            val adapter = albums_list.adapter as? AlbumsAdapter ?: return@ChangeSortingDialog
            val albums = adapter.albums
            Album.sorting = activity.config.albumSorting
            albums.sort()
            adapter.updateItems(albums, forceUpdate = true)
        }
    }
}
