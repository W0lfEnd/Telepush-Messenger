package com.bigblackwolf.apps.telepush.data.network.api

import com.bigblackwolf.apps.telepush.data.pojo.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*

interface ITelepushApi  {

    @GET("message/")
    fun getMessage(
            @Query("user") userEmail: String,
            @Query("from") fromMessageNumber: Int,
            @Query("to") toMessageNumber: Int,
            @Header("Authorization") authHeader: String
    ): Call<ResponseMessages>

    @GET("contact/")
    fun getContacts(
            @Header("Authorization") authHeader: String
    ): Call<List<ContactPojo>>

    @GET("contact/")
    fun getFindUsers(
            @Query("search") search: String,
            @Header("Authorization") authHeader: String
    ): Call<List<UserPojo>>

    @FormUrlEncoded
    @POST("contact/")
    fun addUserToContacts(
            @Field("email") userEmail: String,
            @Header("Authorization") authHeader: String
    ): Call<ResponseStatus>


    @DELETE("contact/{email}")
    fun deleteUserFromContacts(
            @Path("email") userEmail: String,
            @Header("Authorization") authHeader: String
    ): Call<ResponseStatus>

    @FormUrlEncoded
    @POST("message/")
    fun sendMessage(
            @Field("email") userEmail: String,
            @Field("message_text") message: String,
            @Header("Authorization") authHeader: String
    ): Call<ResponseStatus>

    @FormUrlEncoded
    @POST("user/")
    fun registerUser(
            @Field("email") userEmail: String
    ): Call<ResponseStatus>

    @FormUrlEncoded
    @PUT("user/token/new/")
    fun updateUserToken(
            @Field("token") newToken: String,
            @Header("Authorization") authHeader: String
    ): Call<ResponseStatus>

}