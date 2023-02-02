package com.example.tapbuy

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
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
 * Use the [FragmentSearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSearch : Fragment(), AdapterRecycleSearch.ItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor :SharedPreferences.Editor

    private lateinit var cbName : CheckBox
    private lateinit var cbDistance : CheckBox
    private lateinit var cbPrice : CheckBox
    private lateinit var cbExpedition : CheckBox

    private lateinit var editName : EditText
    private lateinit var editDistance : EditText
    private lateinit var editPrice : EditText
    private lateinit var switchExpedition : SwitchCompat
    private lateinit var expeditionObj : String

    private lateinit var btnResearch : Button
    private lateinit var btnSavedResearch : Button
    private lateinit var listMail : ArrayList<String>
    private lateinit var recycleSearchedObj : RecyclerView
    private lateinit var adapterRecycle : AdapterRecycleSearch
    private lateinit var arraySearchedObject : ArrayList<MyObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        db = Firebase.firestore
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        arraySearchedObject = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentSearch.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentSearch().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cbName = view.findViewById(R.id.cbName)
        cbDistance = view.findViewById(R.id.cbDistance)
        cbPrice = view.findViewById(R.id.cbPrice)
        cbExpedition = view.findViewById(R.id.cbExpedition)

        editName = view.findViewById(R.id.edNameObj)
        editDistance = view.findViewById(R.id.etDistance)
        editPrice = view.findViewById(R.id.etPrice)
        switchExpedition = view.findViewById(R.id.sendObj)

        btnResearch = view.findViewById(R.id.buttonResearch)
        btnSavedResearch = view.findViewById(R.id.btn_save_research)

        val linearLayout = LinearLayoutManager(context)
        recycleSearchedObj = view.findViewById(R.id.recycleSearch)
        recycleSearchedObj.layoutManager = linearLayout

        searchObjects()


        }


    override fun onResume() {
        super.onResume()
        listMail = arrayListOf()
        db.collection("Oggetti").get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val mail = document.id
                    listMail.add(mail)
                    Log.d("mail", mail)
                }

            }
            .addOnFailureListener{ err ->
                Log.e(TAG, "Error downloading categories object : $err")
            }


        //val collezione = db.collection("Oggetti")
        //Log.d("refDB", "$collezione")



    }





    private fun saveResearch(){
        editor = sharedPref.edit()
        //editor.putStringSet()
        editor.apply()
    }

    private fun searchObjects(){
        adapterRecycle = AdapterRecycleSearch(context, arraySearchedObject)
        recycleSearchedObj.adapter = adapterRecycle
        adapterRecycle.setClickListener(this)
    }

    override fun onItemClick(view: View?, position: Int) {


    }
}