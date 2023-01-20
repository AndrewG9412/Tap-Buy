package com.example.tapbuy

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatUsers : AppCompatActivity() {

    lateinit var extras : Bundle

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var email : String

    private lateinit var recyclerChat : RecyclerView
    private lateinit var adapterRecycle : AdapterRecycleChat

    private lateinit var imageObj : ImageView
    private lateinit var titleObj : TextView
    private lateinit var emailObj : TextView

    private lateinit var sendMessage : EditText
    private lateinit var sendButton : Button

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_chat)

        auth = Firebase.auth
        db = Firebase.firestore
        email = auth.currentUser?.email.toString()

        extras = intent.extras!!
        extras.getString("nomeObj")
        intent.getString("emailObj")
        intent.getString("imageObj", )


        val linearLayout = LinearLayoutManager(this)
        recyclerChat = findViewById(R.id.chat)
        recyclerChat.layoutManager = linearLayout
        adapterRecycle = AdapterRecycleChat(this, )
        recyclerChat.adapter = adapterRecycle

        imageObj = findViewById(R.id.image)
        titleObj = findViewById(R.id.nameObj)
        emailObj = findViewById(R.id.nameProp)
        sendMessage = findViewById(R.id.comment)
        sendButton = findViewById(R.id.btn_send)
    }

    private fun createMess(){
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)

        val messaggio = hashMapOf<String, Any?>(
            "user" to auth.currentUser?.email.toString(),
            "message" to sendMessage.text.toString(),
            "time" to current
        )

        db.collection("Chat").document(mailCompratore)
            .collection(oggettoInteressato).document().set(messaggio)
    }

    private fun downloadMessages(){
        db.collection("Chat").document(mailcompratore).collection(ogg)
    }

    override fun onResume() {
        super.onResume()


    }
}

data class messageChat(val user : String, val message : String, date : String)