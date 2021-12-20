package com.scan.testmultipleimages
import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlin.jvm.Synchronized
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        MyUtility.init(this)
    }
    companion object {
        @get:Synchronized
        var instance: App? = null
            private set
        var gson: Gson? = null
            get() {
                if (field == null) {
                    field = Gson()
                }
                return field
            }
        private var gsonBuilder: GsonBuilder? = null
        var isActivityVisible = false
        val context: Context?
            get() = instance

        fun gsonBuilder(): GsonBuilder? {
            if (gsonBuilder == null) {
                gsonBuilder = GsonBuilder()
            }
            return gsonBuilder
        }

        fun activityResumed() {
            isActivityVisible = true
        }

        fun activityPaused() {
            isActivityVisible = false
        }
    }


}