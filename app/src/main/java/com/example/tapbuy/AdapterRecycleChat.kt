package com.example.tapbuy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterRecycleChat(context: Context?, private val mList:List<MyObject>) : RecyclerView.Adapter<AdapterRecycleChat.ViewHolder>() {

    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null


    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    init {
        mInflater = LayoutInflater.from(context)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRecycleChat.ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view: View = mInflater.inflate(R.layout.row_chat, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.textUser.text = itemsViewModel.

        holder.textMessage.text = itemsViewModel.


    }
    override fun getItemCount(): Int {
        return mList.size
    }
    fun setClickListener(itemClickListener: AdapterRecycleChat.ItemClickListener?) {
        mClickListener= itemClickListener
    }
    // Holds the views for adding it to image and text
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val textUser : TextView = itemView.findViewById(R.id.user)
        val textMessage : TextView = itemView.findViewById(R.id.message)

        override fun onClick(view: View?) {
            if (mClickListener != null) {
                mClickListener!!.onItemClick(view, adapterPosition)
            }
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): MyObject {
        return mList[id]
    }
}
