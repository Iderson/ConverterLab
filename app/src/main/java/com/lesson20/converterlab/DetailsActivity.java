package com.lesson20.converterlab;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.lesson20.converterlab.database.ConverterDBHelper;
import com.lesson20.converterlab.models.AskBidModel;
import com.lesson20.converterlab.models.CurrencyModel;
import com.lesson20.converterlab.models.OrganizationModel;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initUI();
        Intent intent = getIntent();
        mID = intent.getStringExtra("_id");
        getFromDB(mID);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_AD);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager llm = new LinearLayoutManager(DetailsActivity.this);

        mRvCurrencies           = (RecyclerView) findViewById(R.id.rvCurrencies_AD);
        mTvBankName             = (TextView) findViewById(R.id.tvBankName_AD);
        mTvRegion               = (TextView) findViewById(R.id.tvRegion_AD);
        mTvCity                 = (TextView) findViewById(R.id.tvCity_AD);
        mTvAddressName          = (TextView) findViewById(R.id.tvAddressName_AD);
        mTvPhoneName            = (TextView) findViewById(R.id.tvPhoneName_AD);
        mTvLink                 = (TextView) findViewById(R.id.tvLink_AD);

        mRvCurrencies.setLayoutManager(llm);
        ((FloatingActionButton) findViewById(R.id.fabMap_AD)).setOnClickListener(this);
        ((FloatingActionButton) findViewById(R.id.fabLink_AD)).setOnClickListener(this);
        ((FloatingActionButton) findViewById(R.id.fabPhone_AD)).setOnClickListener(this);
        ((SwipeRefreshLayout) findViewById(R.id.swpRefreshLayout_AD)).setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getFromDB(mID);
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

    private void getFromDB(String _id){
        ConverterDBHelper DBOpenHelper = new ConverterDBHelper(this);
        Cursor cursor = DBOpenHelper.getOrganizationDetail(_id);
        Cursor cursor2 = DBOpenHelper.getCurrency(_id);

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
            populateInfo();
        }

        if (cursor2 != null) {
            cursor2.moveToFirst();
            ArrayList<CurrencyModel> currencyModels = new ArrayList<>();

            for (int i = 0; i < cursor2.getColumnCount(); i++) {
                CurrencyModel curr = new CurrencyModel();
                if(!cursor2.isNull(i) && cursor2.getColumnName(i).contains("_ASK")) {
                    try {
                        AskBidModel askBidModel = new AskBidModel();
                        askBidModel.setAsk(Double.valueOf(cursor2.getString(i)));
                        askBidModel.setBid(Double.valueOf(cursor2.getString(i + 1)));
                        curr.setName(cursor2.getColumnName(i));
                        curr.setCurrency(askBidModel);
                        currencyModels.add(curr);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                populateRV(currencyModels);
            }

        }
    }

    private void populateRV(ArrayList<CurrencyModel> _list) {
        RVCurrAdapter adapter = new RVCurrAdapter(DetailsActivity.this, _list);
        mRvCurrencies.setAdapter(adapter);
    }



    private void populateInfo() {
        try {
            mTvBankName     .setText("" + mOrganizationModel.getTitle());
            mTvRegion       .setText("Регион (область): " + mOrganizationModel.getRegion());
            mTvCity         .setText("Город: " + mOrganizationModel.getCity());
            mTvAddressName  .setText("Адрес: " + mOrganizationModel.getAddress());
            mTvPhoneName    .setText("Телефон: " + mOrganizationModel.getPhone());
            final String link = mOrganizationModel.getLink();
            if (link != null) {
                mTvLink.setText(
                        Html.fromHtml(
                        "Официальный сайт банка:<br /> <a href=\"" + link +
                        "\">" + link + "</a> "));
                mTvLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(link));
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
