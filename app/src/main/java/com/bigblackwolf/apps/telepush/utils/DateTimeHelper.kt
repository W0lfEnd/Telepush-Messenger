package com.bigblackwolf.apps.telepush.utils

import android.util.Log
import com.bigblackwolf.apps.telepush.activities.ChatActivity
import java.text.SimpleDateFormat
import java.util.*

class DateTimeHelper{
    companion object {
        var serverdateFormat = "yyyy-MM-dd HH:mm:ss"
        fun convertServerDateToUserTimeZone(serverDate: String): String {
            var ourdate: String
            try {
                val formatter = SimpleDateFormat(serverdateFormat, Locale.UK)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value = formatter.parse(serverDate)
                val timeZone = TimeZone.getDefault()
                val dateFormatter = SimpleDateFormat(serverdateFormat, Locale.UK) //this format changeable
                dateFormatter.timeZone = timeZone
                ourdate = dateFormatter.format(value)
            } catch (e: Exception) {
                ourdate = "0000-00-00 00:00:00"
            }

            return ourdate
        }

        fun convertUserTimeZoneToServerDate(serverDate: String): String {
            var ourdate: String
            try {
                val formatter = SimpleDateFormat(serverdateFormat, Locale.UK)
                formatter.timeZone = TimeZone.getDefault()
                val value = formatter.parse(serverDate)
                val timeZone = TimeZone.getTimeZone("UTC")
                val dateFormatter = SimpleDateFormat(serverdateFormat, Locale.UK) //this format changeable
                dateFormatter.timeZone = timeZone
                ourdate = dateFormatter.format(value)
            } catch (e: Exception) {
                ourdate = "0000-00-00 00:00:00"
            }

            return ourdate
        }

        fun getCurrentLocalTime() : String{
            val formatter = SimpleDateFormat(serverdateFormat, Locale.UK)
            formatter.timeZone = TimeZone.getDefault()
            return formatter.format(Date())
        }
        fun getCurrentServerTime():String{
            val formatter = SimpleDateFormat(serverdateFormat, Locale.UK)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(Calendar.getInstance().time)
        }
        fun getTimeDayMonth(date:String):String
        {
            val dateDate = stringToDate(date)
            val formatter = SimpleDateFormat("HH:mm dd/MM", Locale.UK)
            return formatter.format(dateDate)
        }

        fun getTime(date:String):String
        {
            val dateDate = stringToDate(date)
            val formatter = SimpleDateFormat("HH:mm", Locale.UK)
            return formatter.format(dateDate)
        }
        fun dateToString(date:Date):String{
            return SimpleDateFormat(serverdateFormat).format(date)
        }

        fun getFormatedDate(date:String):String {
            val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateNow = Calendar.getInstance().time
            val dateDate = stringToDate(date)
            val res =  if(yearMonthDayFormat.format(dateNow) == yearMonthDayFormat.format(dateDate))
                getTime(date)
            else
                getTimeDayMonth(date)
            return res
        }
        fun stringToDate(date:String):Date{
            return SimpleDateFormat(serverdateFormat).parse(date)
        }
    }
}