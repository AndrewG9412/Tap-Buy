package com.example.tapbuy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LandingActivityAdmin : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_admin)

        auth = Firebase.auth

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = PageAdapterUser(supportFragmentManager, lifecycle)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager){
                tab, position ->
            when (position){
                0 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.lente)
                1 -> tab.text = getString(R.string.modify_cat)

            }
        }.attach()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId){
            R.id.logout->{
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
