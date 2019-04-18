package com.bigblackwolf.apps.telepush.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SQLiteDB : SQLiteOpenHelper {
    companion object {
        const val DATABASE_NAME = "TelepushDB"
        abstract class TableUSER{
            companion object {
                const val TABLE_NAME = "USER"
                const val USER_ID = "USER_ID"
                const val ID_USER_ON_SERVER = "ID_USER_ON_SERVER"
                const val EMAIL = "EMAIL"
                const val LOGIN = "LOGIN"
                const val NICKNAME = "NICKNAME"
                const val USER_ICON_LOCAL_PATH = "USER_ICON_LOCAL_PATH"
            }
        }
        abstract class TableUSER_CONTACTS{
            companion object {
                const val TABLE_NAME = "USER_CONTACTS"
                const val ID_USER_CONTACTS = "ID_USER_CONTACTS"
                const val ID_USER_CONTACT_OWNER = "ID_USER_CONTACT_OWNER"
                const val ID_USER_CONTACT = "ID_USER_CONTACT"
                const val ID_MESSAGE_LAST_SENDED = "ID_MESSAGE_LAST_SENDED"
            }
        }
        abstract class TableMESSAGE{
            companion object {
                const val TABLE_NAME = "MESSAGE"
                const val ID_MESSAGE = "ID_MESSAGE"
                const val ID_USER_SENDER = "ID_USER_SENDER"
                const val ID_USER_TARGET = "ID_USER_TARGET"
                const val MESSAGE_TEXT = "MESSAGE_TEXT"
                const val DATE_OF_SENDING = "DATE_OF_SENDING"
                const val DATE_OF_ARRIVE = "DATE_OF_ARRIVE"
            }
        }



    }

    val database : SQLiteDatabase

    constructor(context:Context) : super(context, DATABASE_NAME, null, 1)
    {
        database = this.writableDatabase
    }

    override fun onCreate(db: SQLiteDatabase) {
        //creating User table
        db.execSQL("CREATE TABLE USER" +
                "(" +
                    "USER_ID              INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "ID_USER_ON_SERVER    INTEGER NOT NULL UNIQUE," +
                    "EMAIL                VARCHAR UNIQUE NOT NULL," +
                    "LOGIN                VARCHAR UNIQUE NOT NULL," +
                    "NICKNAME             VARCHAR NOT NULL," +
                    "USER_ICON_LOCAL_PATH VARCHAR NOT NULL" +
                ")"
        )

        //creating Message table
        db.execSQL("CREATE TABLE MESSAGE" +
                "(" +
                    "ID_MESSAGE      INTEGER  PRIMARY KEY NOT NULL," +
                    "ID_USER_SENDER  INTEGER  REFERENCES USER (USER_ID) NOT NULL," +
                    "ID_USER_TARGET  INTEGER  REFERENCES USER (USER_ID) NOT NULL," +
                    "MESSAGE_TEXT    TEXT     NOT NULL," +
                    "DATE_OF_SENDING DATETIME NOT NULL," +
                    "DATE_OF_ARRIVE  DATETIME" +
                ")"
        )

        //creating User_Contacts table
        db.execSQL("CREATE TABLE USER_CONTACTS " +
                "(" +
                    "ID_USER_CONTACTS       INTEGER PRIMARY KEY NOT NULL," +
                    "ID_USER_CONTACT_OWNER  INTEGER NOT NULL REFERENCES USER (USER_ID)," +
                    "ID_USER_CONTACT        INTEGER REFERENCES USER (USER_ID) NOT NULL," +
                    "ID_MESSAGE_LAST_SENDED INTEGER REFERENCES MESSAGE (ID_MESSAGE) " +
                ")"
        )


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS USER")
        db.execSQL("DROP TABLE IF EXISTS USER_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS MESSAGE")
        onCreate(db)
    }



}