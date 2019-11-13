package com.example.august.defaultSMS

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SmsReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
//        AndroidInjection.inject(this, context)
//        Timber.v("onReceive")
//
//        Sms.Intents.getMessagesFromIntent(intent)?.let { messages ->
//            val subId = intent.extras?.getInt("subscription", -1) ?: -1
//
//            val pendingResult = goAsync()
//            receiveMessage.execute(ReceiveSms.Params(subId, messages)) { pendingResult.finish() }
//        }
    }

}