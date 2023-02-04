package com.example.tapbuy

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
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

    private lateinit var tvTitleObj : TextView
    private lateinit var tvEmailObj : TextView

    private lateinit var tvUserSays : TextView
    private lateinit var tvReceivedMessage : TextView

    private lateinit var sendMessage : EditText
    private lateinit var sendButton : Button
    private lateinit var closeChat : Button

    private lateinit var nomeObj : String
    private lateinit var emailObj : String
    private lateinit var uidCompr : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = Firebase.auth
        db = Firebase.firestore
        email = auth.currentUser?.email.toString()

        extras = intent.extras!!
        nomeObj = extras.getString("nomeObj").toString()
        emailObj = extras.getString("emailObj").toString()
        uidCompr = extras.getString("uidCompr").toString()

        tvTitleObj = findViewById(R.id.nameObj)
        tvEmailObj = findViewById(R.id.mailObj)
        tvUserSays = findViewById(R.id.userSays)
        tvReceivedMessage = findViewById(R.id.receivedMessage)
        sendMessage = findViewById(R.id.comment)
        sendButton = findViewById(R.id.btn_send)
        closeChat = findViewById(R.id.closeChat)

        tvTitleObj.text = nomeObj
        tvEmailObj.text = emailObj

        if (uidCompr != auth.currentUser?.uid.toString()){
            //Log.d("ciao", "sono qui")
            db.collection("Chat").document("${emailObj}_${nomeObj}")
                .collection("chat").document(uidCompr).collection("message").addSnapshotListener{ snapshots, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                Log.d("ciao", "sono qui")
                                tvUserSays.text = String.format(resources.getString(R.string.userSays), dc.document.data["user"].toString())
                                tvReceivedMessage.text = dc.document.data["message"].toString()
                            }
                            DocumentChange.Type.MODIFIED -> {Log.d("ciao", "sono qui")
                                tvUserSays.text = String.format(resources.getString(R.string.userSays), dc.document.data["user"].toString())
                                tvReceivedMessage.text = dc.document.data["message"].toString()
                            }
                            DocumentChange.Type.REMOVED -> {
                            }
                        }
                    }

                }

        }

    }

    private fun createMess(){
        val messaggio = hashMapOf<String, Any?>(
            "user" to auth.currentUser?.email.toString(),
            "message" to sendMessage.text.toString(),
        )
        val emptyMap = HashMap<String, String>()
        val ref = db.collection("Chat").document("${emailObj}_${nomeObj}")
            .collection("chat").document(uidCompr)
        ref.set(emptyMap)
        ref.collection("message").document("messaggio").set(messaggio)

    }

    override fun onResume() {
        super.onResume()

        sendButton.setOnClickListener{
            if (uidCompr == auth.currentUser?.uid.toString()){
                createMess()
                db.collection("Chat").document("${emailObj}_${nomeObj}")
                    .collection("chat").document(uidCompr).collection("message").addSnapshotListener{snapshots, e ->
                        if (e != null) {
                            return@addSnapshotListener
                        }
                        for (dc in snapshots!!.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    tvUserSays.text = String.format(resources.getString(R.string.userSays), dc.document.data["user"].toString())
                                    tvReceivedMessage.text = dc.document.data["message"].toString()
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    tvUserSays.text = String.format(resources.getString(R.string.userSays), dc.document.data["user"].toString())
                                    tvReceivedMessage.text = dc.document.data["message"].toString()
                                }
                                DocumentChange.Type.REMOVED -> {
                                }
                            }
                        }

                    }
                //createMess()
            }
            else {
                createMess()
            }
            sendMessage.text.clear()
        }

        closeChat.setOnClickListener{
            startActivity(Intent(this, LandingActivityUser::class.java))
        }

    }
}
