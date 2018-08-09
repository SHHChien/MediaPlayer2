package com.example.apple.mediaplayer

import android.app.NotificationManager
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import java.security.AccessController.getContext

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

    ////////////////////////////////  Override LifeCycle Function  ////////////////////////////
    ////////////////////////////////  bindService() ///////////////////////////////////////////
    //當Activity調用bindService()會使用這個class

    inner class InnerBinder : Binder() {
        fun getService() : MainService{
            return this@MainService
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        return ServiceBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        Log.d("MainService onUnbind","Unbind")
    }

    ///////////////////////////////////////////////////////////////////////////////

    //基本上用不到
    constructor(){
        init()
    }

    /////////////////////////////////  startService()  ///////////////////////////////////////////////
    //當Activity使用startService()會是用這個class

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var NotificationBuilder : NotificationCompat.Builder = NotificationCompat.Builder(this.applicationContext,"forMainService")
        NotificationBuilder.setAutoCancel(true).setContentTitle("MediaPlayer").setContentText("Try It")
        var ServiceNotificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ServiceNotificationManager.notify(1, NotificationBuilder.build())

        startForeground(5,NotificationBuilder.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(MyMediaPlayer != null){
            MyMediaPlayer?.stop()
            MyMediaPlayer?.release()
            MyMediaPlayer = null

        }
        Log.d("MainService", "onDestroy")
    }

    ///////////////////////////////////  MediaPlayer  方法建立  ////////////////////////////////////////
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
        }catch(e:IllegalStateException){
            e.printStackTrace()
        }

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

    //////////////////////////////////////  初始化  //////////////////////////////////////////////////////
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
