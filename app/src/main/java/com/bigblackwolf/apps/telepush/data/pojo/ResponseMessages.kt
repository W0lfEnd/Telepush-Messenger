package com.bigblackwolf.apps.telepush.data.pojo

data class ResponseMessages(
        val messages : List<MessagePojo>,
        val isMoreMessagesExist: Boolean
)