package com.example.tapbuy

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tapbuy.utils.Utils.Companion.setEditableText
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.io.InputStream
import java.net.URL

class ModifyObject : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val LOCATION_REQUEST_CODE = 101

    private var TAG = "ModifyAdvert"

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
    private lateinit var ETlocationObj : EditText

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

    private lateinit var photo_uri : Uri
    private lateinit var selectedFile : Uri
    private lateinit var downloadUrlImageObj : String

    private val CAMERA_PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_modify_object)

        ETtitleObj = findViewById(R.id.edNameObj)
        imageObj = findViewById(R.id.image_object)
        spinnercategoryObj = findViewById(R.id.spinnerCat)
        val adapterCategory = ArrayAdapter(this, android.R.layout.simple_spinner_item, listCategories)
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercategoryObj.adapter = adapterCategory;
        spinnercategoryObj.onItemSelectedListener = this
        ETpriceObj = findViewById(R.id.editPrice)
        spinnerConditionObj = findViewById(R.id.spinCondition)
        val adapterCondition = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.object_condition))
        adapterCondition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConditionObj.adapter = adapterCondition
        spinnercategoryObj.onItemSelectedListener = this
        ETdescriptionObj = findViewById(R.id.etDescription)
        switchExpeditionObj = findViewById(R.id.sendObj)
        ETemailObj = findViewById(R.id.etEmail)
        ETphoneObj = findViewById(R.id.editTextPhone)
        ETlocationObj = findViewById(R.id.etAddress)

        btn_gallery = findViewById(R.id.btn_gallery)
        btn_photo = findViewById(R.id.btn_photo)
        btn_location = findViewById(R.id.btn_location)
        btn_create = findViewById(R.id.buttonModify)
        downloadCategories()
    }

    override fun onResume() {
        super.onResume()

        checkSwitchExpedition()

        btn_gallery.setOnClickListener {
            checkPermessoGalleria()
        }

        btn_photo.setOnClickListener {
            checkPermessoCamera()
        }

        btn_create.setOnClickListener{
            if(checkData()) {
                uploadImageOnStorage()
                createHashMapObjAndUpload()
            }
        }

        btn_location.setOnClickListener{
            val indirizzo = retrieveLocationobj()
            ETlocationObj.setEditableText(indirizzo)
        }
    }

    private fun checkData() : Boolean {
        if (ETtitleObj.text.isEmpty()){
            Toast.makeText(this, getString(R.string.insert_title), Toast.LENGTH_LONG).show()
            return false
        }
        else titleObj =  ETtitleObj.text.toString()
        if (ETpriceObj.text.isEmpty()){
            Toast.makeText(this, getString(R.string.insert_price), Toast.LENGTH_LONG).show()
            return false
        }
        else priceObj = ETpriceObj.text.toString()
        if (ETdescriptionObj.text.isEmpty()){
            Toast.makeText(this, getString(R.string.insert_description), Toast.LENGTH_LONG).show()
            return false
        }
        else descriptionObj = ETdescriptionObj.text.toString()
        if (ETemailObj.text.isEmpty()){
            Toast.makeText(this, getString(R.string.insert_email), Toast.LENGTH_LONG).show()
            return false
        }
        else emailObj = ETemailObj.text.toString()
        if (ETphoneObj.text.isEmpty()){
            Toast.makeText(this, getString(R.string.insert_phone), Toast.LENGTH_LONG).show()
            return false
        }
        else phoneObj = ETphoneObj.text.toString()
        if (ETlocationObj.text.isEmpty()){
            Toast.makeText(this, getString(R.string.insert_location), Toast.LENGTH_LONG).show()
            return false
        }
        else addressObj = ETlocationObj.text.toString()
        return true
    }

    private fun checkPermessoGalleria() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_PICK)
            resultIntentSelectFile.launch(Intent.createChooser(intent, "Select a file"))
        }
        else {
            requestPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                1000
            )
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_PICK)
            resultIntentSelectFile.launch(Intent.createChooser(intent, "Select a file"))
        }
    }


    private fun checkPermessoCamera() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )
        if (permission == PackageManager.PERMISSION_GRANTED) openCamera()
        else {
            requestPermission(
                android.Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun retrieveLocationobj() : String   {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        var address = ""
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        val coordinates = LatLng(location.latitude, location.longitude)
                        Log.d(TAG, "ciao")
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
        return address
    }

    private fun retrieveAddressFromLatLng(coordinates : LatLng) {
        lateinit var geocodeMatches : List<Address>
        var indirizzo = ""

        val geocodeListener = Geocoder.GeocodeListener { addresses ->
            val via= addresses[0].getAddressLine(0)
            val citta = addresses[0].adminArea
            val cap = addresses[0].postalCode
            val stato = addresses[0].countryName
            indirizzo = "$via $citta, $cap, $stato"
            ETlocationObj.setEditableText("")
            ETlocationObj.setEditableText(indirizzo.toString())
        }

        try {
            val geocoder = Geocoder(this)
            if (Build.VERSION.SDK_INT >= 33) {
                // declare here the geocodeListener, as it requires Android API 33
                geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1, geocodeListener)
            } else {
                geocodeMatches =
                    geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1) as List<Address>
                val address = geocodeMatches[0].getAddressLine(0)
                val citta = geocodeMatches[0].adminArea
                val cap = geocodeMatches[0].postalCode
                val stato = geocodeMatches[0].countryName
                indirizzo = "$address, $citta, $cap, $stato"
                ETlocationObj.setEditableText("")
                ETlocationObj.setEditableText(indirizzo)
            }
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)
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

    private fun uploadImageOnStorage(){
        val fileRef = storageRef.child("$email/$titleObj.png")
        val fileImageRef = storageRef.child("${imageObj.tag}/$email/$titleObj.png")
        fileImageRef.putFile(imageObj.tag!! as Uri)
            .addOnSuccessListener { it ->
                Log.d("Firebase", "Upload completato : ${it.metadata?.path}")
                fileImageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        downloadUrlImageObj = uri.toString()
                        Log.d("Firebase", "File loc: ${uri.path}")
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", "download failed")
                    }
            }
            .addOnFailureListener{
                Log.d("Firebase", "upload non completato")
            }
    }

    private val requestPermissionGallery =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i("DEBUG", "permission granted")
            } else {
                // if permission denied then check whether never ask
                // again is selected or not by making use of
                // !ActivityCompat.shouldShowRequestPermissionRationale(
                // requireActivity(), Manifest.permission.CAMERA)
                Log.i("DEBUG", "permission denied")
            }
        }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "photo")
        photo_uri = this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri)
        resultIntentShootPhoto.launch(cameraIntent)
    }


    private fun createBitmap(uri: Uri?) : Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(URL(uri.toString()).content as InputStream)
            val newWidth = 170
            val newHeight = 170
            val width = bitmap.width
            val height = bitmap.height

            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height

            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)

            return Bitmap.createBitmap(bitmap,0,0,width, height, matrix, false)
        }
        catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }



    private fun createHashMapObjAndUpload() {
        val map = hashMapOf<String,Any?>(
            "titolo" to titleObj,
            "categoria" to categoryObj,
            "indirizzo" to addressObj,
            "descrizione" to descriptionObj,
            "prezzo" to priceObj,
            "condizione" to categoryObj,
            "foto" to downloadUrlImageObj,
            "email" to emailObj,
            "telefono" to phoneObj,
            "spedire" to expeditionObj


        )
        db.collection("Oggetti").document(email)
            .collection("miei_oggetti").document(titleObj).set(map)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Oggetto aggiunto nel db")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Errore eggiunta oggetto: $e", e)
            }

    }

    private fun downloadCategories(){
        listCategories = arrayListOf()
        db.collection("Categorie").get()
            .addOnSuccessListener { categories ->
                for (category in categories){
                    val categoria = category.id
                    listCategories.add(categoria)
                }
            }
            .addOnFailureListener{ err ->
                Log.e(TAG, "Error downloading categories object : $err")
            }
    }

    private fun checkSwitchExpedition() {
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            if (parent.id == R.id.spinnerCat){
                categoryObj = parent.getItemAtPosition(position).toString()
                spinnercategoryObj.prompt = listCategories[position]
            }
            if (parent.id == R.id.spinCondition){
                conditionObj = parent.getItemAtPosition(position).toString()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}
