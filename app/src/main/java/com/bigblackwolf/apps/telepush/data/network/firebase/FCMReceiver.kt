package com.bigblackwolf.apps.telepush.data.network.firebase

import android.util.Log
import com.bigblackwolf.apps.telepush.activities.ChatActivity
import com.bigblackwolf.apps.telepush.data.network.api.RetrofitClient
import com.bigblackwolf.apps.telepush.data.pojo.ContactPojo
import com.bigblackwolf.apps.telepush.data.pojo.MessagePojo
import com.bigblackwolf.apps.telepush.data.pojo.ResponseStatus
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*
import android.content.Intent
import android.os.Bundle


class FCMReceiver : FirebaseMessagingService() {
    companion object {
        const val TAG = "FCMReceiver"
        const val BROADCAST_MESSAGE_NAME ="ACTION_NEW_MESSAGE_RECEIVED"
        const val EXTRA_NAME = "NEW_MESSAGE"
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.i(TAG,"New message from: ${message.data["sender"]}, message: ${message.data["message"]}")

        val bundle = Bundle()
        bundle.putString("sender", message.data["sender"]!!)
        bundle.putString("message", message.data["message"]!!)
        bundle.putString("date_of_sending", message.data["date_of_sending"]!!)

        val newIntent = Intent()
        newIntent.action = BROADCAST_MESSAGE_NAME
        newIntent.putExtra(EXTRA_NAME, bundle)

        sendBroadcast(newIntent)
    }



}