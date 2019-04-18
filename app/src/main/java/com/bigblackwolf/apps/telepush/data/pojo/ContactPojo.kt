package com.bigblackwolf.apps.telepush.data.pojo


data class ContactPojo(
        val email: String,
        val nickname: String,
        val name: String,
        var lastMessage: MessagePojo?
)