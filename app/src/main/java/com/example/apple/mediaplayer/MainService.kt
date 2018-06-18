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

    fun playMusic(position : Int) {
        currentListPosition = position

        Log.d("postion",position.toString())

        var ID = MusicList[currentListPosition].ID
        Log.d("MUSICID",ID.toString())

        var MusicUri : Uri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ID.toLong())
        Log.d("MUSICURI",MusicUri.toString())

      //  MyMediaPlayer = MediaPlayer.create(this@MainService, MusicUri)
        MyMediaPlayer?.reset()
        MyMediaPlayer!!.setDataSource(this@MainService, MusicUri)
        MyMediaPlayer!!.prepare()
        MyMediaPlayer?.setOnPreparedListener(PreparedListener())

        //if(MyMediaPlayer!!.isPlaying()){
         //   MyMediaPlayer?.pause()
       // }
       // else{
         //   MyMediaPlayer?.start()
       // }

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
        playMusic((currentListPosition))
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
        return MusicLength
    }

    fun getCurrentMusicPosition() : Int{
        currentMusicPosition =  MyMediaPlayer!!.currentPosition
        return currentMusicPosition
    }

    override fun onBind(intent: Intent): IBinder? {
        return ServiceBinder
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
        currentListPosition++
        Log.d("currentpostion",currentListPosition.toString())

        if(currentListPosition>=MusicList.size){
            Toast.makeText(this@MainService,"Final Song",Toast.LENGTH_SHORT).show()
            currentListPosition--
        }
        else{
            playMusic(currentListPosition)
        }
    }

    fun init(){
        try {
            /*MyMediaPlayer?.reset()
            MyMediaPlayer?.setDataSource(path)
            MyMediaPlayer?.prepare()
            MyMediaPlayer?.setOnPreparedListener(PreparedListener(currentPlace))*/
            MyMediaPlayer = MediaPlayer()
            MyMediaPlayer!!.setOnCompletionListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
