package com.bigblackwolf.apps.telepush.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SQLiteDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DB_VERSION) {
    companion object {
        const val DATABASE_NAME = "TelepushDB"
        const val DB_VERSION = 2
        abstract class TableUSER{
            companion object {
                const val TABLE_NAME = "USER"
                const val USER_ID = "_id"
                const val ID_USER_ON_SERVER = "ID_USER_ON_SERVER"
                const val EMAIL = "EMAIL"
                const val LOGIN = "LOGIN"
                const val NICKNAME = "NICKNAME"
                const val USER_ICON_LOCAL_PATH = "USER_ICON_LOCAL_PATH"
            }
        }
        abstract class TableMESSAGE{
            companion object {
                const val TABLE_NAME = "MESSAGE"
                const val ID_MESSAGE = "_id"
                const val ID_USER_SENDER = "ID_USER_SENDER"
                const val ID_USER_TARGET = "ID_USER_TARGET"
                const val MESSAGE_TEXT = "MESSAGE_TEXT"
                const val DATE_OF_SENDING = "DATE_OF_SENDING"
                const val DATE_OF_ARRIVE = "DATE_OF_ARRIVE"
            }
        }



    }

    val database : SQLiteDatabase = this.writableDatabase

    override fun onCreate(db: SQLiteDatabase) {
        //creating User table
        db.execSQL("CREATE TABLE ${TableUSER.TABLE_NAME}" +
                "(" +
                "${TableUSER.USER_ID}              INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "${TableUSER.ID_USER_ON_SERVER}    INTEGER NOT NULL UNIQUE," +
                "${TableUSER.EMAIL}                NVARCHAR(50) UNIQUE NOT NULL," +
                "${TableUSER.LOGIN}                NVARCHAR(30) UNIQUE NOT NULL," +
                "${TableUSER.NICKNAME}             NVARCHAR(30) NOT NULL," +
                "${TableUSER.USER_ICON_LOCAL_PATH} NVARCHAR(64) NOT NULL" +
                ")"
        )

        //creating Message table
        db.execSQL("CREATE TABLE ${TableMESSAGE.TABLE_NAME}" +
                "(" +
                "${TableMESSAGE.ID_MESSAGE}      INTEGER  PRIMARY KEY NOT NULL," +
                "${TableMESSAGE.ID_USER_SENDER}  INTEGER  REFERENCES USER (ID_USER_ON_SERVER) NOT NULL," +
                "${TableMESSAGE.ID_USER_TARGET}  INTEGER  REFERENCES USER (ID_USER_ON_SERVER) NOT NULL," +
                "${TableMESSAGE.MESSAGE_TEXT}    TEXT     NOT NULL," +
                "${TableMESSAGE.DATE_OF_SENDING} VARCHAR(24) NOT NULL," +
                "${TableMESSAGE.DATE_OF_ARRIVE}  VARCHAR(24)" +
                ")"
        )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS USER")
        db.execSQL("DROP TABLE IF EXISTS MESSAGE")
        onCreate(db)
    }



}