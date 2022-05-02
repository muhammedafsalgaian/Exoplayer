package com.example.exoplayer

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {



    private var isFullscreen=false
    private var isLock=false
    private var check=false
   private lateinit var   bt_fullscreen:ImageView
   private lateinit var  exoPlayer:ExoPlayer
    val youtubeLink = "https://youtu.be/y2tEPmwWEiI"
  private val ad=4000
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
                   handler.removeCallbacks(updateProgressAction)
                }else{
                   onProgress()
                }
            }
        })


        val videoSource=Uri.parse("")

        val mediaItem=MediaItem.fromUri(getString(R.string.media_url_mp4))  //vidioSourc
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()




    }

     fun onProgress() {

        val player= exoPlayer
        val position:Long =if (player == null) 0 else exoPlayer .currentPosition
        handler.removeCallbacks(updateProgressAction)

        val playbackState= if (player != null) Player.STATE_IDLE  else  exoPlayer.playbackState


         if (playbackState !=Player.STATE_IDLE && playbackState != Player.STATE_ENDED){

             var delayMs:Long
             if (player.playWhenReady && playbackState == Player.STATE_READY){
                 delayMs= 1000 -position %1000
                 if (delayMs <200){
                     delayMs +=1000
                 }
             }else{
                 delayMs =1000
             }


             if ((ad-3000 <= position &&  position <= ad) && !check){

                 check=true
                 initAd()
             }
             handler.postDelayed(updateProgressAction,delayMs)
         }

    }


    var rewardedInterstitialAd:RewardedInterstitialAd ?=null
    private fun initAd() {

        if (rewardedInterstitialAd !=null)  return

        MobileAds.initialize(this)

        RewardedInterstitialAd.load(this,"ca-app-pub-3940256099942544~3347511713",
            AdRequest.Builder().build(),
            object :RewardedInterstitialAdLoadCallback(){
                override fun onAdLoaded(p0: RewardedInterstitialAd) {
                    super.onAdLoaded(p0)

                    rewardedInterstitialAd=p0
                    rewardedInterstitialAd!!.fullScreenContentCallback =object :FullScreenContentCallback(){


                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()

                            exoPlayer.playWhenReady=true
                            rewardedInterstitialAd=null

                            check=false
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            Log.d("WatchScreen_AD",p0.message)
                        }



                        override fun onAdShowedFullScreenContent() {

                            handler.removeCallbacks(updateProgressAction)
                        }
                    }

                    val sec_ad_countdown=findViewById<LinearLayout>(R.id.sect_ad_countdown)
                    val tx_ad_countdown=findViewById<TextView>(R.id.tx_ad_countdown)

                    sec_ad_countdown.visibility=View.VISIBLE

                    object :CountDownTimer(4000,1000){
                        @SuppressLint("SetTextI18n")
                        override fun onTick(p0: Long) {
                            tx_ad_countdown.text="Ad in${p0/1000}"
                        }

                        override fun onFinish() {
                        sec_ad_countdown.visibility=View.GONE
                            rewardedInterstitialAd!!.show(this@MainActivity
                            ) {

                            }
                        }

                    }.start()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    rewardedInterstitialAd=null
                }



        })
    }

    private val updateProgressAction = Runnable { onProgress() }

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



