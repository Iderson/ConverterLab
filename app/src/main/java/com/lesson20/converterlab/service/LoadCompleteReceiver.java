package com.lesson20.converterlab.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lesson20.converterlab.MainActivity;

/**
 * Listener when async loading data from server finished and ready
 * to show on the main screen
 */
public class LoadCompleteReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("INFO", "Load completed");
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//todo: if opened
        context.startActivity(intent1);
    }
}
