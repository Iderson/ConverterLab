package com.iderson.currencyguide;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.iderson.currencyguide.adapter.RVOrgAdapter;
import com.iderson.currencyguide.fragments.GuideFragment;
import com.iderson.currencyguide.models.OrganizationModel;
import com.iderson.currencyguide.service.LoadCompleteReceiver;
import com.iderson.currencyguide.service.LoadService;
import com.iderson.currencyguide.service.ServiceStarter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = "Logs";
    private static final int MENU_MULTI_WINDOW = 0;
    private static final int MENU_NORMAL_WINDOW = 1;
    private static final String LOG_TAG = "MAIN_TAG";
    public static final int STATUS_START = 200;
    public static final int STATUS_FINISH = 300;
    public static final int PENDING_DATA = 100;
    public static final String PARAM_TASK = "pendingIntent";
    public static String BROADCAST_ACTION = "com.iderson.currencyguide";

    BroadcastReceiver br;

    private LoadCompleteReceiver myBroadcastReceiver;
    private List<OrganizationModel>     mBankList;
    private RecyclerView                mRvBanks;
    private Toolbar                     mToolbar;
    private SwipeRefreshLayout          mSwipeRefreshLayout;
    private String                      mQueryStr = "";
    private RVOrgAdapter                mRvAdapter = null;
    private SharedPreferences           mPrefs;
    private ServiceStarter mAlarm = null;
    private boolean                     doubleBackToExitPressedOnce = false;
    private String DATA = "SEARCH";
    public static final String PARAM_STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        if(getIntent().getExtras() != null && getIntent().getBooleanExtra("EXIT", true))
            finish();

        initUI();
        initBroadcast();
        startFragment("");
    }

    private void initBroadcast() {
        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                int task = intent.getIntExtra(PARAM_TASK, 0);
                int status = intent.getIntExtra(PARAM_STATUS, 0);
                Log.d(LOG_TAG, "onReceive: task = " + task + ", status = " + status);

                // Catching broadcast : loading data from server has started
                if (status == STATUS_START){
                    Toast.makeText(MainActivity.this, "Loading data started", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }

                if (status == STATUS_FINISH){
                    Toast.makeText(MainActivity.this, "Loading data finished", Toast.LENGTH_LONG).show();
                }
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);
    }

    public void startFragment(String _query) {
        GuideFragment fragment;
        fragment = new GuideFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DATA, _query);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commit();
    }

    private void initUI() {
//        Helper.admobLoader(this, getResources(), findViewById(R.id.adView));
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_primary));
        }

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
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                startFragment(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
                doubleBackToExitPressedOnce = false;
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
