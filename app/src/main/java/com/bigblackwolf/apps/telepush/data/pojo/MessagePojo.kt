package com.bigblackwolf.apps.telepush.data.pojo


data class MessagePojo(
    val sender_email: String,
    val message_text: String,
    val date_of_sending: String,
    var isDontSent : Boolean = false
)