package hieu.vn.musik.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import hieu.vn.musik.R

class SplashActivity : AppCompatActivity() {

    var img : ImageView ?= null
    var txt1 : TextView ?= null
    var txt2 : TextView ?= null
    var txt3 : TextView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        img = findViewById(R.id.img1)
        txt1 = findViewById(R.id.title1)
        txt2 = findViewById(R.id.title2)
        txt3 = findViewById(R.id.title3)

        img?.animate()?.translationY((-1600).toFloat())?.setDuration(1000)?.setStartDelay(2500)
        txt1?.animate()?.translationY((1400).toFloat())?.setDuration(1000)?.setStartDelay(2500)
        txt2?.animate()?.translationY((1400).toFloat())?.setDuration(1000)?.setStartDelay(2500)
        txt3?.animate()?.translationY((1400).toFloat())?.setDuration(1000)?.setStartDelay(2500)

        Handler().postDelayed({
            val intent = Intent(this@SplashActivity,
                MainActivity::class.java)
            startActivity(intent)
        },4000)
    }
}