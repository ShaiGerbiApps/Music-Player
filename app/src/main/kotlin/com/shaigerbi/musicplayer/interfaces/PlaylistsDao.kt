package com.shaigerbi.musicplayer.interfaces

import androidx.room.*
import com.shaigerbi.musicplayer.models.Playlist

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlist: Playlist): Long

    @Delete
    fun deletePlaylists(playlists: List<Playlist?>)

    @Query("SELECT * FROM playlists")
    fun getAll(): List<Playlist>

    @Query("SELECT * FROM playlists WHERE title = :title COLLATE NOCASE")
    fun getPlaylistWithTitle(title: String): Playlist?

    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getPlaylistWithId(id: Int): Playlist?

    @Update
    fun update(playlist: Playlist)
}
