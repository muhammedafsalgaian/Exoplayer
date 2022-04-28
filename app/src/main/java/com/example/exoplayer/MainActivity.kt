package com.example.exoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout

import com.google.android.exoplayer2.ui.PlayerView
import java.net.URL

class MainActivity : AppCompatActivity(), Player.Listener {


    private lateinit var exoplayerView:PlayerView
    private lateinit var player:ExoPlayer
    private lateinit var progressBar: ProgressBar
    private lateinit var title:TextView


    private lateinit var urlType:URL  //need URLType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        progressBar=findViewById(R.id.progressBar)
        //title=findViewById(R.id.title)

        setUpPlayer()
       // addMP3Files()
        addMp4Files()

       if (savedInstanceState !=null){
           savedInstanceState.getInt("MediaItem").let { restreMedia->

               val seekTime=savedInstanceState.getLong("SeekTime")
               player.seekTo(restreMedia,seekTime)
               player.play()

           }
       }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("SeekTime",player.currentPosition)
        outState.putInt("MediaItem",player.currentMediaItemIndex)

    }

    override fun onStop() {
        super.onStop()
        player.release()
    }

    private fun setUpPlayer(){

        player=ExoPlayer.Builder(this).build()
        exoplayerView=findViewById(R.id.video_view)

        exoplayerView.player=player
        exoplayerView.resizeMode=AspectRatioFrameLayout.RESIZE_MODE_FILL


    }

    private fun addMp4Files(){
        val mediaItem=MediaItem.fromUri(getString(R.string.media_url_mp4))

        player.addMediaItem(mediaItem)
        player.prepare()
    }

    private fun addMP3Files(){
        val mediaItem=MediaItem.fromUri(getString(R.string.test_mp3))

        player.addMediaItem(mediaItem)
        player.prepare()

    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        when(playbackState){

            Player.STATE_BUFFERING ->{
                progressBar.visibility=View.VISIBLE
            }

            Player.STATE_READY ->{
                progressBar.visibility =View.INVISIBLE
            }
        }
    }


    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        title.text=mediaMetadata.title ?: mediaMetadata.displayTitle ?:"title not found"
    }
}