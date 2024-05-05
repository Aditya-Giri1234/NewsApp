package com.aditya.newsapp.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Message
import android.telecom.ConnectionService
import android.util.Log
import android.widget.Toast

object Helper {

    private var toast:Toast?=null
    fun customLog(tag:String,message: String){
        Log.e(tag,message)
    }

    fun customToast(context: Context , message: String){
        if (toast != null) {
            toast?.cancel();
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show();
    }

    fun isInternetAvailable(app:Application):Boolean{
        val connectivityManager=app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork=connectivityManager.activeNetwork ?: return false
        val networkCapabilities=connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when{
            networkCapabilities.hasTransport(TRANSPORT_WIFI)-> true
            networkCapabilities.hasTransport(TRANSPORT_CELLULAR)-> true
            networkCapabilities.hasTransport(TRANSPORT_ETHERNET)-> true
            else-> false
        }
    }
}