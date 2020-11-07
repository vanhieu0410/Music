package hieu.vn.musik

import android.media.MediaPlayer
import hieu.vn.musik.entities.Song

interface ISongController {

    fun createSong(song: Song)

    fun playSong()

    fun pauseSong()

    fun nextSong()

    fun backSong()

    fun getCurrentSong(): Song? {
        return getCurrentSong()
    }

    fun getPositionSong(): Int {
        return getPositionSong()
    }

    fun getDuration() : Int {
        return getDuration()
    }

    fun getPlayerPosition() : Int {
        return getPlayerPosition()
    }

    fun seekTo(time: Int)

    fun getMediaPlayer(): MediaPlayer {
        return getMediaPlayer()
    }



    fun isPlaying(): Boolean{
        return isPlaying()
    }

    fun repeat()

    fun shuffle()

}