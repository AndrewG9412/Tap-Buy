package com.example.tapbuy

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.*
import com.example.tapbuy.utils.Utils.Companion.setEditableText
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentNewAdvert.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentNewAdvert : Fragment(), DownloadCategoryCallback, UploadImageOnStorageCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val TAG = "FragmentNewAdvert"
    private val LOCATION_REQUEST_CODE = 101

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var storageRef : StorageReference
    private lateinit var email: String

    private lateinit var ETtitleObj : EditText
    private lateinit var imageObj : ImageView
    private lateinit var spinnercategoryObj : Spinner
    private lateinit var ETpriceObj : EditText
    private lateinit var spinnerConditionObj : Spinner
    private lateinit var ETdescriptionObj : EditText
    private lateinit var switchExpeditionObj : SwitchCompat
    private lateinit var ETemailObj : EditText
    private lateinit var ETphoneObj : EditText

    private lateinit var btn_gallery : ImageButton
    private lateinit var btn_photo : ImageButton
    private lateinit var btn_location : ImageButton
    private lateinit var btn_create : Button

    private lateinit var titleObj : String
    private lateinit var conditionObj : String
    private lateinit var priceObj : String
    private lateinit var categoryObj : String
    private lateinit var descriptionObj : String
    private lateinit var expeditionObj : String
    private lateinit var emailObj : String
    private lateinit var phoneObj : String
    private lateinit var addressObj : String
    private lateinit var listCategories : ArrayList<String>

    private lateinit var latitude : String
    private lateinit var longitude : String

    private lateinit var photo_uri : Uri
    private lateinit var selectedFile : Uri
    private lateinit var downloadUrlImageObj : String

    private lateinit var notificationManager : NotificationManager

    private val CAMERA_PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        storageRef = storage.reference
        email = auth.currentUser?.email.toString()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_new_advert, container, false)
        spinnercategoryObj = view.findViewById(R.id.spinnerCat)
        downloadCategories(this)

       return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ETtitleObj = view.findViewById(R.id.edNameObj)
        imageObj = view.findViewById(R.id.image_object)

        ETpriceObj = view.findViewById(R.id.editPrice)
        spinnerConditionObj = view.findViewById(R.id.spinCondition)
        val adapterCondition = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.object_condition))
        adapterCondition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConditionObj.adapter = adapterCondition
        spinnerConditionObj.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, id: Long) {
                conditionObj = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        ETdescriptionObj = view.findViewById(R.id.etDescription)
        switchExpeditionObj = view.findViewById(R.id.sendObj)
        ETemailObj = view.findViewById(R.id.etEmail)
        ETphoneObj = view.findViewById(R.id.editTextPhone)
        ETlocationObj = view.findViewById(R.id.etAddress)

        btn_gallery = view.findViewById(R.id.btn_gallery)
        btn_photo = view.findViewById(R.id.btn_photo)
        btn_location = view.findViewById(R.id.btn_location)
        btn_create = view.findViewById(R.id.buttonCreate)

        notificationManager = this.activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    override fun onResume() {
        super.onResume()

        checkSwitchExpedition()

        btn_gallery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 33) {
                requestPermissionGallery.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                requestPermissionGallery.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }

        btn_photo.setOnClickListener {
            requestPermissionCamera.launch(android.Manifest.permission.CAMERA)
        }

        btn_create.setOnClickListener{
           if(checkData()) {
               uploadImageOnStorage(this)
           }
        }

        btn_location.setOnClickListener{
            ETlocationObj.setEditableText("")
            retrieveLocationobj()
        }

    }


    private fun checkData() : Boolean {
        if (ETtitleObj.text.isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.insert_title), Toast.LENGTH_LONG).show()
            return false
        }
        else titleObj =  ETtitleObj.text.toString()
        if (ETpriceObj.text.isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.insert_price), Toast.LENGTH_LONG).show()
            return false
        }
        else priceObj = ETpriceObj.text.toString()
        if (ETdescriptionObj.text.isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.insert_description), Toast.LENGTH_LONG).show()
            return false
        }
        else descriptionObj = ETdescriptionObj.text.toString()
        if (ETemailObj.text.isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.insert_email), Toast.LENGTH_LONG).show()
            return false
        }
        else emailObj = ETemailObj.text.toString()
        if (ETphoneObj.text.isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.insert_phone), Toast.LENGTH_LONG).show()
            return false
        }
        else phoneObj = ETphoneObj.text.toString()
        if (ETlocationObj.text.isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.insert_location), Toast.LENGTH_LONG).show()
            return false
        }
        else addressObj = ETlocationObj.text.toString()
        return true
    }

    private fun retrieveLocationobj()  {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        val coordinates = LatLng(location.latitude, location.longitude)
                        Log.d(TAG, "${location.latitude}")
                        retrieveAddressFromLatLng(coordinates)
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


    @SuppressLint("RestrictedApi")
    private fun retrieveAddressFromLatLng(coordinates: LatLng) {
        lateinit var geocodeMatches : List<Address>
        var indirizzo = ""
        try {
            val geocoder = Geocoder(requireContext())
            if (Build.VERSION.SDK_INT >= 33) {
                // declare here the geocodeListener, as it requires Android API 33
                val geocodeListener = Geocoder.GeocodeListener { addresses ->
                    indirizzo = addresses[0].getAddressLine(0)
                    ETlocationObj.setEditableText(indirizzo)
                }
                geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1, geocodeListener)
            }
            else {
                val uploadWorkRequest = OneTimeWorkRequest.Builder(WorkerLocationClass::class.java).setInputData(
                    workDataOf("latitude" to coordinates.latitude, "longitude" to coordinates.longitude)
                ).build()
                WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest)
            }
        }
         catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permissionType), requestCode)
    }

    private var resultIntentSelectFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedFile = result.data?.data!!
            imageObj.setImageURI(selectedFile)
            imageObj.tag = selectedFile

        }
    }

    private var resultIntentShootPhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageObj.setImageURI(photo_uri)
            imageObj.tag = photo_uri
        }
    }

    private fun uploadImageOnStorage(callback : UploadImageOnStorageCallback){
        val fileRef = storageRef.child("$email/$titleObj.png")
        val fileImageRef = storageRef.child("${imageObj.tag}/$email/$titleObj.png")
        fileImageRef.putFile(imageObj.tag!! as Uri)
            .addOnSuccessListener { it ->
                Log.d("Firebase", "Upload completato : ${it.metadata?.path}")
                fileImageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        downloadUrlImageObj = uri.toString()
                        Log.d("Firebase", "File loc: ${uri.path}")
                        callback.receivedDownloadUrl(downloadUrlImageObj)
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", "download failed")
                    }
            }
            .addOnFailureListener{
                Log.d("Firebase", "upload non completato")
            }
    }

    private val requestPermissionCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i("DEBUG", "permission granted")
                openCamera()
            } else {
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), android.Manifest.permission.CAMERA)
                Log.i("DEBUG", "permission denied")
            }
        }


    private val requestPermissionGallery =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i("DEBUG", "permission granted")
                val intent = Intent()
                    .setType("image/*")
                    .setAction(Intent.ACTION_PICK)
                resultIntentSelectFile.launch(Intent.createChooser(intent, "Select a file"))
            } else {
                if (Build.VERSION.SDK_INT >= 33){
                    Log.d("ciao", "ciao")
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)
                }
                else {
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                Log.i("DEBUG", "permission denied")
            }
        }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "photo")
        photo_uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri)
        resultIntentShootPhoto.launch(cameraIntent)
    }

    private fun createHashMapObjAndUpload(downloadUrlImage: String) {
        val venduto  = "false"
        val map = hashMapOf<String, Any?>(
            "titolo" to titleObj,
            "categoria" to categoryObj,
            "indirizzo" to addressObj,
            "descrizione" to descriptionObj,
            "prezzo" to priceObj,
            "condizione" to conditionObj,
            "foto" to downloadUrlImage,
            "email" to emailObj,
            "telefono" to phoneObj,
            "spedire" to expeditionObj,
            "venduto" to venduto,
            "mailVendAuth" to email,
            "latitudine" to latitude,
            "logitudine" to longitude
        )
        val emptyMap = HashMap<String, String>()
        val ref = db.collection("Oggetti").document(email)
        ref.set(emptyMap)
        ref.collection("miei_oggetti").document(titleObj).set(map)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Oggetto aggiunto nel db")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Errore aggiunta oggetto: $e", e)
            }


        //db.collection("Chat").document(auth.currentUser?.email.toString()).collection("oggetti").document(
         //   titleObj).collection("chat").document(auth.currentUser?.uid.toString()).set(mapChat)
       // val serviceIntent = Intent(requireContext(), ListenerForegroundChat::class.java)
      //  serviceIntent.putExtra("emailSeller", emailObj )
        //serviceIntent.putExtra("titleObj", titleObj )
        //startForegroundService(requireContext(), serviceIntent)

        startActivity(Intent(requireContext(), LandingActivityUser::class.java))
    }
    companion object {

       lateinit var ETlocationObj: EditText
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentNewAdvert.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentNewAdvert().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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

    private fun checkSwitchExpedition() {
        expeditionObj = "false"
        switchExpeditionObj.setOnCheckedChangeListener { compoundButton:
                                                         CompoundButton, value: Boolean ->
            if (value) {
                compoundButton.text = getString(R.string.sendObj_yes)
                expeditionObj = "true"
            } else {
                compoundButton.text = getString(R.string.sendObj)
                expeditionObj = "false"
            }
        }
    }

    fun retrieveLocationAsync(context : Context, coordinates : LatLng) : String{
        val geocoder = Geocoder(context)
        lateinit var indirizzo : String
        val geocodeMatches =
            geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1) as List<Address>
        indirizzo = geocodeMatches[0].getAddressLine(0)
        return indirizzo
    }

    internal class WorkerLocationClass(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

        var context : Context
        init {
            context = appContext
        }

        override fun doWork(): Result {
            val latitude = inputData.keyValueMap["latitude"]
            val longitude = inputData.keyValueMap["longitude"]
            val coordinates = LatLng(latitude as Double, longitude as Double)
            Log.d("WorkerClass","It's Working")
            ETlocationObj.setEditableText(FragmentNewAdvert().retrieveLocationAsync(context, coordinates as LatLng))
            return Result.success()
        }
    }

    override fun onDataLoaded(data: ArrayList<String>) {
        val adapterCategory = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercategoryObj.adapter = adapterCategory
        spinnercategoryObj.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, id: Long) {
                categoryObj = listCategories[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun receivedDownloadUrl(downloadUrl : String){
        createHashMapObjAndUpload(downloadUrl)
    }

}

interface DownloadCategoryCallback{
    fun onDataLoaded(data: ArrayList<String>)
}

interface UploadImageOnStorageCallback{
    fun receivedDownloadUrl(downloadUrl : String)
}




