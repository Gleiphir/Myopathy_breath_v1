package com.digitalnature.myopathy_breath_v1

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.os.Handler
import android.os.HandlerThread
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable

class MainActivity : AppCompatActivity() {



    private lateinit var dataClient : DataClient
    private lateinit var textView : TextView
    private lateinit var node_id : String
    private lateinit var handlerThread : HandlerThread

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("-","setContentView Done")

        start_listen()

        textView = findViewById(R.id.DataShow)
        textView.setText(R.string.waiting)

    }

    fun start_listen() {
        handlerThread = object : HandlerThread("BackgroundThread") {
            override fun onLooperPrepared() {
                val handler = Handler(looper)
                val runnable = object : Runnable {
                    override fun run() {
                        get_data_task()
                        handler.postDelayed(this,50)
                    }
                }
                handler.postDelayed(runnable,300)
            }
        }
        handlerThread.start()
        //FIXME

    }

    @SuppressLint("VisibleForTests")
    fun get_data_task(){
        this.dataClient = Wearable.getDataClient(this)
        //val dataItemTask = dataClient.getDataItems(Uri.parse("wear://%s/health".format(node_id))) //wear://<node_id>/<path>
        val dataItemTask = dataClient.getDataItems(Uri.parse("wear://*/health"))
        dataItemTask.addOnSuccessListener { dataItems ->
            for (dataItem in dataItems) {
                Log.d("phone-got-data","$dataItem")
                if (dataItem.uri.path == "/health") {
                    val myData = DataMapItem.fromDataItem(dataItem).dataMap.getDouble("heartrate")
                    // do something with myData

                    textView.setText(String.format("Current Heartrate : %.1f",myData))
                }
            }
            dataItems.release()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quit()
    }




}