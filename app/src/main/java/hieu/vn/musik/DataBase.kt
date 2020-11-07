package hieu.vn.musik

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hieu.vn.musik.entities.Song
import java.util.*

class DataBase(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    private var ourDatabase: SQLiteDatabase? = null

    private var musicQuery = ("CREATE TABLE " + TABLE_NAME + " ("
            + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SONG_ID + " TEXT, "
            + NAME_SONG + " TEXT, "
            + NAME_ARTIST + " TEXT, "
            + DURATION + " TEXT,"
            + PATH + " TEXT,"
            + IMAGE + " TEXT"
            + " )")

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(musicQuery)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun deleteData(id: String): Int? {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "Song_Id = ?", arrayOf(id))
    }

    fun insertData(
        id: String?,
        name: String?,
        artist: String?,
        duration: String?,
        path: String?,
        image: String?
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SONG_ID, id)
        contentValues.put(NAME_SONG, name)
        contentValues.put(NAME_ARTIST, artist)
        contentValues.put(DURATION, duration)
        contentValues.put(PATH, path)
        contentValues.put(IMAGE, image)
        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L
    }

    val songFromDb: List<Song>
        @SuppressLint("Recycle")
        get() {
            val listFromDb: MutableList<Song> = ArrayList<Song>()
            ourDatabase = this.readableDatabase
            val field = arrayOf(
                SONG_ID,
                NAME_SONG,
                NAME_ARTIST,
                DURATION,
                PATH,
                IMAGE
            )


            val cursor: Cursor = ourDatabase!!.query(TABLE_NAME, field, null, null, null, null, null)
            val iID = cursor.getColumnIndex(SONG_ID)
            val iName = cursor.getColumnIndex(NAME_SONG)
            val iArtist = cursor.getColumnIndex(NAME_ARTIST)
            val iDuration = cursor.getColumnIndex(DURATION)
            val iPath = cursor.getColumnIndex(PATH)
            val iImage = cursor.getColumnIndex(IMAGE)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = cursor.getString(iID)
                val name = cursor.getString(iName)
                val artist = cursor.getString(iArtist)
                val duration = cursor.getString(iDuration)
                val path = cursor.getString(iPath)
                val image = cursor.getString(iImage)
                listFromDb.add(Song(id, name, artist, duration, path, image))
                cursor.moveToNext()
            }
            return listFromDb
        }

    companion object {
        const val DATABASE_NAME = "Songs.db"
        const val TABLE_NAME = "music"
        const val ROW_ID = "Row_Id"
        const val SONG_ID = "Song_Id"
        const val NAME_SONG = "Name_Song"
        const val NAME_ARTIST = "Name_Artist"
        const val DURATION = "Duration"
        const val PATH = "Path"
        const val IMAGE = "Image"
    }
}
