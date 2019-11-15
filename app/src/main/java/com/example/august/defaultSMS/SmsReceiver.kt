package com.example.august.defaultSMS

import android.content.*
import android.net.Uri
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.telephony.TelephonyManager
import android.text.TextUtils


class SmsReceiver : BroadcastReceiver() {

    /**这个函数里面完成短信接受到后的工作*/
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras ?: return
        val smsExtras = extras.get(SmsConstant.PDUS) as Array<Any>?
        val contentResolver = context.contentResolver
        val smsUri = Uri.parse(SmsConstant.SMS_URI)
        for (smsExtra in smsExtras!!) {
            val smsBytes = smsExtra as ByteArray

            // FYI: createFromPdu(byte[] pdu) will be deprecated in near future.
            // Please refer Javadoc of SmsMessage#createFromPdu(byte[] pdu)
            val smsMessage = SmsMessage.createFromPdu(smsBytes)

            val body = smsMessage.messageBody
            val address = smsMessage.originatingAddress

            val values = ContentValues()
            values.put(SmsConstant.COLUMN_ADDRESS, address)
            values.put(SmsConstant.COLUMN_BODY, body)

            val uri = contentResolver.insert(smsUri, values) //将接受到的短信写入到数据库

            // implement notification
        }
    }

}