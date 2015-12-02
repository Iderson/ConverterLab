package com.iderson.currencyguide.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iderson.currencyguide.MainActivity;

import java.util.List;

/**
 * Listener when async loading data from server finished and ready
 * to show on the main screen
 */
public class LoadCompleteReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("INFO", "Load completed");
        try {

            // Using ACTIVITY_SERVICE with getSystemService(String)
            // to retrieve a ActivityManager for interacting with the global system state.

            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);

            // Return a list of the tasks that are currently running,
            // with the most recent being first and older ones after in order.
            // Taken 1 inside getRunningTasks method means want to take only
            // top activity from stack and forgot the olders.

            List<ActivityManager.RunningTaskInfo> alltasks = am
                    .getRunningTasks(1);

            //
            for (ActivityManager.RunningTaskInfo aTask : alltasks) {
                if (aTask.topActivity.getClassName().equals(
                        context.getPackageName() + ".MainActivity")){
                    Intent intent1 = new Intent(context, MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//todo: if opened
                    context.startActivity(intent1);
                }
            }

        } catch (Throwable t) {
            Log.i(MainActivity.TAG, "Throwable caught: "
                    + t.getMessage(), t);
        }
    }
}
