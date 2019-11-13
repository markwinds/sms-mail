package com.example.august

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList


class MySmsObserver(activitya: Context, handler:Handler): ContentObserver(handler) {

    lateinit var activity:Activity
    lateinit var language:String
    lateinit var hintPernission:String
    lateinit var hintPhoneMail:String
    //lateinit var lastSameMessageTime
    var address="address"
    var body="body"

    init {
        activity=activitya as Activity
        language = Locale.getDefault().getLanguage()
        hintPernission=activity.getResources().getString(R.string.getpermission)
        hintPhoneMail=activity.getResources().getString(R.string.smstitle)
        //lastSameMessageTime=0

    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        getSMSFromPhone()
        if(MainActivity.mailBody!=body || MainActivity.mailObject!=hintPhoneMail+address){ //如果相邻两条短信的内容不同则发送
            MainActivity.mailObject=hintPhoneMail+address
            MainActivity.mailBody=body
            MainActivity.sendMails()
        }
    }

    fun getSMSFromPhone(){
        var cr = activity.getContentResolver()
        var where = " date >  "+ (System.currentTimeMillis() - 10 * 60 * 1000)
        var cur:Cursor? = cr.query(SMS_INBOX, null, where, null, "date desc")
        if (cur!!.moveToFirst()) {
            address = cur.getString(cur.getColumnIndex("address"))
            body = cur.getString(cur.getColumnIndex("body"))
        }
    }

    fun registerSMSObserver(){
            getPermission()
            activity!!.getContentResolver().registerContentObserver(SMS_INBOX, true, this)
    }

    fun unregisterSMSObserver() {
            activity.getContentResolver().unregisterContentObserver(this)
    }

    fun checkPermission():Boolean{
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECEIVE_SMS
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_SMS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
            return false
        }
        return true
    }

    /**申请读短信的权限*/
    fun getPermission(){
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                PERMISSION_REQUEST_CODE)

        }
    }

    /**权限申请回调函数*/
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        if(requestCode== PERMISSION_REQUEST_CODE){
            var succeed=true
            for (temp in grantResults){
                if(temp!=PackageManager.PERMISSION_GRANTED)
                    succeed=false
            }
            if (succeed){
                //这里添加监听代码
            }else{
                toast(hintPernission)
            }
        }
    }

    fun toast(message:String){
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    companion object{
        val PERMISSION_REQUEST_CODE = 1116
        val SMS_INBOX = Uri.parse("content://sms/inbox")
    }
}