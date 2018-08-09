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
//        init()

        //create music list
        getMusicList()

        //recyclerview
        var mRecyclerviewAdapter : RecyclerviewAdapter = RecyclerviewAdapter()
        MusicListRecyclerview.layoutManager = LinearLayoutManager(this)
        MusicListRecyclerview.adapter = mRecyclerviewAdapter
        mRecyclerviewAdapter.setOnItemClickListener { view, i ->  }
        mRecyclerviewAdapter.setOnItemClickListener(object : RecyclerviewAdapter.onItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                //對MusicService下播放訊息
                MusicService.playMusic(position)

            }

            override fun onItemLongClick(view: View, position: Int) {
            }
        })

        //Bind Service
        var intent : Intent = Intent(this,MainService::class.java)
        //startService(intent)
        bindService(intent, MainServiceConnection, Context.BIND_AUTO_CREATE)

        //PlayButton
        floatingActionButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                goToPlayActivity()
            }

        })

    }

//    private fun init() {
//    }

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
        var intent : Intent = Intent()
        intent.setClass(this@MainActivity, PlayActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(MainServiceConnection) //如果不加此行會跑錯 leaked service

    }

    inner class MainAndServiceConnection : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            MusicService = (service as MainService.InnerBinder).getService()
        }

    }
}
