package hieu.vn.musik.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import hieu.vn.musik.BaseUtils
import hieu.vn.musik.ISongController
import hieu.vn.musik.R
import hieu.vn.musik.entities.Song
import hieu.vn.musik.screen.home.HomeFragment
import java.io.IOException

class SongService : Service(), ISongController {

    var notificationManager: NotificationManager? = null
    private var listMusic: ArrayList<Song>? = null
    var currentSongPosition = -1
    var mediaSessionCompat: MediaSessionCompat? = null
    var newSong: Song? = null
    var musicPlayer: MediaPlayer? = null
    var mCurrentSong: Song? = null
    private val mBinder: IBinder = MyBinder()

    inner class MyBinder : Binder() {
        fun getService(): SongService {
            return this@SongService
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        musicPlayer = MediaPlayer()
        musicPlayer?.setOnCompletionListener { nextSong() }
        mediaSessionCompat = MediaSessionCompat(baseContext, "tag")
        Log.d("XXX", "onCreate")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (listMusic?.size == null) {
            listMusic = intent?.getParcelableArrayListExtra("LIST")
        }
        Log.d("xxx", "Size of list From service  ${listMusic?.size}")
        if (intent != null) {
            var actionFromBroadcast: String? = ""
            actionFromBroadcast = intent.getStringExtra("key_action")
            if (actionFromBroadcast != null) {
                when (actionFromBroadcast) {
                    BaseUtils.ACTION_BACK_SONG -> {
                        backSong()
                    }
                    BaseUtils.ACTION_NEXT_SONG -> {
                        nextSong()
                    }
                    BaseUtils.ACTION_PLAY_SONG -> {
                        playSong()
                    }
                    BaseUtils.ACTION_PAUSE_SONG -> {
                        pauseSong()
                    }
                    BaseUtils.ACTION_CLOSE_SONG -> {
                        stopSelf()
                    }
                }
            }
        }
        Log.d("XXX", "startCommand")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer?.stop()
        musicPlayer?.release()
        Log.d("XXX", "onDestroy")
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "X"
            val description = "XXX"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                    NotificationChannel(BaseUtils.CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager?.createNotificationChannel(channel)
            channel.setSound(null, null)
        }
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {

        return mBinder
    }

    override fun createSong(song: Song) {

        mCurrentSong = song

        for (i: Int in 0 until (listMusic?.size ?:0) ) {
            if (song.path == listMusic?.get(i)?.path ) {
                currentSongPosition = i
            }
        }
        Log.d("xxx", "currentPosition of item click: $currentSongPosition")
        musicPlayer?.reset()
        try {
            musicPlayer?.setDataSource(song.path)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            musicPlayer?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        musicPlayer?.start()
        createNotificationChannel()

        val noti = notiRun()
        notificationManager?.notify(69, noti)
        startForeground(69, noti)
    }

    override fun playSong() {
        Log.d("xxx", "SongService play song: " + mCurrentSong?.name)
        val noti = notiRun()
        musicPlayer?.start()
        notificationManager?.notify(69, noti)
    }

    override fun isPlaying(): Boolean {
        return musicPlayer?.isPlaying as Boolean
    }

    override fun pauseSong() {
        Log.d("xxx", "SongService pause song")
        musicPlayer?.pause()
        val noti = notiPause()
        notificationManager?.notify(69, noti)
    }



    override fun getPositionSong(): Int {
        return currentSongPosition
    }

    override fun nextSong() {
        if (currentSongPosition == (listMusic?.size ?: 0) - 1) {
            currentSongPosition = -1
        }
        currentSongPosition++
        newSong = listMusic?.get(currentSongPosition)
        mCurrentSong = newSong
        Log.d("xxx", " new song:  $newSong")
        newSong?.let { createSong(it) }
    }

    override fun backSong() {
        Log.d("xxx", "SongService backSong")
        if (currentSongPosition == 0) {
            currentSongPosition = (listMusic?.size ?: 0)
        }
        currentSongPosition--
        newSong = listMusic?.get(currentSongPosition)
        mCurrentSong = newSong
        newSong?.let { createSong(it)}
    }

    override fun getMediaPlayer(): MediaPlayer {
        return musicPlayer as MediaPlayer
    }

    override fun getDuration(): Int {
        return musicPlayer?.duration as Int
    }

    override fun getPlayerPosition(): Int {
        return musicPlayer?.currentPosition as Int
    }



    override fun getCurrentSong(): Song {
        return mCurrentSong as Song
    }

    override fun seekTo(time: Int) {
        musicPlayer?.seekTo(time)
    }

    override fun repeat() {
    }

    override fun shuffle() {
    }


    private fun notiRun(): Notification? {
        val image: Bitmap = if (mCurrentSong?.image != null) {
            BitmapFactory.decodeFile(mCurrentSong!!.image)
        } else {
            BitmapFactory.decodeResource(
                    baseContext.resources,
                    R.drawable.image_default
            )
        }
        val pendingIntentBack: PendingIntent
        val intentBack = Intent(baseContext, SongReceiver::class.java)
                .setAction(BaseUtils.ACTION_BACK_SONG)
        pendingIntentBack = PendingIntent.getBroadcast(
                baseContext, 0, intentBack, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentPause: PendingIntent
        val intentPause = Intent(baseContext, SongReceiver::class.java)
                .setAction(BaseUtils.ACTION_PAUSE_SONG)
        pendingIntentPause = PendingIntent.getBroadcast(
                baseContext, 0, intentPause, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentNext: PendingIntent
        val intentNext = Intent(baseContext, SongReceiver::class.java)
                .setAction(BaseUtils.ACTION_NEXT_SONG)
        pendingIntentNext = PendingIntent.getBroadcast(
                baseContext, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentClose: PendingIntent
        val intentClose = Intent(baseContext, SongReceiver::class.java)
                .setAction(BaseUtils.ACTION_CLOSE_SONG)
        pendingIntentClose = PendingIntent.getBroadcast(
                baseContext, 0, intentClose, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
                NotificationCompat.Builder(this, BaseUtils.CHANNEL_ID)
                        .setSmallIcon(R.drawable.image_default)
                        .setContentTitle(mCurrentSong?.name)
                        .setContentText(mCurrentSong?.artist)
                        .setSmallIcon(R.drawable.ic_music)
                        .setLargeIcon(image)
                        .setOnlyAlertOnce(true)
                        .setShowWhen(false)
                        .setDefaults(0)
                        .addAction(R.drawable.ic_back, BaseUtils.ACTION_BACK_SONG, pendingIntentBack)
                        .addAction(R.drawable.ic_pause, BaseUtils.ACTION_PAUSE_SONG, pendingIntentPause)
                        .addAction(R.drawable.ic_next, BaseUtils.ACTION_NEXT_SONG, pendingIntentNext)
                        .addAction(R.drawable.ic_bclose, BaseUtils.ACTION_NEXT_SONG, pendingIntentClose)
                        .setStyle(
                                androidx.media.app.NotificationCompat.MediaStyle()
                                        .setShowActionsInCompactView(0, 1, 2)
                                        .setMediaSession(mediaSessionCompat?.sessionToken)
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the intent that will fire when the user taps the notification
                        .setAutoCancel(true)
        return builder.build()
    }

    private fun notiPause(): Notification? {
        val image: Bitmap = if (mCurrentSong?.image != null) {
            BitmapFactory.decodeFile(mCurrentSong?.image)
        } else {
            BitmapFactory.decodeResource(
                    baseContext.resources,
                    R.drawable.image_default
            )
        }

        val notificationIntent: Intent = Intent(this, HomeFragment::class.java)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val intent: PendingIntent = PendingIntent.getActivity(baseContext,0,notificationIntent,0)



        val pendingIntentBack: PendingIntent
        val intentBack = Intent(baseContext, SongReceiver::class.java)
                .setAction(BaseUtils.ACTION_BACK_SONG)
        pendingIntentBack = PendingIntent.getBroadcast(
                baseContext, 0, intentBack, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentPlay: PendingIntent
        val intentPlay = Intent(baseContext, SongReceiver::class.java)
                .setAction(BaseUtils.ACTION_PLAY_SONG)
        pendingIntentPlay = PendingIntent.getBroadcast(
                baseContext, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentNext: PendingIntent
        val intentNext = Intent(baseContext, SongReceiver::class.java)
                .setAction(BaseUtils.ACTION_NEXT_SONG)
        pendingIntentNext = PendingIntent.getBroadcast(
                baseContext, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentClose: PendingIntent
        val intentClose = Intent(baseContext, SongReceiver::class.java)
                .setAction(BaseUtils.ACTION_CLOSE_SONG)
        pendingIntentClose = PendingIntent.getBroadcast(
                baseContext, 0, intentClose, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
                NotificationCompat.Builder(this, BaseUtils.CHANNEL_ID)
                        .apply { setContentIntent(intent) }
                        .setSmallIcon(R.drawable.image_default)
                        .setContentTitle(mCurrentSong?.name)
                        .setContentText(mCurrentSong?.artist)
                        .setSmallIcon(R.drawable.ic_music)
                        .setLargeIcon(image)
                        .setOnlyAlertOnce(true)
                        .setShowWhen(false)
                        .setDefaults(0)
                        .addAction(R.drawable.ic_back, BaseUtils.ACTION_BACK_SONG, pendingIntentBack)
                        .addAction(R.drawable.ic_play, BaseUtils.ACTION_PLAY_SONG, pendingIntentPlay)
                        .addAction(R.drawable.ic_next, BaseUtils.ACTION_NEXT_SONG, pendingIntentNext)
                        .addAction(R.drawable.ic_bclose, BaseUtils.ACTION_NEXT_SONG, pendingIntentClose)
                        .setStyle(
                                androidx.media.app.NotificationCompat.MediaStyle()
                                        .setShowActionsInCompactView(0, 1, 2)
                                        .setMediaSession(mediaSessionCompat?.sessionToken)
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the intent that will fire when the user taps the notification
                        .setAutoCancel(true)
        return builder.build()
    }


}