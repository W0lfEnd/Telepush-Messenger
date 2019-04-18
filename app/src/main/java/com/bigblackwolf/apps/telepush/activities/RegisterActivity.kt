package com.bigblackwolf.apps.telepush.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bigblackwolf.apps.telepush.R
import com.bigblackwolf.apps.telepush.data.network.api.RetrofitClient
import com.bigblackwolf.apps.telepush.data.network.firebase.Auth
import com.bigblackwolf.apps.telepush.data.pojo.ResponseStatus
import com.bigblackwolf.apps.telepush.utils.Validator
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    companion object {
        val TAG = "RegisterActivity"
    }
    lateinit var registerButton: Button
    lateinit var emailInputEditText: EditText
    lateinit var passwordInputEditText: EditText
    lateinit var passwordConfirmInputEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        emailInputEditText = findViewById(R.id.emailInputEditText)
        passwordInputEditText = findViewById(R.id.passwordInputEditText)
        passwordConfirmInputEditText = findViewById(R.id.passwordConfirmInputEditText)
        registerButton = findViewById(R.id.registerButton)

    }

    fun OnRegisterButtonClick(view: View) {
        val email = emailInputEditText.text.toString()
        val password = passwordInputEditText.text.toString()
        val passwordConfirm = passwordConfirmInputEditText.text.toString()
        if (Validator.isEmail(email)) {
            if (Validator.isPassword(password, passwordConfirm)) {
                Auth.instance.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Auth.registerSuccessful(this)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }


            } else
                Toast.makeText(this, "Password fields is incorrect", Toast.LENGTH_LONG).show()
        } else
            Toast.makeText(this, "Email is incorrect", Toast.LENGTH_LONG).show()
    }

    fun OnToLoginActivityButton(view: View) {
        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
    }
}
