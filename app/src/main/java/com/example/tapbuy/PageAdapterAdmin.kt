package com.example.tapbuy

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapterAdmin  (private val fm: FragmentManager, private val lifecycle : Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                FragmentSearch()
            }
            1 -> {
                FragmentModifyCategory()
            }
            else -> {
                FragmentSearch()
            }
        }
    }

}