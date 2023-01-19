package com.example.tapbuy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity(){

    val TAG = "Register activity"

    lateinit var layoutLogin : ConstraintLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var etmail : EditText
    lateinit var etpwd : EditText
    lateinit var btnRegister : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        layoutLogin = findViewById(R.id.register_layout)
        auth = Firebase.auth
        db = Firebase.firestore

        etmail = findViewById(R.id.mail)
        etpwd = findViewById(R.id.pwd)
        btnRegister = findViewById(R.id.btn_register)

        layoutLogin.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it?.windowToken, 0)
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null) {
            db.collection("Users").document(currentUser.email.toString()).get()
                .addOnSuccessListener {
                    if (it.data?.get("tipo") == getString(R.string.admin))
                        startActivity(Intent(this, LandingActivityAdmin::class.java))
                    if (it.data?.get("tipo") == getString(R.string.user))
                        startActivity(Intent(this, LandingActivityUser::class.java))
                }
        }
    }

    public override fun onResume() {
        super.onResume()

        btnRegister.setOnClickListener {
            val campiControllati = validateMailAndPassword()
            if (campiControllati) {
                registerAccount(etmail.text.toString(), etpwd.text.toString())
            }
        }
    }

    private fun registerAccount(email : String, password : String){
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        createDocInDB(email)
                        startActivity(Intent(this, LandingActivityUser::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
                    }
                }
        }


    private fun validateMailAndPassword() : Boolean{
        if (!Patterns.EMAIL_ADDRESS.matcher(etmail.text).matches()) {
            Toast.makeText(this, getString(R.string.mail_error), Toast.LENGTH_LONG).show()
            etmail.text.clear()
            return false
        }
        if (!isValidPassword(etpwd.text.toString()))  {
            Toast.makeText(this, getString(R.string.pwd_error), Toast.LENGTH_LONG).show()
            etpwd.text.clear()
            return false
        }
        return true
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false
        return true
    }

    private fun createDocInDB(email : String) {
        val entry = hashMapOf<String, Any?>(
            "tipo" to "user"
        )
        db.collection("Users")
            .document(email).set(entry).addOnSuccessListener {
                Log.d(TAG, "Data added to the document")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}
