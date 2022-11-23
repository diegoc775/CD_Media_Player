package com.example.cdmediaplayer

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
//By Diego Cobos
//Activity that displays player UI, and handles media player
class MusicPlayerActivity : AppCompatActivity() {
    var playingSong : Boolean = true
    lateinit var btnPlayPause : FloatingActionButton
    lateinit var btnPrev : Button
    lateinit var btnNext : Button
    lateinit var songProgressBar : SeekBar
    var ttlSeconds :Int = 0
    var currMs : Int = 0
    lateinit var currentTime : TextView
    lateinit var totalTime : TextView
    lateinit var playList : ArrayList<File>
    var songPosition : Int = 0
    lateinit var songTitle :TextView
    private lateinit var mp: MediaPlayer
    lateinit var seekThread : Thread
    var skThrd :Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)
        //////////////////////////////UI CONFIG////////////////////////////////////
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        songTitle = findViewById<TextView>(R.id.song_Title)
        songTitle.setSingleLine()
        songTitle.isSelected = true
        songTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE)
        window.setNavigationBarColor(getResources().getColor(R.color.fiu_gold))
        btnPlayPause = findViewById<FloatingActionButton>(R.id.btnPlayPause)
        btnPrev = findViewById<Button>(R.id.btn_previous)
        btnNext = findViewById<Button>(R.id.btn_next)
        songProgressBar = findViewById<SeekBar>(R.id.songProgress)
        currentTime = findViewById<TextView>(R.id.songCurrentTime)
        totalTime = findViewById<TextView>(R.id.songTotalTime)
        //////////////////////////////UI CONFIG ^/////////////////////////////////////////

        playList = intent.getSerializableExtra("songList") as ArrayList<File>
        songPosition = intent.getIntExtra("songPosition", 0)
        prepSongandSeekBar(songPosition)

        btnPlayPause.setOnClickListener{
            if(playingSong == true || mp.isPlaying){
                playingSong = false
                btnPlayPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_song))
                songTitle.isSelected = false
                mp.pause()
            }
            else{
                playingSong = true
                btnPlayPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_song))
                songTitle.isSelected = true
                songTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE)
                mp.start()
            }
        }

        btnNext.setOnClickListener {
            playNextSong()
        }

        btnPrev.setOnClickListener {
            btnPrev.isEnabled = false
            skThrd = false
            seekThread.interrupt()
            mp.release()
            if(currMs < 3000){
                if(songPosition == 0){
                    skThrd = true
                    prepSongandSeekBar(songPosition)
                }
                else{
                    songPosition -= 1
                    skThrd = true
                    prepSongandSeekBar(songPosition)
                }
            }
            else{
                skThrd = true
                prepSongandSeekBar(songPosition)
            }
            btnPrev.isEnabled = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        skThrd = false
        seekThread.interrupt()
        mp.release()

    }
    ///////////////////////////////Lifecycle Support Functions/////////////////////////////////////////////
    fun prepSongandSeekBar( songNumber:Int){
        var temp = songNumber
        if(songNumber > playList.size){
            temp = playList.size-1
        }

        var currentFile = playList[temp]
        val uri: Uri = Uri.parse(currentFile.absolutePath)
        playSongFromUri(uri)
        songTitle.text = currentFile.name.subSequence(2,currentFile.name.length-4).toString()
        totalTime.text = getSongDuration(mp.duration)
        songTitle.isSelected = true
        songProgressBar.max = mp.duration
        songProgressBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser){
                        mp.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )
        @SuppressLint("HandlerLeak")
        var handler = object : Handler(){
            override fun handleMessage(msg: Message) {
                var currentPosition = msg.what
                songProgressBar.progress = currentPosition
                currMs = currentPosition
                var elapsedTime = createTimeLabel(currentPosition)
                currMs = currentPosition
                currentTime.text = elapsedTime
                print("CURRRRRRR SECS:"+currMs)
            }
        }
        seekThread = Thread(Runnable{
            while(mp != null && skThrd != false){
                try{
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                }
                catch (e: InterruptedException){
                    e.printStackTrace()
                }
                catch (e:IllegalStateException){
                }
            }
        })
        seekThread.start()
    }

    fun playSongFromUri(uri:Uri){
        mp = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, uri)
            prepare()
            start()
        }
        playingSong = true
        btnPlayPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_song))
        mp.setOnCompletionListener {
            playNextSong()
        }
    }

    fun playNextSong(){
        btnNext.isEnabled = false
        skThrd = false
        seekThread.interrupt()
        mp.release()
        if(songPosition == playList.size-1){
            songPosition = 0
            skThrd = true
            prepSongandSeekBar(songPosition)
        }
        else{
            songPosition += 1
            skThrd = true
            prepSongandSeekBar(songPosition)
        }
        btnNext.isEnabled = true
    }

    fun getSongDuration(songLength:Int):String{
        var songDuration = ""
        val tempTime = songLength / 1000
        val mins = tempTime / 60
        songDuration = "${mins.toString()}:"
        var seconds = (songLength - (mins*60*1000))/1000
        if(seconds < 10){
            songDuration+="0"
        }
        songDuration+="${seconds.toString()}"
        return songDuration
    }

    fun createTimeLabel(time : Int):String{
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = (time / 1000) % 60
        timeLabel = "$min:"
        if(sec < 10) timeLabel += "0"
        timeLabel += sec.toString()
        return timeLabel
    }
}