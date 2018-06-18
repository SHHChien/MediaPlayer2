package com.example.apple.mediaplayer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.list_item.view.*

class RecyclerviewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listener : onItemClickListener? = null

    fun setOnItemClickListener(listener: onItemClickListener){
        this.listener = listener
    }

    public interface onItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val setView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return ViewHolder(setView)
    }

    override fun getItemCount(): Int {
        return MusicList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.item_textview.text = MusicList[position].Title
        if(listener != null){
            holder.itemView.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View) {
                    listener!!.onItemClick(v, position )
                }
            })
        }
    }

    class ViewHolder : RecyclerView.ViewHolder{
        constructor(v : View) : super(v) {
        }
    }
}