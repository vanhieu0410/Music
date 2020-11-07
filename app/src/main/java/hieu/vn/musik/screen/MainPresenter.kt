package hieu.vn.musik.screen

import android.content.ContentResolver
import android.provider.MediaStore
import android.util.Log
import hieu.vn.musik.DataProtocols
import hieu.vn.musik.entities.ImageSong
import hieu.vn.musik.entities.InfoSong
import hieu.vn.musik.entities.Song


class MainPresenter(val screen: MainContract.Screen): MainContract.Presenter(screen), DataProtocols {

    private val listSong = ArrayList<Song>()
    private var lisInfo: MutableList<InfoSong>? = null
    private var listImage: MutableList<ImageSong>? = null

    override fun initDataSong(contentResolver: ContentResolver) {
        val threadGetDataSong = Thread(Runnable {
            lisInfo = mutableListOf()
            var infoSong: InfoSong?
            val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val songCursor = contentResolver.query(
                songUri,
                null,
                null,
                null,
                null
            )
            if (songCursor != null && songCursor.moveToFirst()) {
                val songId = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val songDuration =
                    songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                val songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                do {
                    val currentID = songCursor.getString(songId)
                    Log.i("aaa", " $currentID")
                    val currentName = songCursor.getString(songTitle)
                    Log.i("bbb", " $currentName")
                    val currentArtist = songCursor.getString(songArtist)
                    val currentDuration = songCursor.getString(songDuration)
                    val currentPath = songCursor.getString(songPath)
                    Log.i("ccc", " $currentPath")
                    infoSong = InfoSong(
                        currentID,
                        currentName,
                        currentArtist,
                        currentDuration,
                        currentPath
                    )
                    lisInfo?.add(infoSong)
                } while (songCursor.moveToNext())
                songCursor.close()
            }
            //List image
            listImage = mutableListOf()
            var imageSong: ImageSong?
            val imageUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
            val imageCursor = contentResolver.query(
                imageUri,
                null,
                null,
                null,
                null
            )
            if (imageCursor != null && imageCursor.moveToFirst()) {
                val songIdAlbum = imageCursor.getColumnIndex(MediaStore.Audio.Albums._ID)
                val songImagePath =
                    imageCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)
                do {
                    val currentId = imageCursor.getString(songIdAlbum)
                    Log.i("ID", "" + currentId)
                    val currentImage = imageCursor.getString(songImagePath)
                    imageSong = ImageSong(currentId, currentImage)
                    listImage?.add(imageSong)
                } while (imageCursor.moveToNext())
            }
            imageCursor?.close()

            // So sanh ad cua 2  list neu trung nhau thi gan path image vao, co nhung bai hat khong co image path
            // Sau do gop 2 list lai thanh listsong

            val sizeListImage: Int = listImage!!.size
            var pathImageSong: String? = null
            var song: Song? = null
            var a: Int
            for (infoSong in lisInfo!!) {
                a = 0
                while (a < sizeListImage) {
                    if (infoSong.mAlbumID == listImage!![a].mImageAlbumID) {
                        Log.i("EEE", "" + listImage?.get(0)?.mImageAlbumID)
                        pathImageSong = listImage?.get(a)?.mPathImage
                        break
                    }
                    a++
                }
                song = Song(
                    infoSong.mAlbumID,
                    infoSong.mTitle,
                    infoSong.mArtist,
                    infoSong.mDuration,
                    infoSong.mPath,
                    pathImageSong
                )
                listSong.add(song)
            }
            screen.startService(listSong)
            screen.bindDataToView(listSong)
        })
        threadGetDataSong.start()
    }

}