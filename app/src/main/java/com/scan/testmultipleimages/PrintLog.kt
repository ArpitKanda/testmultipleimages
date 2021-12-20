//package com.scan.testmultipleimages
//
//import android.app.Activity
//import android.graphics.Color
//import android.util.Log
//import android.view.View
//import android.widget.TextView
//import android.widget.Toast
//import com.google.android.material.snackbar.Snackbar
//import com.google.gson.Gson
//import java.lang.Exception
//
//object PrintLog {
//
//    fun response(TAG: String?, response: Any?) {
//        if (response != null) {
//            if (response is String) {
//                Log.e(TAG, (response as String?)!!)
//            } else if (response is Int) {
//                Log.e(
//                    TAG,
//                    App.gsonBuilder()?.setPrettyPrinting()?.create()?.toJson(response.toString())
//                )
//            } else if (response is Float) {
//                Log.e(
//                    TAG,
//                    App.gsonBuilder().setPrettyPrinting().create().toJson(response.toString())
//                )
//            } else {
//                Log.e(TAG, App.gsonBuilder().setPrettyPrinting().create().toJson(response))
//            }
//        } else {
//            Log.e(TAG, "Response is Null")
//        }
//    }
//
//
//    fun toast(activity: Activity?, message: Any?) {
//        if (message != null) {
//            if (message is String) {
//                val toast = Toast.makeText(activity, message as String?, Toast.LENGTH_SHORT)
//                toast.show()
//            } else if (message is Int) {
//                val toast = Toast.makeText(activity, message.toString(), Toast.LENGTH_SHORT)
//                toast.show()
//            } else if (message is Float) {
//                val toast = Toast.makeText(activity, Gson().toJson(message.toString()), Toast.LENGTH_SHORT)
//                toast.show()
//            } else {
//                val toast = Toast.makeText(activity, Gson().toJson(message.toString()), Toast.LENGTH_SHORT)
//                toast.show()
//            }
//        }else {
//            val toast = Toast.makeText(activity, "Response is Null", Toast.LENGTH_SHORT)
//            toast.show()
//        }
//    }
//
//
//    fun showsnackbar(activity: Activity, message: String?){
//        try {
//            val snack = Snackbar.make(
//                activity.findViewById(android.R.id.content),
//                message!!, Snackbar.LENGTH_SHORT
//            )
//            snack.duration = 2000
//            snack.setBackgroundTint(activity.resources.getColor(R.color.black_overlay))
//            val view = snack.view
//            val tv = view.findViewById<View>(R.id.snackbar_text) as TextView
//            tv.setTextColor(Color.WHITE) //change textColor
//            snack.show()
//        }catch (ex: Exception){
//            response("#SnackBar ",ex)
//        }
//    }
//}