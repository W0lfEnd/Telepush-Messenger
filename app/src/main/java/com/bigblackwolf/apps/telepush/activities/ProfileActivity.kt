package com.bigblackwolf.apps.telepush.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bigblackwolf.apps.telepush.R

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    fun backToPreviousActivity(view: View)
    {
        finish()
    }
}
