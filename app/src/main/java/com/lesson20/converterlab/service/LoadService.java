package com.lesson20.converterlab.service;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.Toast;

import com.lesson20.converterlab.MainActivity;
import com.lesson20.converterlab.R;
import com.lesson20.converterlab.database.ConverterContentProvider;
import com.lesson20.converterlab.database.ConverterDBHelper;
import com.lesson20.converterlab.json.AsyncCurrencyLoader;
import com.lesson20.converterlab.json.CallbackLoading;
import com.lesson20.converterlab.models.OrganizationModel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoadService extends Service implements CallbackLoading {
    private static final String TAG = "LoadService";
    private NotificationManager mNM;

    private static Timer timer = new Timer();

    private int NOTIFICATION = R.string.load_service_started;

    @Override
    public void onSuccess(List<OrganizationModel> _organizationModelList) {
//        RVAdapter adapter = new RVAdapter(LoadService.this, _organizationModelList);
//        mRvBanks.setAdapter(adapter);
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
        }

        InsertTask insertTask = new InsertTask();
        insertTask.execute(contentValues);

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

    @Override
    public void onFailure(String errorMessage) {

    }

    public class LocalBinder extends Binder {
        public LoadService getService() {
            return LoadService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        startService();
//        showNotification();
    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 30 * 60 * 1000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            showNotification();
            new AsyncCurrencyLoader(LoadService.this, LoadService.this).execute();
        }
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        private void showNotification() {
            CharSequence text = getText(R.string.load_service_started);
            CharSequence textProgress = getText(R.string.load_service_progress);

            PendingIntent contentIntent = PendingIntent.getActivity(LoadService.this, 0,
                    new Intent(LoadService.this, MainActivity.class), 0);

            final Builder builder = new NotificationCompat.Builder(LoadService.this)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(textProgress)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis())
                    .setContentText(text)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);


            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            int incr;
                            for (incr = 0; incr <= 100; incr+=20) {
                                builder.setProgress(100, incr, false);
                                mNM.notify(NOTIFICATION, builder.build());
                                try {
                                    // Sleep for 5 seconds
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Log.d(TAG, "sleep failure");
                                }
                            }
                            builder.setContentText("Download complete")
                                    .setProgress(0,0,false);
                            mNM.notify(NOTIFICATION, builder.build());
                        }
                    }
            ).start();
//            notification.ledARGB = 0xff0000ff;
//            notification.ledOnMS = 1000;
//            notification.ledOffMS = 1000;
//            notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
//
//            mNM.notify(NOTIFICATION, notification);
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LoadService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);

        Toast.makeText(this, "Service stoped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();

}

