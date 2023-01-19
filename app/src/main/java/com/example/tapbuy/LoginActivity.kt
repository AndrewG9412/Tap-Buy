package com.example.tapbuy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private var TAG = "User SignIn...."

    lateinit var layoutLogin: ConstraintLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var etmail: EditText
    lateinit var etpwd: EditText
    lateinit var btnRegister: Button
    lateinit var btnLogin: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        layoutLogin = findViewById(R.id.login_layout)
        auth = Firebase.auth
        db = Firebase.firestore

        etmail = findViewById(R.id.mail)
        etpwd = findViewById(R.id.pwd)
        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_register)

        layoutLogin.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it?.windowToken, 0)
        }

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
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
            val registerActivity = Intent(this, RegisterActivity::class.java)
            startActivity(registerActivity)
        }

        btnLogin.setOnClickListener {
            val campiControllati = validateMailAndPassword()
            if (campiControllati) {
                signIn(etmail.text.toString(), etpwd.text.toString())
            }
        }
    }

    private fun validateMailAndPassword(): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(etmail.text).matches()) {
            Toast.makeText(this, getString(R.string.mail_error), Toast.LENGTH_LONG).show()
            etmail.text.clear()
            return false
        }
        if (!isValidPassword(etpwd.text.toString())) {
            Toast.makeText(this, getString(R.string.pwd_error), Toast.LENGTH_LONG).show()
            etpwd.text.clear()
            return false
        }
        return true
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }
                .firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }
                .firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }


    private fun signIn(email: String, pwd: String) {
        auth.signInWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")

                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, R.string.login_failed, Toast.LENGTH_SHORT).show()
                }
            }
        db.collection("Users")
            .document(email)
            .get()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (task.result.data?.get("tipo")
                            ?.equals("admin") == true
                    ) startActivity(Intent(this, LandingActivityAdmin::class.java))
                    else startActivity(Intent(this, LandingActivityUser::class.java))
                }else {
                    Log.w(TAG, "tipo non pervenuto", task.exception)
                }
            }
    }

}