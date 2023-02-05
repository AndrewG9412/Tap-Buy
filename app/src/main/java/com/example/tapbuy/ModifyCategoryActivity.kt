package com.example.tapbuy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.bundle.BundleElement

class ModifyCategoryActivity : AppCompatActivity() {

    private var TAG = "ModifyCategoryActivity"
    private lateinit var extras : Bundle
    private lateinit var catToEdit : String

    private lateinit var etModCat : EditText
    private lateinit var btnModCat : Button

    private lateinit var db: FirebaseFirestore
    private lateinit var listMail : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modify_category)

        extras = intent.extras!!

        db = Firebase.firestore

        catToEdit = extras.getString("category_to_edit").toString()

        etModCat = findViewById(R.id.etModCat)
        btnModCat = findViewById(R.id.btnModCat)

    }

    override fun onResume() {
        super.onResume()

        btnModCat.setOnClickListener{
           val newName = etModCat.text.toString()

           db.collection("Categorie").document(catToEdit).delete()

           val map = hashMapOf("nome" to newName)

           db.collection("Categorie").document(newName).set(map)
           retrieveMail()

           for (mail in listMail){
               db.collection("Oggetti").document(mail).collection("miei_oggetti")
                   .whereEqualTo("categoria", catToEdit).
                   get().addOnSuccessListener {
                   for (document in it){
                       db.collection("Oggetti").document(mail).collection("miei_oggetti")
                           .document(document.data["titolo"].toString()).update("categoria", etModCat.text.toString())
                   }
               }
           }



            val intent = Intent(this, LandingActivityAdmin::class.java )
           startActivity(intent)
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


}
