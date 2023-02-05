package com.example.tapbuy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageSavedResearchActivity : AppCompatActivity(){

    private lateinit var sharedPref: SharedPreferences
    private lateinit var recycleViewSavedResearch : RecyclerView
    private lateinit var adapterRecycle : AdapterRecycleSavedResearch

    private lateinit var data : ArrayList<MySavedResearch>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_research)

        sharedPref = getPreferences(MODE_PRIVATE)

        val linearLayout = LinearLayoutManager(this)
        recycleViewSavedResearch = findViewById(R.id.rvresearch)
        recycleViewSavedResearch.layoutManager = linearLayout

        val setSaved = sharedPref.getStringSet("mySavedResearch", null)

        for (research in setSaved!!.iterator()){
            val arrayJson = research.toString().split(",")
            arrayJson.
            MySavedResearch(research.)
        }

        val arraySaved : ArrayList<String> = setSaved?.toList()  as ArrayList<String>
        for (line in arraySaved)

            //{"distanceObj":32,"expeditionObj":"false","nameObj":"oppo","priceObj":"80"}
            //data = arrayListOf<MySavedResearch>()
            //data.add()

                adapterRecycle = AdapterRecycleSavedResearch(this, data)

        //recycleViewSavedResearch.adapter = adapterRecycle

    }

    override fun onResume() {
        super.onResume()
    }

}
