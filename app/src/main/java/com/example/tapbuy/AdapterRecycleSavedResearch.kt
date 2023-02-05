package com.example.tapbuy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterRecycleSavedResearch (context: Context?, private val mList:List<MySavedResearch>) : RecyclerView.Adapter<AdapterRecycleSavedResearch.ViewHolder>() {

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
        val view:View= mInflater.inflate(R.layout.row_research, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.textTitle.text = itemsViewModel.nameObj
        holder.textPrice.text = "€ ${itemsViewModel.priceObj}"
        holder.textDistance.text = itemsViewModel.distanceObj.toString()
        holder.textExpedition.text = itemsViewModel.expeditionObj

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

        val textTitle : TextView = itemView.findViewById(R.id.name)
        val textPrice : TextView = itemView.findViewById(R.id.price)
        val textDistance : TextView = itemView.findViewById(R.id.distance)
        val textExpedition : TextView = itemView.findViewById(R.id.expedition)
        val btn_delete_research : Button = itemView.findViewById(R.id.btnDelete)

        override fun onClick(view: View?) {
            if (mClickListener != null) {
                mClickListener!!.onItemClick(view, adapterPosition)
            }
        }

        init {
            //itemView.setOnClickListener(this)
            btn_delete_research.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): MySavedResearch {
        return mList[id]
    }

}
