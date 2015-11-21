package com.lesson20.converterlab.service;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.lesson20.converterlab.MainActivity;
import com.lesson20.converterlab.R;
import com.lesson20.converterlab.database.ConverterContentProvider;
import com.lesson20.converterlab.database.ConverterDBHelper;
import com.lesson20.converterlab.json.AsyncCurrencyLoader;
import com.lesson20.converterlab.json.CallbackLoading;
import com.lesson20.converterlab.models.CurrencyModel;
import com.lesson20.converterlab.models.OrganizationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoadService extends Service implements CallbackLoading {
    private static final String TAG = "LoadService";

    private static Timer timer = new Timer();
    private static boolean aIsStarted;
    private final IBinder mBinder = new LocalBinder();
    private int NOTIFICATION = R.string.load_service_started;

    @Override
    public void onSuccess(List<OrganizationModel> _organizationModelList) {
        ContentValues[]contentValues = new ContentValues[_organizationModelList.size()];
        for (int i = 0; i < _organizationModelList.size(); i++) {

            contentValues[i] = new ContentValues();
            contentValues[i].put(ConverterDBHelper.FIELD_ROW_ID, _organizationModelList.get(i).getId());
            contentValues[i].put(ConverterDBHelper.FIELD_TITLE, _organizationModelList.get(i).getTitle());
            contentValues[i].put(ConverterDBHelper.FIELD_REGION, _organizationModelList.get(i).getRegion());
            contentValues[i].put(ConverterDBHelper.FIELD_CITY, _organizationModelList.get(i).getCity());
            contentValues[i].put(ConverterDBHelper.FIELD_PHONE, _organizationModelList.get(i).getPhone());
            contentValues[i].put(ConverterDBHelper.FIELD_ADDRESS, _organizationModelList.get(i).getAddress());
            contentValues[i].put(ConverterDBHelper.FIELD_LINK, _organizationModelList.get(i).getLink());


            ArrayList<CurrencyModel> list = _organizationModelList.get(i).getCurrencies();
            for (int j = 0; j < list.size(); j++) {
                contentValues[i].put(list.get(j).getName() + "_ASK", list.get(j).getCurrency().getAsk());
                contentValues[i].put(list.get(j).getName() + "_BID", list.get(j).getCurrency().getBid());
                contentValues[i].put(list.get(j).getName() + "_FULL", list.get(j).getFullName());
            }
        }

        InsertTask insertTask = new InsertTask();
        insertTask.execute(contentValues);
        showNotification();
    }

    @Override
    public void onFailure(String errorMessage) {

    }

    @Override
    public void onCreate() {
        startService();
    }

    private void startService()
    {
        if(!aIsStarted) {
            aIsStarted = true;
            timer.scheduleAtFixedRate(new mainTask(), 0, 30 * 60 * 1000);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CharSequence text = getText(R.string.load_service_started);
        CharSequence textProgress = getText(R.string.load_service_progress);
        Intent intent = new Intent(LoadService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(LoadService.this, 0,
                intent, 0);

        final Builder builder = new NotificationCompat.Builder(LoadService.this)
                .setSmallIcon(R.drawable.ic_push)
                .setContentTitle(textProgress)
                .setTicker(text)
                .setContentText(text)
                .setSound(alarmSound)
                .setAutoCancel(true);

        // Creates an Intent that shows the title and a description of the feed
        Intent resultIntent = new Intent(LoadService.this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(LoadService.this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        for (int incr = 0; incr <= 100; incr += 20) {
            builder.setProgress(100, incr, false);
            try {
                // Sleep for 5 seconds
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "sleep failure");
            }
        }
        builder.setContentText("Download complete")
                .setProgress(0, 0, false);
        mNotificationManager.notify(NOTIFICATION, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LoadService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Currency Service stoped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        return mbinder;
        return null;
    }

    private class InsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {
            for (ContentValues contentValue : contentValues) {
                getContentResolver().insert(ConverterContentProvider.CONTENT_URI, contentValue);
            }

            return null;
        }
    }

    public class LocalBinder extends Binder {
        public LoadService getService() {
            return LoadService.this;
        }
    }

    private class mainTask extends TimerTask {
        public void run() {
            new AsyncCurrencyLoader(LoadService.this, LoadService.this).execute();
        }


    }

}

