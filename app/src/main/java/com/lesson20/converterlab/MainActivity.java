package com.lesson20.converterlab;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private List<OrganizationModel> mBankList;
    private RecyclerView mRvBanks;
    private Toolbar mToolbar;

    private boolean mIsBound = false;
    private LoadService mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((LoadService.LocalBinder)service).getService();

            Toast.makeText(MainActivity.this, R.string.load_service_started,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
            Toast.makeText(MainActivity.this, R.string.load_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        Intent intent = new Intent(this, LoadService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        startService(intent);
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doBindService();
        initUI();
    }

    private void initUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        mRvBanks = (RecyclerView) findViewById(R.id.rvBanks_AM);
        mRvBanks.setLayoutManager(llm);


//        testRV();
        getSupportLoaderManager().initLoader(0, null, this);
    }


    private void populateRV(List<OrganizationModel> _list) {
        RVAdapter adapter = new RVAdapter(MainActivity.this, _list);
        mRvBanks.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbar.inflateMenu(R.menu.menu_main);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //todo: Configure the search info and add event listeners

        return super.onCreateOptionsMenu(menu);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_search)
        {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ConverterContentProvider.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

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
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
