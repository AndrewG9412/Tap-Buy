package com.example.tapbuy

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class ViewObject : AppCompatActivity() {

    lateinit var viewedObj : MyObject

    lateinit var btnContact : Button

    private lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseFirestore

    lateinit var extras : Bundle
    lateinit var obj : String
    lateinit var seller : String


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_view_object)

        auth = Firebase.auth
        db = Firebase.firestore

        extras = intent.extras!!

        obj = extras?.getString("object").toString()
        seller = extras?.getString("seller").toString()

        val intentObject = getSerializable(this, "object", MyObject::class.java)

        btnContact = findViewById(R.id.btn_contact)

    }

    override fun onResume() {
        super.onResume()

        btnContact.setOnClickListener{
            val intent = Intent(this, ChatUsers::class.java)
            intent.putExtra("nomeObj", )
            intent.putExtra("emailObj", auth.currentUser?.email.toString())
            intent.putExtra("imageObj", )
            startActivity(intent)
        }
    }

    private fun downloadObject(){

        db.collection("Oggetti").document(seller)
            .collection("miei_oggetti").document(obj).get()
            .addOnSuccessListener {

            }

    }

    private fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent.getSerializableExtra(name, clazz)!!
        else
            activity.intent.getSerializableExtra(name) as T
    }


}
