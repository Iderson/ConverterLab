package com.lesson20.converterlab;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.lesson20.converterlab.adapter.RVOrgAdapter;
import com.lesson20.converterlab.database.ConverterContentProvider;
import com.lesson20.converterlab.database.ConverterDBHelper;
import com.lesson20.converterlab.models.OrganizationModel;
import com.lesson20.converterlab.service.Helper;
import com.lesson20.converterlab.service.LoadCompleteReceiver;
import com.lesson20.converterlab.service.LoadService;
import com.lesson20.converterlab.service.ServiceStarter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String TAG = "Logs";
    private LoadCompleteReceiver myBroadcastReceiver;


    private List<OrganizationModel>     mBankList;
    private RecyclerView                mRvBanks;
    private Toolbar                     mToolbar;

    private SwipeRefreshLayout          mSwipeRefreshLayout;
    private String                      mQueryStr = "";
    private RVOrgAdapter                mRvAdapter = null;
    private SharedPreferences           mPrefs;
    private ServiceStarter              mAlarm = null;
    private boolean                     doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        if(getIntent().getExtras() != null && getIntent().getBooleanExtra("EXIT", true))
            finish();

        initUI();
    }

    private void initUI() {
        Helper.admobLoader(this, getResources(), findViewById(R.id.adView));
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_primary));
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swpRefreshLayout_AM);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean firstStart = mPrefs.getBoolean("firstStart", true);

        if (firstStart) {
            mAlarm = new ServiceStarter();
            SharedPreferences.Editor editor = mPrefs.edit();

            mAlarm.setAlarm(this);
            editor.putBoolean("firstStart", false);
            // commits your edits
            editor.commit();
        }

        myBroadcastReceiver = new LoadCompleteReceiver();

        // register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(
                LoadService.ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);
        //get data from database
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void updateList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(Helper.isOnline(MainActivity.this, false, true)) {
                            Intent intent = new Intent(MainActivity.this, LoadService.class);
                            startService(intent);
                        }
//                        initUI();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void populateRV(List<OrganizationModel> _list) {
        if (mRvAdapter != null) mRvAdapter.notifyDataSetChanged();
        else {
            LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
            mRvBanks = (RecyclerView) findViewById(R.id.rvBanks_AM);
            mRvBanks.setLayoutManager(llm);
            mRvBanks.setItemAnimator(new DefaultItemAnimator());
        }
        mRvAdapter = new RVOrgAdapter(MainActivity.this, _list);
        mRvBanks.setAdapter(mRvAdapter);
    }

    /*
    * Prepare and handle search operation
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQueryStr = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.action_share) item.setVisible(false);
            else if (item.getItemId() == R.id.action_search) item.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /*
    * Prepere handling database operation
    */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ConverterContentProvider.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

    /*
    * Database listener to populate currency list
    */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String id = "";
        int count = 0;
        String title = "";
        String region = "";
        String city = "";
        String phone = "";
        String address = "";
        String link = "";

        if (data == null) {
            count = 0;
        } else {
            count = data.getCount();
            data.moveToFirst();
            mBankList = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                id = data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_ROW_ID));
                title = data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_TITLE));
                region = data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_REGION ));
                city = data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_CITY ));
                phone = data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_PHONE ));
                address = data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_ADDRESS ));
                link = "" + data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_LINK));
                if((!mQueryStr.equals("") &&
                        (title.toLowerCase().contains(mQueryStr.toLowerCase().trim()) ||
                                city.toLowerCase().contains(mQueryStr.toLowerCase().trim()) ||
                                region.toLowerCase().contains(mQueryStr.toLowerCase().trim())))
                        || mQueryStr.equals(""))
                mBankList.add(new OrganizationModel(
                        id,
                        title,
                        region,
                        city,
                        phone,
                        address,
                        link));
                data.moveToNext();
            }
            populateRV(mBankList);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            unregisterReceiver(myBroadcastReceiver);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAlarm != null && mAlarm.isOrderedBroadcast())
            mAlarm.cancelAlarm(this);
        try {
            unregisterReceiver(myBroadcastReceiver);
        } catch (IllegalArgumentException ie){
            ie.printStackTrace();
        }
    }
}
