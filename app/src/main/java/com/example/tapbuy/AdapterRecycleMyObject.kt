package com.example.tapbuy

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class AdapterRecycleMyObject(context: Context?, private val mList:List<MyObject>) : RecyclerView.Adapter<AdapterRecycleMyObject.ViewHolder>() {

    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null


    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    init {
        mInflater = LayoutInflater.from(context)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view:View= mInflater.inflate(R.layout.card_object, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class

        Picasso.get().load(itemsViewModel.photo).into(holder.imageObject)

        // sets the text to the textview from our itemHolder class
        //holder.textTitle.text = itemsViewModel.

       // holder.textPrice.text = itemsViewModel.


    }
    override fun getItemCount(): Int {
        return mList.size
    }
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener= itemClickListener
    }
    // Holds the views for adding it to image and text
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imageObject : ImageView = itemView.findViewById(R.id.image_object)
        val textTitle : TextView = itemView.findViewById(R.id.titleObj)
        val textPrice : TextView = itemView.findViewById(R.id.priceObj)

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
