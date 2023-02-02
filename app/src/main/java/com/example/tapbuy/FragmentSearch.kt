package com.example.tapbuy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
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
class FragmentSearch : Fragment(), AdapterRecycleSearch.ItemClickListener, DownloadResearchedObjectCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var TAG = "FragmentSearch"

    private lateinit var db : FirebaseFirestore

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

    private lateinit var btnResearch : Button
    private lateinit var btnSavedResearch : Button

    private lateinit var recycleSearchedObj : RecyclerView
    private lateinit var adapterRecycle : AdapterRecycleSearch
    private lateinit var arraySearchedObject : ArrayList<MyObject>

    private lateinit var valExpedition : String

    private lateinit var listMail : ArrayList<String>

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 101

    private lateinit var coordinatesBuyer : LatLng

    private lateinit var mySavedResearch: HashSet<MySavedResearch>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        arraySearchedObject = ArrayList()
        db = Firebase.firestore
        retrieveMail()
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

    }

    override fun onResume() {
        super.onResume()

        btnResearch.setOnClickListener{

        }

        btnSavedResearch.setOnClickListener{
            saveResearch()
        }

    }


    fun retrieveMail(){
        listMail = arrayListOf()
        db.collection("Oggetti").get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val mail = document.id
                    listMail.add(mail)
                }
            }
            .addOnFailureListener{ err ->
                Log.e(TAG, "Error downloading categories object : $err")
            }
    }

    private fun saveResearch(){
        editor = sharedPref.edit()
        mySavedResearch.add()
        //dentro sharedpref si devono mettere Set(hashSet ho usato)
        //prova a vedere se riesci a metterci dentro un hashSet (mySavedResearch) di oggetti MySavedResearch
        editor.putStringSet("mySavedResearch", mySavedResearch )
        editor.apply()
    }

    private fun searchObjects(callback: DownloadResearchedObjectCallback){
        for (mail in listMail){
            db.collection("Oggetti").document(mail)
                .collection("miei_oggetti").whereArrayContains("spedire", valExpedition).get()
                .addOnSuccessListener {
                    for (obj in it) {
                        if (obj.get("titolo").toString().contains(editName.text.toString()))
                        val coordinatesObj = LatLng(obj.get("latitude").toString().toDouble(), obj.get("longitude").toString().toDouble())
                            if (haversine(coordinatesBuyer, coordinatesObj) <= (editDistance.text.toString().toDouble()))

                                obj.get("prezzo"))
                    }

                arraySearchedObject.add()
                }
        callback.onDataLoaded(arraySearchedObject)
        }



        adapterRecycle = AdapterRecycleSearch(context, arraySearchedObject)
        recycleSearchedObj.adapter = adapterRecycle
        adapterRecycle.setClickListener(this)
    }


    private fun haversine(p: LatLng, t: LatLng): Double {
        val lon1 = Math.toRadians(p.longitude)
        val lat1 = Math.toRadians(p.latitude)
        val lon2 = Math.toRadians(t.longitude)
        val lat2 = Math.toRadians(t.latitude)
        val dlon = lon2 - lon1
        val dlat = lat2 - lat1
        val a = Math.pow(Math.sin(dlat / 2), 2.0) +
                Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2.0)
        val c = 2 * Math.asin(Math.sqrt(a))
        return 6367 * c // 6367 is Earth radius in Km
    }

    private fun retrieveLocationobj()  {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        coordinatesBuyer = LatLng(location.latitude, location.longitude)

                    }
                }
                .addOnFailureListener { err->
                    Log.w(TAG, "error retrieving location : $err")
                }
        }
        else {
            requestPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_REQUEST_CODE
            )
        }
    }

    private fun calculateDistance(latitudeBuyer : String, longitudeeBuyer : String, latitudeObj : String, longitudeObj : String ){

    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permissionType), requestCode)
    }
    private fun checkSwitchExpedition() {
        valExpedition = ""
        switchExpedition.setOnCheckedChangeListener { compoundButton:
                                                         CompoundButton, value: Boolean ->
            if (value) {
                compoundButton.text = getString(R.string.sendObj_yes)
                valExpedition = "true"
            } else {
                compoundButton.text = getString(R.string.sendObj)
                valExpedition = "false"
            }
        }
    }


    override fun onItemClick(view: View?, position: Int) {
        val intent = Intent(requireContext(), ViewObject::class.java)
        intent.putExtra("obj", arraySearchedObject[position] )
        startActivity(intent)
    }

    override fun onDataLoaded(data: ArrayList<MyObject>) {

    }
}
interface DownloadResearchedObjectCallback{
    fun onDataLoaded(data: ArrayList<MyObject>)
}

da