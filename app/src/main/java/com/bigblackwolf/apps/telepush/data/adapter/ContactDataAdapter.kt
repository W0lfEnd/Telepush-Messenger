package com.bigblackwolf.apps.telepush.data.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bigblackwolf.apps.telepush.R
import com.bigblackwolf.apps.telepush.activities.ChatActivity
import com.bigblackwolf.apps.telepush.data.network.api.ITelepushApi
import com.bigblackwolf.apps.telepush.data.network.api.RetrofitClient
import com.bigblackwolf.apps.telepush.data.network.firebase.Auth
import com.bigblackwolf.apps.telepush.data.pojo.ContactPojo
import com.bigblackwolf.apps.telepush.data.pojo.ResponseStatus
import com.bigblackwolf.apps.telepush.utils.DateTimeHelper
import kotlinx.android.synthetic.main.contact_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ContactDataAdapter : RecyclerView.Adapter<ContactDataAdapter.ContactViewHolder> {
    companion object {
        val TAG = "ContactDataAdapter"
    }
    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nickname: TextView = itemView.contactNameTextView
        val lastMessage: TextView = itemView.contactLastMessageTextView
        val dateLastMessage: TextView = itemView.contactLastMessageDateTextView
        val locale: Locale = itemView.resources.configuration.locale
        var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", locale)
        lateinit var email: String

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)

                intent.putExtra("contactEmail", email)
                context.startActivity(intent)
            }

            itemView.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setMessage(context.resources.getString(R.string.do_you_want_delete_contact,nickname.text))
                        .setTitle(R.string.delete_contact)
                        .setPositiveButton(android.R.string.yes) { _,_->
                            deleteContact(email)
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .create()
                        .show()
                return@setOnLongClickListener true
            }
        }

        fun setData(contactItem: ContactPojo) {
            nickname.text = contactItem.name
            email = contactItem.email
            if(contactItem.lastMessage != null)
            {
                lastMessage.text = contactItem.lastMessage!!.message_text
                this.dateLastMessage.text = DateTimeHelper.getFormatedDate(contactItem.lastMessage!!.date_of_sending)
            }
        }


    }

    private var contactItemList: ArrayList<ContactPojo>
    private val layoutInflater: LayoutInflater
    private val context: Context

    constructor(context: Context, contactItemList: ArrayList<ContactPojo>) {
        this.context = context
        this.contactItemList = contactItemList
        this.layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactDataAdapter.ContactViewHolder {
        val view: View = layoutInflater.inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactDataAdapter.ContactViewHolder, position: Int) {
        holder.setData(contactItemList[position])
    }

    override fun getItemCount(): Int {
        return contactItemList.size
    }

    fun addContacts(conts : List<ContactPojo>)
    {
        contactItemList.addAll(conts)
        refresh()
    }
    fun refresh()
    {
        notifyDataSetChanged()
    }
    fun setContacts(conts : ArrayList<ContactPojo>)
    {
        contactItemList = conts
        refresh()
    }

    fun deleteContact(email:String)
    {
        val call = RetrofitClient.getApi().deleteUserFromContacts(email, Auth.getBasicAuthHeader())
        call.enqueue(object : Callback<ResponseStatus> {

            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                if(response.isSuccessful)
                {
                    Log.i(TAG,"Deleting contact response message: ${response.body()!!.message}")
                    Toast.makeText(context,response.body()!!.message,Toast.LENGTH_LONG).show()
                    contactItemList.removeAll {
                        it.email == email
                    }
                    refresh()
                }
            }
        })
    }

    fun clearContacts()
    {
        contactItemList.clear()
        refresh()
    }
}