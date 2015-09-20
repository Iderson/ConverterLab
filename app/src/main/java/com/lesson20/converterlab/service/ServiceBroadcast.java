package com.lesson20.converterlab.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ServiceBroadcast extends BroadcastReceiver {

        final String LOG_TAG = "myLogs";

        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive " + intent.getAction());

            Toast.makeText(context, "Service started", Toast.LENGTH_SHORT).show();
            context.startService(new Intent(context, LoadService.class));
        }
    }
