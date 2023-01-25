package com.example.tapbuy

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ListenerForegroundChat(mailSeller : String, nameObj : String) : Service() {

    val TAG = "ListenerBackgroundChat"

    val mailSeller = mailSeller
    val nameObj = nameObj

    var db = Firebase.firestore

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            while (true) {
                Log.e("Service", "Service is running...")
                try {
                    db.collection("Chat").document("${mailSeller}_${nameObj}")
                        .collection("chat").addSnapshotListener { snapshots, e ->
                        if (e != null) {
                            Log.d(TAG, "Cannot listen on firestore!!.")
                            return@addSnapshotListener
                        }

                        for (dc in snapshots!!.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    //Log.d("quoteListener", "New quote: ${dc.document.data}")
                                    createNotification("Chat")
                                }
                                DocumentChange.Type.MODIFIED -> {}          ////////////////////////////////////////////////////////////////////////////////////////////
                                DocumentChange.Type.REMOVED -> {}
                            }
                        }
                    }

                }
                catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        val CHANNELID = "In ascolto dei messaggi dai possibili compratori"
        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: Notification.Builder = Notification.Builder(this, CHANNELID)
            .setContentText("Service is running")
            .setContentTitle("Service enabled")
        startForeground(1001, notification.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}