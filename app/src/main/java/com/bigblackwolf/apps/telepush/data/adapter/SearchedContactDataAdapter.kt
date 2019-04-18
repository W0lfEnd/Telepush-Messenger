package com.bigblackwolf.apps.telepush.data.adapter

import android.content.Context
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
import com.bigblackwolf.apps.telepush.activities.ContactsActivity
import com.bigblackwolf.apps.telepush.data.network.api.ITelepushApi
import com.bigblackwolf.apps.telepush.data.network.api.RetrofitClient
import com.bigblackwolf.apps.telepush.data.network.firebase.Auth
import com.bigblackwolf.apps.telepush.data.pojo.ContactPojo
import com.bigblackwolf.apps.telepush.data.pojo.ResponseStatus
import com.bigblackwolf.apps.telepush.data.pojo.UserPojo
import kotlinx.android.synthetic.main.searched_contact_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.collections.ArrayList

class SearchedContactDataAdapter : RecyclerView.Adapter<SearchedContactDataAdapter.SearchedContactViewHolder> {
    companion object {
        const val TAG = "SrchContactDataAdapter"
    }
    inner class SearchedContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nickname: TextView = itemView.searchedContactNickname
        private val email: TextView = itemView.searchedContactEmail

        fun setData(userItem: UserPojo) {
            nickname.text = userItem.nickname
            email.text = userItem.email
            itemView.addUserToContactsButton.setOnClickListener {
                itemView.addUserToContactsButton.isEnabled = false
                val call = RetrofitClient.getApi().addUserToContacts(userItem.email,Auth.getBasicAuthHeader())
                call.enqueue(object :Callback<ResponseStatus>{

                    override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                        itemView.addUserToContactsButton.isEnabled = true
                    }

                    override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                        if(response.isSuccessful)
                        {
                            response.body()?.let {
                                if(response.body()!!.status)
                                {
                                    if(searchedUsers.removeAll { it.email == userItem.email }){
                                        Toast.makeText(context,"User was added to contacts", Toast.LENGTH_LONG).show()
                                        refresh()
                                        if(context is ContactsActivity)
                                            context.loadContacts()
                                    }
                                }
                            }

                        }
                        else
                            Toast.makeText(context,"Please try again", Toast.LENGTH_LONG).show()
                        itemView.addUserToContactsButton.isEnabled = true
                    }
                })
            }
        }


    }

    private var searchedUsers: ArrayList<UserPojo>
    private val layoutInflater: LayoutInflater
    private val context: Context

    constructor(context: Context, searchedUsers: ArrayList<UserPojo>) {
        this.context = context
        this.searchedUsers = searchedUsers
        this.layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedContactDataAdapter.SearchedContactViewHolder {
        val view: View = layoutInflater.inflate(R.layout.searched_contact_item, parent, false)
        return SearchedContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchedContactDataAdapter.SearchedContactViewHolder, position: Int) {
        holder.setData(searchedUsers[position])
    }

    override fun getItemCount(): Int {
        return searchedUsers.size
    }

    fun addUsers(conts : List<UserPojo>)
    {
        searchedUsers.addAll(conts)
        refresh()
    }
    fun refresh()
    {
        notifyDataSetChanged()
    }
    fun setUsers(conts : ArrayList<UserPojo>)
    {
        searchedUsers = conts
        refresh()
    }

    fun clearUsers()
    {
        searchedUsers.clear()
        refresh()
    }
}