package hieu.vn.musik.screen

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import hieu.vn.musik.BaseUtils
import hieu.vn.musik.ISongController
import hieu.vn.musik.R
import hieu.vn.musik.entities.Song
import hieu.vn.musik.screen.home.HomeFragment
import hieu.vn.musik.screen.play.PlayFragment
import hieu.vn.musik.service.SongService

class MainActivity : AppCompatActivity(), ISongController, MainContract.Screen {

    companion object {
        private const val MY_PERMISSION_REQUEST : Int = 1
    }

    private val presenter = MainPresenter(this)
    var mSongService: SongService? = null
    var homeFragment: HomeFragment? = null
    var playFragment: PlayFragment? = null
    var myConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {}
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mSongService = (service as SongService.MyBinder).getService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(BaseUtils.ACTION_CLOSE_SONG)
        filter.addAction(BaseUtils.ACTION_NEXT_SONG)
        filter.addAction(BaseUtils.ACTION_BACK_SONG)
        filter.addAction(BaseUtils.ACTION_PLAY_SONG)
        filter.addAction(BaseUtils.ACTION_PAUSE_SONG)
        registerReceiver(broadcastReceiver, filter)
    }

    override fun startService(listSong: List<Song>) {
        Handler(Looper.getMainLooper()).post {
            val intentService = Intent(this@MainActivity, SongService::class.java)
            intentService.putParcelableArrayListExtra("LIST", listSong as ArrayList)
            startService(intentService)
            Log.d("xxx", " Size of list before send ${listSong.size}")
            bindService(Intent(this@MainActivity, SongService::class.java),
                    myConnection,
                    Context.BIND_AUTO_CREATE)
        }
    }



    override fun bindDataToView(listSong: List<Song>) {
        val homeFragment = HomeFragment()
        val bundle = Bundle()
        bundle.putParcelableArrayList("DATA", listSong as ArrayList)
        homeFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, homeFragment, HomeFragment::class.java.toString())
            .addToBackStack(null)
            .commit()
    }




    override fun getCurrentSong(): Song? {
        return mSongService?.mCurrentSong
    }

    override fun isPlaying(): Boolean {
        return mSongService?.isPlaying() as Boolean
    }

    override fun createSong(song: Song) {
        mSongService?.createSong(song)
    }

    override fun playSong() {
        mSongService?.playSong()
    }

    override fun pauseSong() {
        mSongService?.pauseSong()
    }


    override fun nextSong() {
        mSongService?.nextSong()
    }

    override fun backSong() {
        mSongService?.backSong()
    }

    override fun getDuration(): Int {
        return mSongService?.getDuration() as Int
    }

    override fun getPositionSong(): Int {
        return mSongService?.getPositionSong() as Int
    }

    override fun getMediaPlayer(): MediaPlayer {
        return mSongService?.getMediaPlayer() as MediaPlayer
    }

    override fun getPlayerPosition(): Int {
        return mSongService?.getPlayerPosition() as Int
    }

    override fun seekTo(time: Int) {
        mSongService?.seekTo(time)
    }

    override fun repeat() {


    }

    override fun shuffle() {

    }



    fun synStatePlayButton() {
        homeFragment = supportFragmentManager.findFragmentByTag(HomeFragment::class.java.toString()) as HomeFragment?
        getCurrentSong()?.let { homeFragment?.setDataSongBar(it) }
        homeFragment?.setColorChange()
        if (isPlaying()) {
            homeFragment?.setImgPause()
        }
        homeFragment?.setImgPlay()
    }

    fun syncStateListSong() {
        homeFragment = supportFragmentManager.findFragmentByTag(HomeFragment::class.java.toString()) as HomeFragment?
        getCurrentSong()?.let { homeFragment?.setDataSongBar(it) }
        homeFragment?.setColorChange()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        }
        super.onBackPressed()
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@MainActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    )
            ) {
                ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSION_REQUEST
                )
            } else {
                ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSION_REQUEST
                )
            }
        } else {
            presenter.initDataSong(contentResolver)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
                        presenter.initDataSong(contentResolver)
                    }
                } else {
                    Toast.makeText(this, "No Permission granted! ", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return
            }
        }
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            homeFragment = supportFragmentManager.findFragmentByTag(HomeFragment::class.java.toString()) as HomeFragment?
            playFragment = supportFragmentManager.findFragmentByTag(PlayFragment::class.java.toString()) as PlayFragment?
            when (intent.action) {
                BaseUtils.ACTION_CLOSE_SONG -> {
                    unbindService(myConnection)
                }
                BaseUtils.ACTION_BACK_SONG -> {
                    if (homeFragment != null) {
                        homeFragment?.setDataSongBar(getCurrentSong())
                    }
                    if (playFragment != null) {
                        playFragment?.setDataToolBar(getCurrentSong())
                        playFragment?.setImgDetail(getCurrentSong())
                        playFragment?.setTimeToTal()
                    }
                    homeFragment?.setColorChange()
                    Log.d("xxx", "current song in controller: ${mSongService?.getCurrentSong()?.name}")
                }
                BaseUtils.ACTION_NEXT_SONG -> {
                    Log.d("xxx", "1")
                    if (playFragment != null) {
                        playFragment?.setDataToolBar(getCurrentSong())
                        playFragment?.setImgDetail(getCurrentSong())
                        playFragment?.setTimeToTal()
                    }
                    homeFragment?.setDataSongBar(getCurrentSong())
                    homeFragment?.setColorChange()
                    Log.d("xxx", "2")
                }
                BaseUtils.ACTION_PAUSE_SONG -> {
                    if (playFragment != null) {
                        playFragment?.setDataToolBar(getCurrentSong())
                        playFragment?.setBtnPlay()
                    }
                    homeFragment?.setImgPlay()
                    getCurrentSong()?.let { homeFragment?.setDataSongBar(it) }

                }
                BaseUtils.ACTION_PLAY_SONG -> {
                    if (playFragment != null) {
                        playFragment?.setDataToolBar(getCurrentSong())
                        playFragment?.setBtnPause()
                    }
                    homeFragment?.setImgPause()
                    getCurrentSong()?.let { homeFragment?.setDataSongBar(it) }

                }
            }
        }
    }

}




