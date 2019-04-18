package com.bigblackwolf.apps.telepush.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.*
import com.bigblackwolf.apps.telepush.R
import com.bigblackwolf.apps.telepush.data.adapter.MessageDataAdapter
import com.bigblackwolf.apps.telepush.data.network.api.RetrofitClient
import com.bigblackwolf.apps.telepush.data.network.firebase.Auth
import com.bigblackwolf.apps.telepush.data.pojo.MessagePojo
import com.bigblackwolf.apps.telepush.data.pojo.ResponseMessages
import com.bigblackwolf.apps.telepush.data.pojo.ResponseStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import android.widget.TextView
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import com.bigblackwolf.apps.telepush.data.network.firebase.FCMReceiver
import com.bigblackwolf.apps.telepush.utils.DateTimeHelper
import java.time.LocalDateTime

//TODO Крашить коли відправляють сообщєніє користувачу, з яким ще не було ні одного сообщєнія

class ChatActivity : AppCompatActivity() {

    companion object {
       const val TAG = "ChatActivity"
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var emailTextView: TextView
    private lateinit var dataAdapter: MessageDataAdapter
    private lateinit var messageFetchingProgressBar : ProgressBar

    private var messages: ArrayList<MessagePojo> = ArrayList()
    private lateinit var anotherUserEmail: String

    private val messageRangeStep = 50
    private var messageRangeStart = 0
    private var isMessagesExist = true
    private var isNowFetching = false

    private val activityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val bundle = intent.getBundleExtra(FCMReceiver.EXTRA_NAME)
            if(bundle != null)
            {
                val senderEmail = bundle.getString("sender")!!
                val messageText = bundle.getString("message")!!
                val date = bundle.getString("date_of_sending")!!
                Log.i(TAG,"FCM Message was intercepted by $TAG from $senderEmail; message: $messageText")
                if(senderEmail == anotherUserEmail)
                {
                    addMessageToRecycler(MessagePojo(senderEmail,messageText,date))
                }
            }


        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(activityReceiver, IntentFilter(FCMReceiver.BROADCAST_MESSAGE_NAME))
        Log.i(TAG,"Message receiver was registered")
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(activityReceiver)
        Log.i(TAG,"Message receiver was unregistered")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        messageInput = findViewById(R.id.messageInput)
        recyclerView = findViewById(R.id.recyclerView)
        emailTextView = findViewById(R.id.emailTextView)
        messageFetchingProgressBar = findViewById(R.id.messageFetchingProgressBar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dataAdapter = MessageDataAdapter(this, messages)
        recyclerView.adapter = dataAdapter
        anotherUserEmail = intent.getStringExtra("contactEmail")
        emailTextView.text = anotherUserEmail


        fetchMessages({
            scrollToBottom(false)
            recyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0)
                    {
                        fetchMessages()
                    }
                }
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        })

    }

    private fun fetchMessages(
            onSucc : ()->Unit = {},
            onFail : ()->Unit = {}
    ) {
        if(!isMessagesExist || isNowFetching)
            return
        isNowFetching = true
        val telepushApi = RetrofitClient.getApi()
        val call: Call<ResponseMessages> = telepushApi.getMessage(anotherUserEmail, messageRangeStart, messageRangeStart + messageRangeStep, Auth.getBasicAuthHeader())
        Log.i(TAG, "Fetching messages...")
        messageFetchingProgressBar.visibility = View.VISIBLE
        call.enqueue(object : Callback<ResponseMessages> {
            override fun onResponse(call: Call<ResponseMessages>, response: Response<ResponseMessages>) {
                isNowFetching = false
                if (response.isSuccessful) {
                    val responseBody = response.body() as ResponseMessages
                    Log.i(TAG, "Messages was fetched: ${responseBody.messages.size}")
                    addMessagesToStartRecycler(responseBody.messages)
                    isMessagesExist = responseBody.isMoreMessagesExist
                    messageRangeStart +=messageRangeStep
                    onSucc.invoke()
                } else {
                    Log.i(TAG, "Error: " + response.errorBody().toString())
                }

                messageFetchingProgressBar.visibility = View.GONE

            }
            override fun onFailure(call: Call<ResponseMessages>, t: Throwable) {
                isNowFetching = false
                Log.i(TAG, "onFailure: " + t.message)
                messageFetchingProgressBar.visibility = View.GONE
                onFail.invoke()
            }
        })
    }

    fun addMessagesToStartRecycler(newMessages: List<MessagePojo>) {
        messages.addAll(0,newMessages)
        recyclerView.adapter!!.notifyItemRangeInserted(0,newMessages.size)
    }

    private fun addMessageToRecycler(newMessage: MessagePojo) {
        messages.add(newMessage)
        dataAdapter.notifyItemInserted(dataAdapter.itemCount-1)
        scrollToBottom()
    }

    private fun scrollToBottom(isSmoothly:Boolean = true)
    {
        if(recyclerView.adapter != null)
            if(recyclerView.adapter!!.itemCount > 0)
                if(isSmoothly) {
                    recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount - 1)
                }else{
                    recyclerView.scrollToPosition(recyclerView.adapter!!.itemCount - 1)
                }
    }
    fun onSendMessageClick(view: View) {

        val message = messageInput.text.toString().trim()
        if (message.isNotEmpty()) {
            sendMessage(message)
            messageInput.text.clear()
        } else {
            Toast.makeText(this, "Message to short", Toast.LENGTH_SHORT).show()
        }
        scrollToBottom()
    }

    private fun sendMessage(message: String) {
        Log.i(TAG, "Sending message to $anotherUserEmail...")
        val newMessage = MessagePojo(Auth.userEmail, message, DateTimeHelper.getCurrentServerTime(),true)
        addMessageToRecycler(newMessage)
        val call = RetrofitClient.getApi().sendMessage(anotherUserEmail, message, Auth.getBasicAuthHeader())
        call.enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Message: ${response.body()!!.message}, isSuccessful: ${response.body()!!.status}")
                    newMessage.isDontSent = false
                    dataAdapter.notifyItemChanged(dataAdapter.getMessagePosition(newMessage))
                } else {
                    Log.i(TAG, "Error: ${response.errorBody()!!.string()}, msg: ${response.code()}")

                }
            }
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun backToPreviousActivity(view:View)
    {
        finish()
    }

}
