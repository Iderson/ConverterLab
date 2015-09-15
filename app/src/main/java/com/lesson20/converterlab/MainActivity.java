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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<BankModel> mBankList;
    private RecyclerView mRvBanks;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        mBankList.add(new BankModel(
                "Text1",
                "Text2",
                "Text3",
                "Text4",
                "Text5"));
        populateRV(mBankList);

    }

    private void initUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mBankList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        mRvBanks = (RecyclerView) findViewById(R.id.rvBanks_AM);
        mRvBanks.setLayoutManager(llm);

//        getSupportLoaderManager().initLoader(0, null, this);
//        mRvBanks.addOnItemTouchListener(new RecyclerClickListener(
//                MainActivity.this,
//                mRvBanks,
//                new RecyclerClickListener.OnItemLongClickListener() {
//                    @Override
//                    public void onItemLongClick(View view, int position) {
//                    }
//
//                    @Override
//                    public void onItemClick(View view, int position) {
////                        showNotification(mBankList.get(position));
//                    }
//                }));
    }

    private void populateRV(List<BankModel> _bankModelList) {
        RVAdapter adapter = new RVAdapter(MainActivity.this, _bankModelList);
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

}
