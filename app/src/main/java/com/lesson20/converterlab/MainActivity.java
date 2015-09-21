package com.lesson20.converterlab;

import android.app.SearchManager;
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
import android.widget.Toast;

import com.lesson20.converterlab.database.ConverterContentProvider;
import com.lesson20.converterlab.database.ConverterDBHelper;
import com.lesson20.converterlab.models.OrganizationModel;
import com.lesson20.converterlab.service.LoadService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private List<OrganizationModel>     mBankList;
    private RecyclerView                mRvBanks;
    private Toolbar                     mToolbar;

    private boolean                     mIsBound = false;
    private LoadService                 mBoundService;
    private SwipeRefreshLayout          mSwipeRefreshLayout;
    private String                      mQueryStr = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        doBindService();
        initUI();
    }

    private void initUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        mRvBanks = (RecyclerView) findViewById(R.id.rvBanks_AM);
        mRvBanks.setLayoutManager(llm);
        mRvBanks.setItemAnimator(new DefaultItemAnimator());
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swpRefreshLayout_AM);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });


        getSupportLoaderManager().initLoader(0, null, this);
    }

    void refreshItems() {
        getSupportLoaderManager().initLoader(0, null, this);
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
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
        bindService(intent, mConnection, 0);
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


    private void populateRV(List<OrganizationModel> _list) {
        RVOrgAdapter adapter = new RVOrgAdapter(MainActivity.this, _list);
        mRvBanks.setAdapter(adapter);
    }

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
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
