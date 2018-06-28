package com.example.apple.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var path : String = "abc"
//    var MyMediaPlayer : MediaPlayer? = MediaPlayer()
    lateinit var PlayerBar : SeekBar
//    var mList : ArrayList<DataModel> = ArrayList<DataModel>()
    var MainServiceConnection : MainAndServiceConnection = MainAndServiceConnection()
    lateinit var MusicService : MainService

/*
    var mHandler : Handler = object : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if(msg!!.equals(0)){
                var position : Int = MyMediaPlayer!!.currentPosition
                var time : Int = MyMediaPlayer!!.duration
                var max : Int = PlayerBar.max

            }
        }
    }
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //初始化
        init()

        //create music list
        getMusicList()

        //recyclerview
        var mRecyclerviewAdapter : RecyclerviewAdapter = RecyclerviewAdapter()
        MusicListRecyclerview.layoutManager = LinearLayoutManager(this)
        MusicListRecyclerview.adapter = mRecyclerviewAdapter
        mRecyclerviewAdapter.setOnItemClickListener(object : RecyclerviewAdapter.onItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                //對MusicService下播放訊息
                MusicService.playMusic(position)

/*

                MyMediaPlayer = MediaPlayer.create(this@MainActivity, MusicUri)
                MyMediaPlayer?.setOnCompletionListener(object : MediaPlayer.OnCompletionListener{
                    override fun onCompletion(mp: MediaPlayer?) {
                        mp?.release()
                    }
                })
*/
            }

            override fun onItemLongClick(view: View, position: Int) {
            }
        })

        //Bind Service
        var intent : Intent = Intent(this,MainService::class.java)
        //startService(intent)
        bindService(intent, MainServiceConnection, Context.BIND_AUTO_CREATE)

        //PlayButton
        Player.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                goToPlayActivity()
            }

        })

        //PauseButton
        PauseButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
//                pause()
            }
        })

/*

        PlayerBar = findViewById(R.id.seekBar)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
           }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                var dest : Int = PlayerBar.progress
                var time : Int = MyMediaPlayer!!.duration
                var max : Int = PlayerBar.max

                MyMediaPlayer?.seekTo(time*dest/max)
            }

        })
*/
/*


        var tryIntent : Intent = Intent(this, MainService::class.java)
        startService(tryIntent)
*/

    }

    private fun init() {
/*

        try {
            MyMediaPlayer?.setDataSource(path)
            MyMediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            MyMediaPlayer?.prepareAsync()

            val milliseconds : Int = 100

            val t = Thread()
            Thread{
                Runnable{
                    while(true){
                        try{
                            sleep(milliseconds.toLong())
                        }catch (e : Exception){
                            e.printStackTrace()
                        }
                    }
                    mHandler.sendEmptyMessage(0)
                }
            }.start()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
*/

    }

    fun getMusicList(){
        var cursor : Cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER)


        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var MusicName = cursor.getString(cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE))
            var MusicID = cursor.getInt(cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID))
            MusicList.add(DataModel(MusicName,MusicID))
            cursor.moveToNext()
            Log.d("cursor",MusicName)
            Log.d("cursorID", MusicID.toString())
        }
        cursor.close()
    }

    fun goToPlayActivity(){
        //MyMediaPlayer?.start()
        var intent : Intent = Intent()
        intent.setClass(this@MainActivity, PlayActivity::class.java)
        startActivity(intent)
    }
/*

    fun pause(){
        MyMediaPlayer?.pause()
    }
*/

    override fun onDestroy() {
        super.onDestroy()
        unbindService(MainServiceConnection) //如果不加此行會跑錯 leaked service

/*
        if(MyMediaPlayer != null && MyMediaPlayer!!.isPlaying){
            MyMediaPlayer?.stop()
            MyMediaPlayer?.release()
            MyMediaPlayer = null
        }
*/

    }

    inner class MainAndServiceConnection : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            MusicService = (service as MainService.InnerBinder).getService()
        }

    }
}
