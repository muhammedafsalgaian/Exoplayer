package com.example.exoplayer

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class MainActivity : AppCompatActivity() {



    private var isFullscreen=false
    private var isLock=false
   private lateinit var   bt_fullscreen:ImageView
   private lateinit var  exoPlayer:ExoPlayer
    val youtubeLink = "https://youtu.be/y2tEPmwWEiI"

  private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler= Handler(Looper.getMainLooper())
        val playerView=findViewById<PlayerView>(R.id.video_view)
        val progressBar=findViewById<ProgressBar>(R.id.progressBar)
         bt_fullscreen=findViewById(R.id.bt_fullscreen)
        val bt_lockScreen=findViewById<ImageView>(R.id.exo_lock)

             bt_lockScreen.setOnClickListener{

                 if (! isLock){
                     bt_lockScreen.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.ic_baseline_lock))

                 }else{
                     bt_lockScreen.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.ic_baseline_lock_open_24))

                 }
                 isLock =!isLock
                 lockScreen(isLock)
             }

           bt_fullscreen.setOnClickListener{

               if (! isFullscreen){
                   bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.ic_baseline_fullscreen_exit))

                   requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

               }else{
                   bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.ic_fullscreen_icon))

//                   requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                   requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
               }

               isFullscreen =!isFullscreen
           }



        exoPlayer=ExoPlayer.Builder(this)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()

        playerView.player=exoPlayer
        playerView.keepScreenOn=true
        exoPlayer.addListener(object :Player.Listener{

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)

                if (playbackState ==Player.STATE_BUFFERING){
                    progressBar.visibility=View.VISIBLE
                }else if ( playbackState == Player.STATE_READY){

                    progressBar.visibility=View.GONE
                }

                if (!exoPlayer.playWhenReady){
                 //   handler.removeCallbacks(updatProgressAction)
                }else{
                 //   onProgress()
                }
            }
        })


        val videoSource=Uri.parse("")

        val mediaItem=MediaItem.fromUri(getString(R.string.media_url_mp4))  //vidioSourc
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()



    }

   // private val updatProgressAction = Runnable { onProgress() }

    override fun onBackPressed() {

        if (isLock) return
        if (resources.configuration.orientation ==Configuration.ORIENTATION_LANDSCAPE){

            bt_fullscreen.performClick()
        }

      else   super.onBackPressed()
    }


    override fun onStop() {
        super.onStop()
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.pause()
    }


    private fun lockScreen(lock: Boolean) {

        val sec_mid=findViewById<LinearLayout>(R.id.sec_controlvid1)
        val sec_bottom=findViewById<LinearLayout>(R.id.sec_controlvid2)

        if (lock){
            sec_mid.visibility=View.INVISIBLE
            sec_bottom.visibility=View.INVISIBLE
        }else{
            sec_mid.visibility=View.VISIBLE
            sec_bottom.visibility=View.VISIBLE
        }

    }


}



