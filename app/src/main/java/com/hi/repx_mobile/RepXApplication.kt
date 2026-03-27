package com.hi.repx_mobile

import android.app.Application
import com.hi.repx_mobile.data.network.ApiClient

class RepXApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
    }
}