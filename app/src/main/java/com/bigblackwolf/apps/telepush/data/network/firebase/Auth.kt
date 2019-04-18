package com.bigblackwolf.apps.telepush.data.network.firebase

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import com.bigblackwolf.apps.telepush.activities.ContactsActivity
import com.bigblackwolf.apps.telepush.activities.LoginActivity
import com.bigblackwolf.apps.telepush.data.network.api.RetrofitClient
import com.bigblackwolf.apps.telepush.data.pojo.ResponseStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Auth {
    companion object {
        val TAG = "Auth"
        private lateinit var userToken: String
        lateinit var userEmail: String
        var instance: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser?
            get() = instance.currentUser

        fun getBasicAuthHeader(): String {
            val header = "$userEmail:$userToken"
            return "Basic " + Base64.encodeToString(header.toByteArray(), Base64.NO_WRAP)
        }
        fun loginSuccessful(context: Context) {
            userEmail = instance.currentUser!!.email!!
            instance.currentUser!!.getIdToken(true).addOnSuccessListener {
                userToken = it.token!!
                //Log.i(TAG,"Auth.header: " + Auth.getBasicAuthHeader())
                Log.i(TAG, "Current user: ${instance.currentUser!!.email}")
                Log.i(TAG, "Token for login: $userToken")
                //send send_token to remote database

                val contactsActivity = Intent(context, ContactsActivity::class.java)
                context.startActivity(contactsActivity)
                sendSendTokenToServer(context)
            }

        }
        fun registerSuccessful(context: Context) {
            val call = RetrofitClient.getApi().registerUser(instance.currentUser!!.email!!)
            call.enqueue(object : Callback<ResponseStatus> {
                override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                    loginSuccessful(context)
                }
                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                }
            })

        }

        fun sendSendTokenToServer(context: Context,depth:Int = 0)
        {
            if(depth > 4)
                return
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                Log.i(TAG, "Token for sending: ${it.token}")
                val call = RetrofitClient.getApi().updateUserToken(it.token, Auth.getBasicAuthHeader())
                call.enqueue(object : Callback<ResponseStatus> {
                    override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                        if (response.isSuccessful) {
                            Log.i(TAG, "Send send_token to remote db; Message: ${response.body()!!.message}, isSuccessful: ${response.body()!!.status}")
                            if(!response.body()!!.status)
                                sendSendTokenToServer(context,depth + 1)
                        } else {
                            Log.i(TAG, "Send send_token to remote db; Error: ${response.errorBody()!!.string()}, msg: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                        Log.i(TAG, "Send send_token to remote db; onFailure: ${t.message}")
                    }
                })
            }
        }
        fun signOut(context: Context) {
            if (instance.currentUser != null)
                instance.signOut()
            val loginActivity = Intent(context, LoginActivity::class.java)
            context.startActivity(loginActivity)
        }

        val isLoginned: Boolean
            get() = instance.currentUser != null


    }


}