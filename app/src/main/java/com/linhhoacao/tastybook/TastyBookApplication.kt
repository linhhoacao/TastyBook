package com.linhhoacao.tastybook

import android.app.Application
import com.google.firebase.FirebaseApp

class TastyBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}