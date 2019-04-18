package com.bigblackwolf.apps.telepush.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

class Validator
{


    companion object {


        fun isEmail(email: String): Boolean {
            val EMAIL_PATTERN = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+\$"
            val pattern : Pattern = Pattern.compile(EMAIL_PATTERN)
            val matcher : Matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun isPassword(password: String,confirmPassword: String): Boolean {
            return (password.length > 6) && (password == confirmPassword)
        }
    }


}