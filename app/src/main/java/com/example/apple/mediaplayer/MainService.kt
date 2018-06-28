package com.example.apple.mediaplayer

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class MainService : Service, MediaPlayer.OnCompletionListener {


    var count : Int = 0
    var MyMediaPlayer : MediaPlayer ?= null
    var currentPlace : Int =0
    var path : String = String()
    var currentListPosition : Int = 0
    private var currentMusicPosition : Int = 0

    var startToPlay = false
    var changeSong = false

    var ServiceBinder : IBinder = InnerBinder()

    inner class InnerBinder : Binder() {
        fun getService() : MainService{
            return this@MainService
        }
    }

    constructor(){
        init()
    }
/*    constructor(arrayList: ArrayList<DataModel>){
        init()

    }*/
/*    var mHandler : Handler = object : Handler(){
         override fun handleMessage(message: Message){
             when(message.what){
                 1 -> if (message.what ==1){
                     currentPlace = MyMediaPlayer.currentPosition
                     var intent : Intent = Intent(android.content.)
                     intent.action()
                 }
             }
            *//*if(message.what == 1){
                if(MyMediaPlayer !=null){
                    currentPlace = MyMediaPlayer.currentPosition
                }
            }*//*
         }
    }*/

/*    override fun onCreate() {
        super.onCreate()
        if(MyMediaPlayer != null) {
            try {
                MyMediaPlayer?.reset()
                MyMediaPlayer?.prepare()
                MyMediaPlayer?.setOnPreparedListener(PreparedListener(currentPlace))

            } catch (e: Exception) {
                Log.DEBUG
            }
        }

        MyMediaPlayer = MediaPlayer()
        MyMediaPlayer!!.setOnCompletionListener(this)


    }*/

/*

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var timer : Timer = Timer()
        var action : TimerTask = object : TimerTask() {
            override fun run() {
                if(count<10){
                    Log.d("MainService", "Running")
                    count++
                }
                else{
                    Log.d("MainService", "Done")
                    timer.cancel()
                    stopSelf()
                }
            }
        }


        timer.schedule(action, 1000, 1000)

        Log.d("MainService", "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }
*/

    override fun onDestroy() {
        super.onDestroy()
        if(MyMediaPlayer != null){
            MyMediaPlayer?.stop()
            MyMediaPlayer?.release()
            MyMediaPlayer = null

        }
        Log.d("MainService", "onDestroy")
    }

    fun updateSeekBar(){
    }

    fun isStart() : Boolean{
        return startToPlay
    }
    fun isChangeSong() : Boolean{
        return changeSong
    }
    fun playMusic(position : Int) {
        currentListPosition = position
        Log.d("beforeplay",startToPlay.toString())
        Log.d("position",position.toString())
        var ID = MusicList[currentListPosition].ID
        Log.d("MUSICID",ID.toString())

        var MusicUri : Uri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ID.toLong())
        Log.d("MUSICURI",MusicUri.toString())

        try{
            MyMediaPlayer?.reset()
            Log.d("MyMediaPlayer reset()",MyMediaPlayer.toString())
            MyMediaPlayer!!.setDataSource(this@MainService, MusicUri)
            Log.d("Uri",MusicUri.toString())
            MyMediaPlayer!!.prepare()
            changeSong = false
            startToPlay = true
            Log.d("Afterplay",startToPlay.toString())
            MyMediaPlayer!!.start()
            if (currentMusicPosition > 0) {
                MyMediaPlayer?.seekTo(currentMusicPosition)
            }
        }catch(e:IllegalStateException){
            e.printStackTrace()
        }
        //MyMediaPlayer = MediaPlayer.create(this@MainService, MusicUri)

//        MyMediaPlayer?.setOnPreparedListener(PreparedListener())

        //if(MyMediaPlayer!!.isPlaying()){
         //   MyMediaPlayer?.pause()
       // }
       // else{
         //   MyMediaPlayer?.start()
       // }

        /*if (!MyMediaPlayer!!.isPlaying()) {
            try {
                MyMediaPlayer!!.stop()
                MyMediaPlayer!!.release()
                MyMediaPlayer = MediaPlayer()
                MyMediaPlayer!!.setDataSource(this@MainService, MusicUri)
                MyMediaPlayer!!.prepare()
                MyMediaPlayer!!.start()
            } catch (e: IllegalStateException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        } else {
            try {
                MyMediaPlayer!!.setDataSource(this@MainService, MusicUri)
                MyMediaPlayer!!.prepare()
                MyMediaPlayer!!.start()
            } catch (e: IllegalArgumentException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: SecurityException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }*/

    }

    fun seekTo(MusicBarPosition: Int){
        MyMediaPlayer!!.seekTo(MusicBarPosition)
    }

    fun pauseMusic(){
        MyMediaPlayer!!.pause()
    }

    fun resumeMusic(){
        MyMediaPlayer!!.start()
    }
    fun nextMusic() {
        currentListPosition++
        playMusic(currentListPosition)
    }

    fun previousMusic() {
        currentListPosition--
        playMusic(currentListPosition)
    }

    fun isEmpty() : Boolean{
        return MyMediaPlayer==null
    }

    fun getMusicLength() : Int{
        var MusicLength = MyMediaPlayer!!.duration
        Log.d("getMusicLength", MusicLength.toString()
        )
        return MusicLength
    }

    fun getCurrentMusicPosition() : Int{
        currentMusicPosition =  MyMediaPlayer!!.currentPosition
        return currentMusicPosition
    }

    override fun onBind(intent: Intent): IBinder? {
        return ServiceBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        Log.d("MainService onUnbind","Unbind")
    }

    inner class PreparedListener() : MediaPlayer.OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer?) {
            mp?.start()
            if (currentMusicPosition > 0) {
                mp?.seekTo(currentMusicPosition)
            }
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        changeSong = true
        currentListPosition+=1
        startToPlay = false
        Log.d("isStartinonCompletion", startToPlay.toString())
/*        if(MyMediaPlayer != null){
            MyMediaPlayer!!.stop()
            MyMediaPlayer!!.release()
            MyMediaPlayer = null
        }*/

        Log.d("currentposition",currentListPosition.toString())
        Log.d("MusicList",MusicList.size.toString())
        if(currentListPosition>=MusicList.size){
            Toast.makeText(this@MainService,"Final Song",Toast.LENGTH_SHORT).show()
            currentListPosition = 0
            Log.d("currentpostion_if",currentListPosition.toString())
        }
        playMusic(currentListPosition)
        Log.d("currentposition_2",currentListPosition.toString())
    }

    fun init(){
        Log.d("init","test")
        try {
            MyMediaPlayer?.reset()
            //MyMediaPlayer?.setDataSource(path)
            MyMediaPlayer?.prepareAsync()
            MyMediaPlayer?.setOnPreparedListener(PreparedListener())
            MyMediaPlayer = MediaPlayer()
            MyMediaPlayer!!.setOnCompletionListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
