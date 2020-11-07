package hieu.vn.musik.screen

import android.content.ContentResolver
import hieu.vn.musik.entities.Song


interface MainContract {

    interface Screen {
        fun bindDataToView(listSong: List<Song>)

        fun startService(listSong: List<Song>)

    }

    abstract class Presenter(screen: Screen) {

        abstract fun initDataSong(contentResolver: ContentResolver)


    }
}