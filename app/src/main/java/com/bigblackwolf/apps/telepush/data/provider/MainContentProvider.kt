package com.bigblackwolf.apps.telepush.data.provider

import android.content.Context
import com.bigblackwolf.apps.telepush.data.database.SQLiteDB

class MainContentProvider(context: Context) {
    val database = SQLiteDB(context).writableDatabase


}