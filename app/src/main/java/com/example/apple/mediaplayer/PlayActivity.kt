package com.example.apple.mediaplayer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {
    lateinit var ServiceBinder : MainService.InnerBinder
    lateinit var MusicService : MainService
    var currentMusicPosition : Int = 0



    private var sc : ServiceConnection = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            ServiceBinder = service as MainService.InnerBinder
            MusicService=ServiceBinder.getService()
            modifySeekBar()
        }
    }


//    var flag=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        var isPlaying = false


        bindServiceAndAcitivity()

        /*var barThread=Thread()
        barThread.run {

            var currentSeekBarPosition : Int = 0

            runOnUiThread {object : Runnable{
                override fun run() {
                        currentSeekBarPosition = MusicService.getCurrentSeekBarPosition()
                        MusicBar.setProgress(currentSeekBarPosition)
                }
            }

            }
            Thread.sleep(1000)
        }
        barThread.start()*/

        PlayImageButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if(MusicService.MyMediaPlayer!!.isPlaying){
                    MusicService.pauseMusic()
                    PlayImageButton.setImageResource(R.drawable.ic_music_play)
                }else{
                    MusicService.resumeMusic()
                    PlayImageButton.setImageResource(R.drawable.ic_music_pause)
                }
            }
        })

        PreviousImageButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){
                MusicService.previousMusic()
            }
        })

        NextImageButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){
                MusicService.nextMusic()
            }
        })
    }

    fun bindServiceAndAcitivity(){
        var intent : Intent = Intent(this@PlayActivity, MainService ::class.java)
        bindService(intent, sc, BIND_AUTO_CREATE)
    }

    //改變 SeekBar
    fun modifySeekBar(){
        MusicBar.max = MusicService.getMusicLength()
        var mRunnable : Runnable = object :Runnable {
            override fun run(){
                while(currentMusicPosition<MusicBar.max || !MusicService.isEmpty()){
                    /*if(currentMusicPosition == MusicBar.max){
                        MusicBar.max = MusicService.getMusicLength()
                        currentMusicPosition = 0
                    }*/
                    currentMusicPosition = MusicService.getCurrentMusicPosition()
                    MusicBar.setProgress(currentMusicPosition)
                }

                Log.d("currentMusicPosition", currentMusicPosition.toString())


                /*MusicBar.max = MusicService.getMusicLength()

                while(currentMusicPosition<=MusicBar.max){
                    currentMusicPosition = MusicService.getCurrentMusicPosition()
                    MusicBar.setProgress(currentMusicPosition)
                    Log.d("currentMusicPosition", currentMusicPosition.toString())
                }*/

            }
        }
        var skThread : Thread = Thread(mRunnable)
        skThread.start()

        MusicBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            //對seekbar移動到哪(progress),告訴service內mediaplayer該執行哪
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    MusicService.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                MusicService.pauseMusic()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                MusicService.resumeMusic()
            }
        })
    }
}
