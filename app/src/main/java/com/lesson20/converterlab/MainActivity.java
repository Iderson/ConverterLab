package com.lesson20.converterlab;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements CallbackLoading {

    private List<OrganizationModel> mBankList;
    private RecyclerView mRvBanks;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mBankList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        mRvBanks = (RecyclerView) findViewById(R.id.rvBanks_AM);
        mRvBanks.setLayoutManager(llm);
        new AsyncCurrencyLoader(this, MainActivity.this).execute();

//        testRV();
//        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void testRV() {
        for(int i = 0; i < 10; i++){
            mBankList.add(new OrganizationModel("id",
                    "Alfa Bank",
                    "Transcarpathian",
                    "Ungvar",
                    "Pushkinskaya, 10",
                    "911-66666",
                    "tttp://alfabank.com"));
        }
        RVAdapter adapter = new RVAdapter(MainActivity.this, mBankList);
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
    public void onSuccess(List<OrganizationModel> _organizationModelList) {
        RVAdapter adapter = new RVAdapter(MainActivity.this, _organizationModelList);
        mRvBanks.setAdapter(adapter);

    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }


}
