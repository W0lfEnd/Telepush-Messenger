package com.bigblackwolf.apps.telepush.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.bigblackwolf.apps.telepush.R
import com.bigblackwolf.apps.telepush.data.adapter.ContactDataAdapter
import com.bigblackwolf.apps.telepush.data.network.api.ITelepushApi
import com.bigblackwolf.apps.telepush.data.network.api.RetrofitClient
import com.bigblackwolf.apps.telepush.data.network.firebase.Auth
import com.bigblackwolf.apps.telepush.data.pojo.ContactPojo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.bigblackwolf.apps.telepush.data.adapter.SearchedContactDataAdapter
import com.bigblackwolf.apps.telepush.data.network.firebase.FCMReceiver
import com.bigblackwolf.apps.telepush.data.pojo.MessagePojo
import com.bigblackwolf.apps.telepush.data.pojo.UserPojo
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager


class ContactsActivity : AppCompatActivity() {

    companion object {
        val TAG = "ContactsActivity"
    }
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var searchedContactsRecyclerView: RecyclerView
    private lateinit var searchedContactsLayout: LinearLayout
    private lateinit var searchContactsEditText: EditText
    private lateinit var telepushApi: ITelepushApi
    private lateinit var moreToolbarButton : AppCompatImageButton
    private lateinit var popupMenu : PopupMenu
    private lateinit var searchToolbarButton : AppCompatImageButton
    private var contacts: ArrayList<ContactPojo> = ArrayList()
    private var searchedContacts: ArrayList<UserPojo> = ArrayList()
    private lateinit var contactsDataAdapter: ContactDataAdapter
    private lateinit var searchedContactsDataAdapter: SearchedContactDataAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val activityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG,"1. FCM Message was intercepted by $TAG")
            val bundle = intent.getBundleExtra(FCMReceiver.EXTRA_NAME)
            if(bundle != null)
            {
                val senderEmail = bundle.getString("sender")!!
                val contactIndex = contacts.indexOfFirst {
                    contactPojo ->
                    contactPojo.email == senderEmail
                }
                for(i in contacts.indices)
                {
                    Log.i(TAG,"Contact[$i].email = ${contacts[i].email}")
                }
                Log.i(TAG,"2. Finded contact with email = $senderEmail, index = $contactIndex")
                if(contactIndex != -1)
                {
                    Log.i(TAG,"3. Applying message to contact...")
                    val messageText = bundle.getString("message")!!
                    val date = bundle.getString("date_of_sending")!!
                    contacts[contactIndex].lastMessage = MessagePojo(senderEmail,messageText,date)
                    contactsRecyclerView.adapter!!.notifyItemChanged(contactIndex)
                }
            }

        }
    }


    override fun onStart() {
        super.onStart()
        loadContacts()
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
        setContentView(R.layout.activity_contacts)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshContacts)
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView)
        searchedContactsRecyclerView = findViewById(R.id.searchedContactsRecyclerView)
        moreToolbarButton = findViewById(R.id.moreToolbarButton)
        searchToolbarButton = findViewById(R.id.searchToolbarButton)
        searchContactsEditText = findViewById(R.id.searchContactsEditText)
        searchedContactsLayout = findViewById(R.id.searchedContactsLayout)

        contactsRecyclerView.setHasFixedSize(true)
        contactsRecyclerView.layoutManager = LinearLayoutManager(this)
        contactsDataAdapter = ContactDataAdapter(this, contacts)
        contactsRecyclerView.adapter = contactsDataAdapter

        searchedContactsRecyclerView.setHasFixedSize(true)
        searchedContactsRecyclerView.layoutManager = LinearLayoutManager(this)
        searchedContactsDataAdapter = SearchedContactDataAdapter(this, searchedContacts)
        searchedContactsRecyclerView.adapter = searchedContactsDataAdapter



        telepushApi = RetrofitClient.getApi()

        popupMenu = PopupMenu(this@ContactsActivity,moreToolbarButton)
        popupMenu.menuInflater.inflate(R.menu.main_menu,popupMenu.menu)
        registerReceiver(activityReceiver, IntentFilter("ACTION_NEW_MESSAGE_RECEIVED"))

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId)
            {
                R.id.action_signout ->
                {
                    Auth.signOut(this)
                    true
                }
                else -> false

            }
        }

        searchContactsEditText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(searchContactsEditText.text.isEmpty())
                {
                    searchedContactsLayout.visibility = View.GONE
                    hideKeyboard()
                }
            }
        })

        searchContactsEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchToolbarButton.callOnClick()
                    return true
                }
                return false
            }
        })

        searchContactsEditText.setOnEditorActionListener{ _, _, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val obj = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                obj.hideSoftInputFromWindow(searchContactsEditText.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            }
            false
        }

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            loadContacts()
        }



    }

    public fun loadContacts() {
        val telepushApi = RetrofitClient.getApi()
        val call: Call<List<ContactPojo>> = telepushApi.getContacts(Auth.getBearerAuthHeader())
        Log.i(TAG,"Fetching contacts...")
        call.enqueue(object : Callback<List<ContactPojo>> {

            override fun onResponse(call: Call<List<ContactPojo>>, response: Response<List<ContactPojo>>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Contacts was fetched")
                    displayContacts(ArrayList(response.body() as List<ContactPojo>))
                } else {
                    Log.i(TAG, "Contacts cant be fetched, error: "+response.errorBody()!!.string())
                }
                swipeRefreshLayout.isRefreshing = false

            }

            override fun onFailure(call: Call<List<ContactPojo>>, t: Throwable) {
                Log.i(TAG, "Contacts request was failed, error: " + t.message + "\nStack trace: " + t.fillInStackTrace().message)
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    fun OnMoreVerticalButtonClick(view: View)
    {
        popupMenu.show()
    }

    fun OnSearchButtonClick(view:View)
    {
        if(searchContactsEditText.text.isNotEmpty())
            searchedContactsLayout.visibility = View.VISIBLE
        else return
        hideKeyboard()

        Log.i(TAG,"Searching users by string: ${searchContactsEditText.text}")
        val call =telepushApi.getFindUsers(searchContactsEditText.text.toString(),Auth.getBearerAuthHeader())
        Log.i(TAG,"Call: ${call.request()}")
        call.enqueue(object : Callback<List<UserPojo>>{

            override fun onFailure(call: Call<List<UserPojo>>, t: Throwable) {
                Log.i(TAG,"Searching was failed")
            }

            override fun onResponse(call: Call<List<UserPojo>>, response: Response<List<UserPojo>>) {
                Log.i(TAG,"Searching ended, message: ${response.message()}")
                if(response.isSuccessful){
                    Log.i(TAG,"Searching successful")
                    searchedContacts = ArrayList(response.body()!!)
                    searchedContactsDataAdapter.setUsers(searchedContacts)
                    searchedContactsDataAdapter.notifyDataSetChanged()
                    Log.i(TAG,"Searched users was updated (${searchedContacts.size})")
                    Toast.makeText(this@ContactsActivity,"Finded successful", Toast.LENGTH_LONG).show()

                }
            }
        })


    }

    private fun displayContacts(contacts: ArrayList<ContactPojo>) {
        this.contacts = contacts
        contactsDataAdapter.setContacts(this.contacts)
    }
    private fun addContactsToEnd(contacts: ArrayList<ContactPojo>) {
        (contactsRecyclerView.adapter as ContactDataAdapter).addContacts(contacts)
    }

    fun addContactToEnd(contact: ContactPojo) {
        (contactsRecyclerView.adapter as ContactDataAdapter).addContacts(listOf(contact))
    }

    private fun hideKeyboard()
    {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var v = currentFocus
        if (v == null)
        {
            v = View(this)
        }
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }





}
