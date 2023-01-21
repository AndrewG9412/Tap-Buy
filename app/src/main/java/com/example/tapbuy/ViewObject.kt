package com.example.tapbuy

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class ViewObject : AppCompatActivity() {

    lateinit var viewedObj : MyObject

    private lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseFirestore

    lateinit var extras : Bundle
    lateinit var obj : String
    lateinit var seller : String

    private lateinit var tvName : TextView
    private lateinit var tvCategory : TextView
    private lateinit var tvPrice : TextView
    private lateinit var tvExp : TextView
    private lateinit var tvCondition : TextView
    private lateinit var tvDescription : TextView
    private lateinit var tvAddress : TextView
    private lateinit var tvmail : TextView
    private lateinit var tvPhone : TextView

    lateinit var btnContact : Button
    lateinit var btn_modify : Button
    lateinit var btn_delete : Button

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_view_object)

        auth = Firebase.auth
        db = Firebase.firestore

        extras = intent.extras!!

        obj = extras?.getString("object").toString()
        seller = extras?.getString("seller").toString()

        val intentObject = getSerializable(this, "object", MyObject::class.java)

        tvName = findViewById(R.id.tvObjName)
        tvCategory = findViewById(R.id.tvCategory)
        tvPrice = findViewById(R.id.tvPrice)
        tvExp = findViewById(R.id.tvExpedition)
        tvCondition = findViewById(R.id.tvCondition)
        tvDescription = findViewById(R.id.tvDescription)
        tvAddress = findViewById(R.id.tvAddress)
        tvmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)

        btnContact = findViewById(R.id.btn_contact)
        btn_modify = findViewById(R.id.btn_modify)
        btn_delete = findViewById(R.id.btn_delete)

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

        btn_modify.setOnClickListener{
            val intent = Intent(this, ModifyObject::class.java)
        }
    }

    private fun checkUserForEnablingButtonSeller(){

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
