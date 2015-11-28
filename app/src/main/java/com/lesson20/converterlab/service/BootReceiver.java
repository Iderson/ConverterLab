package com.lesson20.converterlab.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    ServiceStarter alarm = new ServiceStarter();

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Toast.makeText(context, "Converter Service started", Toast.LENGTH_SHORT).show();
            alarm.setAlarm(context);
        }
    }
}
