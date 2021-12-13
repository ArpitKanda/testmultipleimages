package com.example.bflmultipleimages

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


object MyUtility {


    fun addFavoriteItem(context:Context, favoriteItem: String) {
        var favoriteList = getStringFromPreferences(context, null, "favorites")
        favoriteList = if (favoriteList != null) {
            "$favoriteList,$favoriteItem"
        } else {
            favoriteItem
        }
        putStringInPreferences(context, favoriteList, "favorites")
    }

    fun getFavoriteList(context: Context): MutableList<String>? {
        val favoriteList = getStringFromPreferences(context, null, "favorites")
        return convertStringToArray(favoriteList)
    }

    fun putStringInPreferences(context: Context?, nick: String?, key: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(key, nick)
        editor.commit()
    }

     fun getStringFromPreferences(
        context: Context,
        defaultValue: String?,
        key: String
    ): String? {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, defaultValue)
    }

    fun convertStringToArray(str: String?): MutableList<String>? {
        return str!!.split(",").toMutableList()
    }



}