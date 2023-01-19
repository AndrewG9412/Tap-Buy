package com.example.tapbuy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMyObject.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentMyObject : Fragment(), AdapterRecycleMyObject.ItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val TAG = "FragmentMyObject"

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var email : String

    private lateinit var recyclerViewMyObject : RecyclerView
    private lateinit var adapterRecycle : AdapterRecycleMyObject
    private lateinit var listMyObject : ArrayList<MyObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        auth = Firebase.auth
        db = Firebase.firestore
        email = auth.currentUser?.email.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_object, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayout = LinearLayoutManager(context)
        recyclerViewMyObject = view.findViewById(R.id.recycleViewObject)
        recyclerViewMyObject.layoutManager = linearLayout
        adapterRecycle = AdapterRecycleMyObject(context, listMyObject)
        recyclerViewMyObject.adapter = adapterRecycle

        adapterRecycle.setClickListener(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentMyObject.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentMyObject().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun downloadListMyObject(db : FirebaseFirestore, email : String) : List<MyObject> {
        listMyObject = arrayListOf()
        db.collection("Oggetti").document(email).collection("miei_oggetti")
            .get().addOnSuccessListener {result ->
                for (document in result){
                    lateinit var obj : MyObject
                    document.data.forEach{
                        when (it.key){
                            "titolo" -> obj.title = it.value as String
                            "categoria" -> obj.category = it.value as String
                            "indirizzo" -> obj.address = it.value as String
                            "descrizione" -> obj.description = it.value as String
                            "prezzo" -> obj.price = it.value as String
                            "condizione" -> obj.condition = it.value as String
                            "foto" -> obj.photo = it.value as String
                            "email" -> obj.email = it.value as String
                            "telefono" -> obj.phone = it.value as String
                            "spedire" -> obj.expedition = it.value as String
                        }
                    }
                listMyObject.add(obj)
                }
            }
            .addOnFailureListener { err ->
                Log.e(TAG, "Error retrieving Object from Firestore: $err")

            }
        return listMyObject
    }

    override fun onItemClick(view: View?, position: Int) {
        val clickedObj = listMyObject[position]
        val intent = Intent(context, ViewObject::class.java)
        intent.putExtra("object", clickedObj)
        startActivity(intent)
    }
}