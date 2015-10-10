package com.lesson20.converterlab.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.lesson20.converterlab.MainActivity;
import com.lesson20.converterlab.R;

public class ServiceBroadcast extends BroadcastReceiver {


        public void onReceive(Context context, Intent intent) {
            if (MainActivity.isOnline(context)) {
                Log.d(MainActivity.TAG, "MyReceiver#onReceive: intent: " + intent);

                Toast.makeText(context, "Converter Service started", Toast.LENGTH_SHORT).show();
                context.startService(new Intent(context, LoadService.class));
            }
        }
    }
