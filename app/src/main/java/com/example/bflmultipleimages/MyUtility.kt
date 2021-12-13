package com.example.bflmultipleimages

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


object MyUtility {
    private const val NAME = "favorites"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

 fun addFavoriteItem(activity: Activity, favoriteItem: String) {

        var favoriteList = getStringFromPreferences(activity, null, "favorites")
        favoriteList = if (favoriteList != null) {
            "$favoriteList,$favoriteItem"
        } else {
            favoriteItem
        }
        putStringInPreferences(activity, favoriteList, "favorites")
    }

    fun getFavoriteList(activity: Activity): MutableList<String>? {
        val favoriteList = getStringFromPreferences(activity, null, "favorites")
        return convertStringToArray(favoriteList)
    }

    fun putStringInPreferences(activity: Activity?, nick: String?, key: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(key, nick)
        editor.commit()
    }

    private fun getStringFromPreferences(
        activity: Activity,
        defaultValue: String?,
        key: String
    ): String? {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(activity)
        return sharedPreferences.getString(key, defaultValue)
    }

    private fun convertStringToArray(str: String?): MutableList<String>? {
        return if(str!=null && str.isNotEmpty()) {
            str!!.split(",").toMutableList()
        }else{
            null
        }
    }

}