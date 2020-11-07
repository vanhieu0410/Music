package hieu.vn.musik.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import hieu.vn.musik.BaseUtils

class SongReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("xxx", "" + intent?.action)
        val action = intent?.action


        when (action) {
            BaseUtils.ACTION_BACK_SONG -> {
                val intentBack = Intent(context, SongService::class.java)
                intentBack.putExtra("key_action", BaseUtils.ACTION_BACK_SONG)
                context?.startService(intentBack)
            }
            BaseUtils.ACTION_PLAY_SONG -> {
                val intentPlay = Intent(context, SongService::class.java)
                intentPlay.putExtra("key_action", BaseUtils.ACTION_PLAY_SONG)
                context?.startService(intentPlay)
            }
            BaseUtils.ACTION_NEXT_SONG -> {
                val intentNext = Intent(context, SongService::class.java)
                intentNext.putExtra("key_action", BaseUtils.ACTION_NEXT_SONG)
                context?.startService(intentNext)
            }
            BaseUtils.ACTION_PAUSE_SONG -> {
                val intentPause = Intent(context, SongService::class.java)
                intentPause.putExtra("key_action", BaseUtils.ACTION_PAUSE_SONG)
                context?.startService(intentPause)
            }
            BaseUtils.ACTION_CLOSE_SONG -> {
                val intentClose = Intent(context, SongService::class.java)
                intentClose.putExtra("key_action", BaseUtils.ACTION_CLOSE_SONG)
                context?.startService(intentClose)
            }
        }
        context?.sendBroadcast(Intent(intent?.action))

    }
}