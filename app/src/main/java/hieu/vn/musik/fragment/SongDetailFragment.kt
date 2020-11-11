package hieu.vn.musik.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import hieu.vn.musik.ISongController
import hieu.vn.musik.entities.Song
import hieu.vn.musik.R

class SongDetailFragment : Fragment() {
    var imgSongDetail: ImageView? = null
    var iSongController: ISongController? = null
    var resourceImage = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        iSongController = context as ISongController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.song_detail_fragment, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        imgSongDetail = view.findViewById(R.id.imageSongDetail)
    }

    fun setImageDetail(song: Song) {
        val songImage: Bitmap
        if (song.image != null) {
            songImage = BitmapFactory.decodeFile(song.image)
            imgSongDetail?.setImageBitmap(songImage)
            imgSongDetail?.clipToOutline = true
        } else {
            imgSongDetail?.setImageResource(R.drawable.image_default)
            imgSongDetail?.clipToOutline = true
        }
        resourceImage = 0
    }

    fun startAnimationRotate() {
        val rotate =
            AnimationUtils.loadAnimation(context, R.anim.rotation)
        rotate.fillAfter = true
        imgSongDetail?.startAnimation(rotate)
    }

    override fun onResume() {
        Log.d("xxx", "songDetail onResume")
        iSongController?.getCurrentSong()?.let { setImageDetail(it) }
        startAnimationRotate()
        imgSongDetail?.setOnClickListener {
            val oa1 = ObjectAnimator.ofFloat(imgSongDetail, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(imgSongDetail, "scaleX", 0f, 1f)
            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateDecelerateInterpolator()
            oa1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (resourceImage == 0) {
                        resourceImage = 1
                        imgSongDetail?.setImageResource(R.drawable.png2)
                    } else {
                        iSongController?.getCurrentSong()?.let { it1 -> setImageDetail(it1) }
                    }
                    oa2.start()
                }
            })
            oa1.start()
        }
        super.onResume()
    }

    override fun onDestroyView() {
        Log.d("xxx", "songDetail onDestroyView")
        super.onDestroyView()
    }

    override fun onStart() {
        Log.d("xxx", "songDetail onStart")
        super.onStart()
    }

    override fun onStop() {
        Log.d("xxx", "songDetail onStop")
        super.onStop()
    }

    override fun onPause() {
        Log.d("xxx", "songDetail onPause")
        super.onPause()
    }



}