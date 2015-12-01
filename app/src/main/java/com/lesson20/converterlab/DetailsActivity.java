package com.lesson20.converterlab;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.lesson20.converterlab.adapter.RVCurrAdapter;
import com.lesson20.converterlab.database.ConverterDBHelper;
import com.lesson20.converterlab.models.AskBidModel;
import com.lesson20.converterlab.models.CurrencyModel;
import com.lesson20.converterlab.models.OrganizationModel;
import com.lesson20.converterlab.service.Helper;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private ShareActionProvider mShareActionProvider;
    private String mID = "";

    private TextView            mTvBankName;
    private TextView            mTvRegion;
    private TextView            mTvAddressName;
    private TextView            mTvCity;
    private TextView            mTvPhoneName;
    private TextView            mTvLink;
    private RecyclerView        mRvCurrencies;
    private OrganizationModel   mOrganizationModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RVCurrAdapter mRvAdapter;
    private ConverterDBHelper mDBOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        mID = intent.getStringExtra("_id");
        mDBOpenHelper = new ConverterDBHelper(this);
        initUI();
        getFromDB();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_AD);
        Helper.admobLoader(this, getResources(), findViewById(R.id.adView));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_primary));
        }

        LinearLayoutManager llm = new LinearLayoutManager(DetailsActivity.this);

        mRvCurrencies           = (RecyclerView) findViewById(R.id.rvCurrencies_AD);
        mTvBankName             = (TextView) findViewById(R.id.tvBankName_AD);
        mTvRegion               = (TextView) findViewById(R.id.tvRegion_AD);
        mTvCity                 = (TextView) findViewById(R.id.tvCity_AD);
        mTvAddressName          = (TextView) findViewById(R.id.tvAddressName_AD);
        mTvPhoneName            = (TextView) findViewById(R.id.tvPhoneName_AD);
        mTvLink                 = (TextView) findViewById(R.id.tvLink_AD);

        populateInfo();
        mRvCurrencies.setLayoutManager(llm);
        findViewById(R.id.fabMap_AD).setOnClickListener(this);
        findViewById(R.id.fabLink_AD).setOnClickListener(this);
        findViewById(R.id.fabPhone_AD).setOnClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swpRefreshLayout_AD);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateList();
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbar.inflateMenu(R.menu.menu_main);
        MenuItem shareItem = menu.findItem(R.id.action_share);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.action_share) item.setVisible(true);
            else if (item.getItemId() == R.id.action_search) item.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                return true;
            case R.id.action_share:
                ShareDialog dialog = new ShareDialog();
                dialog.setBankInfo(mOrganizationModel);
                dialog.show(getFragmentManager(), "Edit Contact");

                return true;
        }

        return super.onOptionsItemSelected(item);
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
            }
        }).start();
        mSwipeRefreshLayout.setRefreshing(false);
        getFromDB();
    }

    private void getFromDB(){
        Cursor cursor = mDBOpenHelper.getCurrency(mID);

        if (cursor != null) {
            cursor.moveToFirst();
            ArrayList<CurrencyModel> currencyModels = new ArrayList<>();

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                CurrencyModel curr = new CurrencyModel();
                if(!cursor.isNull(i) && cursor.getColumnName(i).contains("_ASK")) {
                    try {
                        AskBidModel askBidModel = new AskBidModel();
                        String name = cursor.getColumnName(i).replace("_ASK", "");
                        curr.setName(name);
                        askBidModel.setAsk(Double.valueOf(cursor.getString(i)));
                        askBidModel.setBid(Double.valueOf(cursor.getString(cursor.getColumnIndex(curr.getName() + "_BID"))));
                        curr.setFullName(cursor.getString(cursor.getColumnIndex(curr.getName() + "_FULL")));
                        curr.setCurrency(askBidModel);
                        currencyModels.add(curr);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }


                populateRV(currencyModels);
            }
            if(cursor != null) cursor.close();

        }
    }

    private void populateRV(ArrayList<CurrencyModel> _list) {
        if (mRvAdapter != null) mRvAdapter.notifyDataSetChanged();
        else mRvAdapter = new RVCurrAdapter(DetailsActivity.this, _list);
        mRvCurrencies.setAdapter(mRvAdapter);
    }

    private void populateInfo() {
        Cursor cursor = mDBOpenHelper.getOrganizationDetail(mID);

        if (cursor != null) {
            cursor.moveToFirst();
            String id = "" + cursor.getString(cursor.getColumnIndex(ConverterDBHelper.FIELD_ROW_ID));
            String title = "" + cursor.getString(cursor.getColumnIndex(ConverterDBHelper.FIELD_TITLE));
            String region = "" + cursor.getString(cursor.getColumnIndex(ConverterDBHelper.FIELD_REGION));
            String city = "" + cursor.getString(cursor.getColumnIndex(ConverterDBHelper.FIELD_CITY));
            String phone = "" + cursor.getString(cursor.getColumnIndex(ConverterDBHelper.FIELD_PHONE));
            String address = "" + cursor.getString(cursor.getColumnIndex(ConverterDBHelper.FIELD_ADDRESS));
            String link = "" + cursor.getString(cursor.getColumnIndex(ConverterDBHelper.FIELD_LINK));
            mOrganizationModel = new OrganizationModel(
                    id,
                    title,
                    region,
                    city,
                    phone,
                    address,
                    link);
            try {
                mTvBankName.setText("" + mOrganizationModel.getTitle());
                mTvRegion       .setText(Html.fromHtml("Регион (область): <b>" + mOrganizationModel.getRegion() + "</b>"));
                mTvCity         .setText(Html.fromHtml("Город: <b>" + mOrganizationModel.getCity() + "</b>"));
                mTvAddressName  .setText(Html.fromHtml("Адрес: <b> " + mOrganizationModel.getAddress() + "</b>"));
                mTvPhoneName    .setText(Html.fromHtml("Телефон: <b>" + mOrganizationModel.getPhone() + "</b>"));
                link = mOrganizationModel.getLink();
                if (link != null) {
                    mTvLink.setText(
                            Html.fromHtml(
                                    "Официальный сайт банка:<br /> <b><a href=\"" + link +
                                            "\">" + link + "</a></b>"));
                    final String finalLink = link;
                    mTvLink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse(finalLink));
                            startActivity(browserIntent);
                        }
                    });
                }
                if(mOrganizationModel != null) {
                    getSupportActionBar().setTitle(mOrganizationModel.getTitle());
                    mToolbar.setSubtitle(mOrganizationModel.getCity());
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fabMap_AD:
                Uri uriLoc = Uri.parse("geo:0,0?q="
                        + mOrganizationModel.getCity()
                        + mOrganizationModel.getAddress() + " ");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uriLoc);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            break;
            case R.id.fabLink_AD:
                try{
                    String url =  mOrganizationModel.getLink();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(url));
                    startActivity(browserIntent);

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(),
                            "Web address has not found " +
                                    ex.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            break;
            case R.id.fabPhone_AD:
                try{
                    String phoneNumber =  mOrganizationModel.getPhone();
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:+38" + phoneNumber));
                    startActivity(dialIntent);

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(),
                            "Phone number has not found " +
                                    ex.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
