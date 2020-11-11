package hieu.vn.musik.screen.play

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import hieu.vn.musik.DataBase
import hieu.vn.musik.ISongController
import hieu.vn.musik.screen.MainActivity
import hieu.vn.musik.entities.Song
import hieu.vn.musik.R
import hieu.vn.musik.fragment.ListSongFragment
import hieu.vn.musik.fragment.LyricFragment
import hieu.vn.musik.fragment.PagerAdapter
import hieu.vn.musik.fragment.SongDetailFragment
import hieu.vn.musik.screen.home.HomeFragment
import kotlinx.android.synthetic.main.lf_item_song.*
import kotlinx.android.synthetic.main.play_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlayFragment : Fragment(), PlayContract.Screen {

    private val presenter = PlayPresenter(this)

    var songController: ISongController? = null
    private var mAdapter: PagerAdapter? = null
    private var currentSong: Song? = null
    private var songDetailFragment: SongDetailFragment? = null
    private var listSongFragment: ListSongFragment? = null
    private var mDataBase: DataBase? = null
    var homeFragment: HomeFragment? = fragmentManager?.findFragmentByTag(HomeFragment::class.java.toString()) as HomeFragment?
    override fun onAttach(context: Context) {
        super.onAttach(context)
        songController = context as ISongController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.play_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mDataBase = DataBase(context)
        mAdapter = PagerAdapter(this)
        mAdapter?.addFragment(ListSongFragment())
        mAdapter?.addFragment(SongDetailFragment())
        mAdapter?.addFragment(LyricFragment())

        viewPager.adapter = mAdapter
        circleIndicator.setViewPager(viewPager)
        circleIndicator.adapterDataObserver?.let { mAdapter?.registerAdapterDataObserver(it) }
        songDetailFragment = mAdapter?.getFragment(1) as SongDetailFragment
        listSongFragment = mAdapter?.getFragment(0) as ListSongFragment
        viewPager.post {
            viewPager.currentItem = 1
        }

        currentSong = songController?.getCurrentSong()
        setDataToolBar(currentSong)
        setTimeToTal()
        updateTimeSong()

        listSongFragment?.listSongLike = mDataBase?.songFromDb as ArrayList<Song>
        buttonPlay.setOnClickListener {
            songController?.playSong()
            setDataToolBar(songController?.getCurrentSong())
            setTimeToTal()
            (activity as MainActivity?)?.synStatePlayButton()
            songDetailFragment?.startAnimationRotate()
            setImgDetail(songController?.getCurrentSong())
            buttonPause.visibility = View.VISIBLE
            buttonPlay.visibility = View.INVISIBLE
        }

        buttonPause.setOnClickListener {
            songController?.pauseSong()
            setDataToolBar(songController?.getCurrentSong())
            setTimeToTal()
            songDetailFragment?.imgSongDetail?.clearAnimation()
            (activity as MainActivity?)?.synStatePlayButton()
            setImgDetail(songController?.getCurrentSong())
            buttonPlay.visibility = View.VISIBLE
            buttonPause.visibility = View.INVISIBLE
        }

        buttonNext.setOnClickListener {
            songController?.nextSong()
            setDataToolBar(songController?.getCurrentSong())
            (activity as MainActivity?)?.syncStateListSong()
            setTimeToTal()
            setImgDetail(songController?.getCurrentSong())
            if (songController?.isPlaying() as Boolean) {
                buttonPlay.visibility = View.INVISIBLE
                buttonPause.visibility = View.VISIBLE
            }
        }

        buttonPre.setOnClickListener {
            songController?.backSong()
            setDataToolBar(songController?.getCurrentSong())
            setTimeToTal()
            (activity as MainActivity?)?.syncStateListSong()
            setImgDetail(songController?.getCurrentSong())
            if (songController?.isPlaying() as Boolean) {
                buttonPlay.visibility = View.INVISIBLE
                buttonPause.visibility = View.VISIBLE
            }
        }

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }

        frame_play.setOnClickListener {

        }

        layoutPlayer.setOnClickListener{

        }

        toolBar.setOnClickListener {

        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                songController?.seekTo(seekBar.progress)
            }
        })

        imgLike.setOnClickListener {
            songController?.getCurrentSong()?.let { it1 -> listSongFragment?.listSongLike?.add(it1) }
            listSongFragment?.listFavourAdapter?.notifyDataSetChanged()
            mDataBase?.insertData(
                songController?.getCurrentSong()?.id,
                songController?.getCurrentSong()?.name,
                songController?.getCurrentSong()?.artist,
                songController?.getCurrentSong()?.duration,
                songController?.getCurrentSong()?.path,
                songController?.getCurrentSong()?.image
            )
        }
    }

    fun setBtnPlay() {
        buttonPlay.visibility = View.VISIBLE
        buttonPause.visibility = View.INVISIBLE
    }

    fun setBtnPause() {
        buttonPause.visibility = View.VISIBLE
        buttonPlay.visibility = View.INVISIBLE
    }

    fun setDataToolBar(song: Song?) {
        txt_artistBar.text = song?.artist
        txt_songBar.text = song?.name
    }

    fun setTimeToTal() {
        val timeFormat = SimpleDateFormat("mm:ss", Locale.US)
        val timeDuration: Int = songController?.getDuration()!!
        timeTotal.text = timeFormat.format(timeDuration)
        seekBar?.max = timeDuration
    }

    fun updateTimeSong() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val timeFormat = SimpleDateFormat("mm:ss", Locale.US)
                try {
                    val timeCurrentPos: Int? = songController?.getPlayerPosition()
                    timeCurrent?.text = timeFormat.format(timeCurrentPos)
                    //Update seekBar
                    seekBar?.progress = timeCurrentPos as Int
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                handler.postDelayed(this, 500)
                songController?.getMediaPlayer()
                    ?.setOnCompletionListener {
                        songController?.nextSong()
                        setTimeToTal()
                        setDataToolBar(songController?.getCurrentSong())
                        updateTimeSong()
                        homeFragment?.setColorChange()
                        homeFragment?.setDataSongBar(songController?.getCurrentSong())
                        setImgDetail(songController?.getCurrentSong())
                    }
            }
        }, 100)
    }

    fun setImgDetail(song: Song?) {
        song?.let { songDetailFragment?.setImageDetail(it) }
    }

}