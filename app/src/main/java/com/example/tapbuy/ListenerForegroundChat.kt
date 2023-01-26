package com.example.tapbuy

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ListenerForegroundChat(mailSeller : String, nameObj : String) : Service() {

    val TAG = "ListenerBackgroundChat"
    var mailSeller : String
    var nameObj : String

    init {
        this.mailSeller = mailSeller
        this.nameObj = nameObj
    }

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
                                    createNotification("Chat", dc.document.id)
                                }
                                DocumentChange.Type.MODIFIED -> {}
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

    private fun createNotification(channelId: String, idDoc : String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, channelId, importance)
        notificationManager.createNotificationChannel(channel)

        val resultIntent = Intent(this, ChatUsers::class.java )
        resultIntent.putExtra("buyer_id_document", idDoc )

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notification = Notification.Builder(this, channelId)
            .setContentTitle(getString(R.string.received_message))
            .setContentText("${getString(R.string.message_for)} ${nameObj}")
            .setChannelId(channelId)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify((Math.random() % 10000).toInt(), notification)


    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}