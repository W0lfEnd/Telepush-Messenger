package com.bigblackwolf.apps.telepush.data.adapter


import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.bigblackwolf.apps.telepush.R
import com.bigblackwolf.apps.telepush.data.network.firebase.Auth
import com.bigblackwolf.apps.telepush.data.pojo.MessagePojo
import java.util.*
import android.util.DisplayMetrics
import android.util.Log
import com.bigblackwolf.apps.telepush.utils.DateTimeHelper
import java.util.TimeZone
import java.security.AccessController.getContext
import java.sql.Time
import java.text.SimpleDateFormat






class MessageDataAdapter : RecyclerView.Adapter<MessageDataAdapter.MessageViewHolder> {
    companion object {
        val TAG = "MessageDataAdapter"
    }
    //viewHolder
    class MessageViewHolder : RecyclerView.ViewHolder {
        private val messageTextView : TextView
        private val dataTextView: TextView

        private var messageContainer: LinearLayout
        private val colorOtherUser = itemView.context.resources.getColor(R.color.white)
        private val colorUser = itemView.context.resources.getColor(R.color.primary_light_x2)
        private val colorUserDontSentMessage = itemView.context.resources.getColor(R.color.material_yellow)

        val drawable: GradientDrawable = itemView.context.resources.getDrawable(R.drawable.message_styled_background) as GradientDrawable

        constructor(view: View) : super(view) {
            messageTextView = itemView.findViewById(R.id.message_item_textView)
            dataTextView = itemView.findViewById(R.id.messate_item_date)
            messageContainer = view.findViewById(R.id.message_container)
        }

        fun setData(message: MessagePojo) {

            this.messageTextView.text = message.message_text
            this.dataTextView.text = DateTimeHelper.getFormatedDate(message.date_of_sending)

            val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            if (message.sender_email.toLowerCase() == Auth.instance.currentUser!!.email) {
                params.gravity = Gravity.END
                params.rightMargin = 20
                params.leftMargin = 140
                drawable.setColor(if(message.isDontSent) colorUserDontSentMessage else colorUser)
                messageContainer.layoutParams = params
                messageContainer.background = drawable
                messageContainer.setPadding(0, 0, 0, 3)
            } else {
                params.gravity = Gravity.START
                params.leftMargin = 20
                params.rightMargin = 140
                drawable.setColor(colorOtherUser)
                messageContainer.layoutParams = params
                messageContainer.background = drawable
                messageContainer.setPadding(0, 0, 0, 3)
            }
        }
    }


    private val messagesList: ArrayList<MessagePojo>
    private var layoutInflater: LayoutInflater

    constructor(context: Context, messages: ArrayList<MessagePojo>) : super() {

        this.messagesList = messages
        this.layoutInflater = LayoutInflater.from(context)
        notifyDataSetChanged()

    }
    fun getMessagePosition(message: MessagePojo) : Int
    {
        return messagesList.indexOf(message)
    }

    fun clearMessages() {
        this.messagesList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View = layoutInflater.inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.setData(messagesList[position])
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }



}