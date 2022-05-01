package com.example.exoplayer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout

import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import java.net.URL

class MainActivity : AppCompatActivity(), Player.Listener {


    private lateinit var exoplayerView:PlayerView
    private  var player:ExoPlayer?=null
    private lateinit var progressBar: ProgressBar

    private var playWhenReady=true

    val youtubeLink = "https://youtu.be/y2tEPmwWEiI"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        progressBar=findViewById(R.id.progressBar)
        //title=findViewById(R.id.title)

        setUpPlayer()
        initPlayer()
     //   addMP3Files()
        //addMp4Files()

        savedInstanceState?.getInt("MediaItem")?.let { restreMedia->

            val seekTime=savedInstanceState.getLong("SeekTime")
            player?.seekTo(restreMedia,seekTime)
            player?.play()

        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        player?.let {
            outState.putLong("SeekTime", it.currentPosition)
            outState.putInt("MediaItem",it.currentMediaItemIndex)
        }


    }

    override fun onResume() {
        super.onResume()

        supportActionBar?.hide()
    }
    override fun onStop() {
        super.onStop()
        player?.release()
    }





    @SuppressLint("StaticFieldLeak")
    private fun initPlayer() {

        object : YouTubeExtractor(this) {

            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
                if (ytFiles != null) {
                    val itag = 136  //Tag of video 700p
                    val audioTag = 140 //Tag of m4a audio
                  //  val downloadUrl = ytFiles[itag].url
                    val audioUtil = ytFiles[audioTag].url
                    val videoUrl = ytFiles[itag].url


                    val audioSource: MediaSource = ProgressiveMediaSource.Factory(
                        DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(audioUtil))
                    val videoSource: MediaSource = ProgressiveMediaSource.Factory(
                        DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(videoUrl))


                    player!!.setMediaSource(
                        MergingMediaSource(true,videoSource,audioSource)
                        ,true)

                    player!!.prepare()
                    player!!.playWhenReady=playWhenReady


                }
            }
        }.extract(youtubeLink,false,true)
    }

    private fun setUpPlayer(){

        player=ExoPlayer.Builder(this).build()
        exoplayerView=findViewById(R.id.video_view)

        exoplayerView.player=player
        exoplayerView.resizeMode=AspectRatioFrameLayout.RESIZE_MODE_FILL


    }

    private fun addMp4Files(){
        val mediaItem=MediaItem.fromUri(getString(R.string.media_url_mp4))

        player?.addMediaItem(mediaItem)
        player?.prepare()
    }

//    private fun addMP3Files(){
//        val mediaItem=MediaItem.fromUri(getString(R.string.test_mp3))
//
//        player.addMediaItem(mediaItem)
//        player.prepare()
//
//    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        Log.d("TEST","Play back method is called")
    //    Toast.makeText(this,"play back method is called",Toast.LENGTH_SHORT).show()

        when(playbackState){

            Player.STATE_IDLE ->{
                progressBar.visibility=View.VISIBLE
            }
            Player.STATE_BUFFERING ->{
                progressBar.visibility=View.VISIBLE
            }

            Player.STATE_READY ->{
                progressBar.visibility =View.GONE      }

        }
    }


//    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
//        super.onMediaMetadataChanged(mediaMetadata)
//       // title.text=mediaMetadata.title ?: mediaMetadata.displayTitle ?:"title not found"
//    }
}