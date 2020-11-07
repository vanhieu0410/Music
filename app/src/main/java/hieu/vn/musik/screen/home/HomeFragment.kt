package hieu.vn.musik.screen.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hieu.vn.musik.ISongController
import hieu.vn.musik.entities.Song
import hieu.vn.musik.entities.SongAdapter
import hieu.vn.musik.R
import hieu.vn.musik.screen.play.PlayFragment
import kotlinx.android.synthetic.main.fragment_home_screen.*
import java.util.ArrayList

class HomeFragment : Fragment(), HomeContract.Screen {
    
    private val presenter = HomePresenter(this)

    private var songAdapter: SongAdapter? = null
    private var songController: ISongController? = null
    private var songCurrent: Song? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        songController = context as ISongController
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listSong = arguments?.getParcelableArrayList<Song>("DATA")

        listSong?.let {
            songAdapter = SongAdapter()
                .setData(listSong)
                .setSongItemClick { song ->
                    if (song != null) {
                        (activity as ISongController?)?.createSong(song)
                        setDataSongBar(song)
                    }

                    songCurrent = song
                    startAnimationRotate()
                    songControllerView.visibility = View.VISIBLE
                    lineShadow.visibility = View.VISIBLE
                    changeToPlayFragment(listSong)
                }

            listSongView.layoutManager = LinearLayoutManager(context)
            listSongView.adapter = songAdapter
        }

        buttonPlayHome.setOnClickListener {
            songController?.playSong()
            songController?.getCurrentSong()?.let { it1 -> setDataSongBar(it1) }
            buttonPauseHome?.visibility = View.VISIBLE
            buttonPlayHome?.visibility = View.INVISIBLE
            startAnimationRotate()
        }

        buttonPauseHome.setOnClickListener {
            songController?.pauseSong()
            songController?.getCurrentSong()?.let { it1 -> setDataSongBar(it1) }
            buttonPauseHome?.visibility = View.INVISIBLE
            buttonPlayHome?.visibility = View.VISIBLE
            imageSongForController?.clearAnimation()
        }

        buttonPrevious.setOnClickListener {
            songController?.backSong()
            songController?.getCurrentSong()?.let { it1 -> setDataSongBar(it1) }
            setColorChange()
        }

        buttonNextHome.setOnClickListener {
            songController?.nextSong()
            songController?.getCurrentSong()?.let { it1 -> setDataSongBar(it1) }
            setColorChange()
        }

        songControllerView.setOnClickListener {
            changeToPlayFragment(listSong)
        }
    }

    private fun changeToPlayFragment(listSong: ArrayList<Song>?) {
        val playFragment = PlayFragment()
        val  bundle = Bundle()
        bundle.putParcelableArrayList("DATA", listSong)
        playFragment.arguments = bundle

        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            ?.add(R.id.fragmentContainer, playFragment, PlayFragment::class.java.toString())
            ?.addToBackStack(null)
            ?.commit()
    }

    fun setColorChange() {
        songAdapter?.currentPosition = songController?.getPositionSong()?:0
        songAdapter?.notifyDataSetChanged()
    }

    fun setDataSongBar(song: Song?) {
        buttonPauseHome?.visibility = View.VISIBLE
        buttonPlayHome?.visibility = View.INVISIBLE
        val songImage: Bitmap
        if (song?.image != null) {
            songImage = BitmapFactory.decodeFile(song.image)
            imageSongForController.setImageBitmap(songImage)
            imageSongForController.clipToOutline = true
        } else {
            imageSongForController.setImageResource(R.drawable.image_default)
            imageSongForController.clipToOutline = true
        }
        textNameSong?.text = song?.name
        textArtist?.text = song?.artist
    }

    fun startAnimationRotate() {
        val rotate : Animation = AnimationUtils.loadAnimation(context,R.anim.rotation)
        rotate.fillAfter = true
        imageSongForController?.startAnimation(rotate)
    }

    fun setImgPause() {
        buttonPauseHome?.visibility = View.VISIBLE
        buttonPlayHome?.visibility = View.INVISIBLE
    }

    fun setImgPlay() {
        buttonPlayHome?.visibility = View.VISIBLE
        buttonPauseHome?.visibility = View.INVISIBLE
    }
    
}