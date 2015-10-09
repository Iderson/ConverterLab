package com.lesson20.converterlab.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lesson20.converterlab.MainActivity;

public class ServiceBroadcast extends BroadcastReceiver {


        public void onReceive(Context context, Intent intent) {
            Log.d(MainActivity.TAG, "MyReceiver#onReceive: intent: " + intent);

            Toast.makeText(context, "Service started", Toast.LENGTH_SHORT).show();
            context.startService(new Intent(context, LoadService.class));
        }
    }
