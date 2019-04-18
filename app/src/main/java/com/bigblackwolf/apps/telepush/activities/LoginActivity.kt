package com.bigblackwolf.apps.telepush.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bigblackwolf.apps.telepush.R
import com.bigblackwolf.apps.telepush.data.network.firebase.Auth

import com.bigblackwolf.apps.telepush.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {

    companion object {
        val TAG = "LoginActivity"
    }
    lateinit var emailInputEditText: EditText
    lateinit var passwordInputEditText: EditText
    var isEmailInputCorrect = false
    var isPasswordInputCorrect = false
    lateinit var loginButton: Button
    lateinit var toRegisterActivityButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i(TAG, "Login activity created")

        toRegisterActivityButton = findViewById(R.id.toRegisterActivityButton)
        loginButton = findViewById(R.id.loginButton)
        emailInputEditText = findViewById(R.id.emailInputEditText)
        passwordInputEditText = findViewById(R.id.passwordInputEditText)
        if(Auth.isLoginned)
        {
            emailInputEditText.setText(Auth.currentUser!!.email)
            loginButton.isEnabled = false
            emailInputEditText.isEnabled = false
            passwordInputEditText.isEnabled = false
            Auth.loginSuccessful(this)
        }else
        {
            emailInputEditText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    isEmailInputCorrect = emailInputEditText.text.toString().length > 3 && Validator.isEmail(emailInputEditText.text.toString())
                    loginButton.isEnabled = isEmailInputCorrect && isPasswordInputCorrect
                }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?){}
            })
            passwordInputEditText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    isPasswordInputCorrect = passwordInputEditText.text.toString().length > 3
                    loginButton.isEnabled = isEmailInputCorrect && isPasswordInputCorrect

                }
                override fun afterTextChanged(p0: Editable?){}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
        }


    }

    fun OnLoginButtonClick(view: View) {
        loginButton.isEnabled = false
        emailInputEditText.isEnabled = false
        passwordInputEditText.isEnabled = false
        val password = passwordInputEditText.text.toString()
        val email = emailInputEditText.text.toString()
        Log.i(TAG,"Loginning...")
        Auth.instance.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    loginButton.isEnabled = true
                    emailInputEditText.isEnabled = true
                    passwordInputEditText.isEnabled = true
                }
                .addOnSuccessListener(this) {
                    Auth.loginSuccessful(this)
                }.addOnFailureListener {
                    // If sign in fails, display a message to the user.
                    Log.i(TAG, "SignIn:failure")
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    passwordInputEditText.text.clear()
                }
    }




    fun OnToRegisterActivityButtonClick(view: View)
    {
        val registerActivity = Intent(this, RegisterActivity::class.java)
        startActivity(registerActivity)
    }



}
