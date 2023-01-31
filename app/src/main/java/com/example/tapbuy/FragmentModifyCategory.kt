package com.example.tapbuy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentModifyCategory.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentModifyCategory : Fragment(), DownloadCategoryCallback, AdapterRecycleCategory.ItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var TAG = "FragmentModifyCategory"

    private lateinit var db: FirebaseFirestore

    private lateinit var listCategories : ArrayList<String>

    private lateinit var recycleViewCategories : RecyclerView
    private lateinit var adapterRecycle : AdapterRecycleCategory


    private lateinit var btnAddCategory : Button

    private lateinit var etNewCat : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d("ciao","sono qui" )
        db = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("ciao","sono qui" )
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify_category, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayout = LinearLayoutManager(context)
        recycleViewCategories = view.findViewById(R.id.rvCat)
        recycleViewCategories.layoutManager = linearLayout
        downloadCategories(this)
        Log.d("ciao","sono qui" )
        btnAddCategory = view.findViewById(R.id.btn_add_cat)
        etNewCat = view.findViewById(R.id.etNewCat)
    }

    override fun onResume() {
        super.onResume()

        btnAddCategory.setOnClickListener{
            val newCategory = etNewCat.text.toString()
            val map = hashMapOf(
                "nome" to newCategory
            )
            db.collection("Categorie").document(newCategory).set(map)
            val intent = Intent(requireContext(), LandingActivityAdmin::class.java )
            startActivity(intent)
        }

    }

    private fun downloadCategories(callback: DownloadCategoryCallback){
        listCategories = arrayListOf()
        db.collection("Categorie").get()
            .addOnSuccessListener { categories ->
                for (category in categories){
                    val categoria = category.id
                    listCategories.add(categoria)
                }
                callback.onDataLoaded(listCategories)
            }
            .addOnFailureListener{ err ->
                Log.e(TAG, "Error downloading categories object : $err")
            }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentModifyCategory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentModifyCategory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDataLoaded(data: ArrayList<String>) {
        adapterRecycle = AdapterRecycleCategory(context, data)
        recycleViewCategories.adapter = adapterRecycle
        adapterRecycle.setClickListener(this)
    }

    override fun onItemClick(view: View?, position: Int) {
        val intent = Intent(requireContext(), ModifyCategoryActivity::class.java )
        intent.putExtra("category_to_edit", listCategories[position])
        startActivity(intent)
    }
}
//data class Category(val name : String)