package com.lesson20.converterlab;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private ShareActionProvider mShareActionProvider;
    private String mID = "";

    TextView mTvBankName;
    TextView mTvRegion;
    TextView mTvAddressName;
    TextView mTvCity;
    TextView mTvPhoneName;
    TextView mTvLink;
    private RecyclerView mRvCurrencies;
    private OrganizationModel mOrganizationModel;

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

        mRvCurrencies           = (RecyclerView) findViewById(R.id.rvCurrencies_AD);
        mTvBankName             = (TextView) findViewById(R.id.tvBankName_AD);
        mTvRegion               = (TextView) findViewById(R.id.tvRegion_AD);
        mTvCity                 = (TextView) findViewById(R.id.tvCity_AD);
        mTvAddressName          = (TextView) findViewById(R.id.tvAddressName_AD);
        mTvPhoneName            = (TextView) findViewById(R.id.tvPhoneName_AD);
        mTvLink                 = (TextView) findViewById(R.id.tvLink_AD);

        ((FloatingActionButton) findViewById(R.id.fabMap_AD)).setOnClickListener(this);
        ((FloatingActionButton) findViewById(R.id.fabLink_AD)).setOnClickListener(this);
        ((FloatingActionButton) findViewById(R.id.fabPhone_AD)).setOnClickListener(this);
        ((SwipeRefreshLayout) findViewById(R.id.swpRefreshLayout_AM)).setOnRefreshListener(
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

//                mShareActionProvider.setShareIntent(getDefaultIntent());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent getDefaultIntent() {
        Bitmap bitmap = drawBitmap("Text\n Txt\n text\n", 100, 500);
        Uri bmpUri = getImageUri(bitmap);
        Intent shareIntent = new Intent();
        if (bmpUri != null) {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Toast.makeText(this, "No image loaded", Toast.LENGTH_SHORT).show();
        }
        //todo: make a bitmap image (scrollable)

        return shareIntent;
    }

    public Uri getImageUri( Bitmap _bitmap) {Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            _bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private Bitmap drawBitmap(String text, int textWidth, int textSize) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        Bitmap bitmap = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);

        canvas.save();
        canvas.translate(0, 0);
        mTextLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }


    private void getFromDB(String _id){
        ConverterDBHelper DBOpenHelper = new ConverterDBHelper(this);
        Cursor cursor = DBOpenHelper.getOrganizationDetail(_id);
        Cursor cursor2 = DBOpenHelper.getCurrency(_id);

        if (cursor != null) {
            cursor.moveToFirst();
            String id = "" + cursor.getString(cursor.getColumnIndex(ConverterDBHelper.FIELD_ROW_ID));
            if (id.equals(_id)) {
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
        }

        if (cursor2 != null) {
            cursor2.moveToFirst();
            String id = "" + cursor2.getString(cursor2.getColumnIndex(ConverterDBHelper.FIELD_ROW_ID));
            if (id.equals(_id)) {
                ArrayList<CurrencyModel> currencyModels = new ArrayList<>();

                CurrencyModel curr = new CurrencyModel();
                for (int i = 0; i < cursor2.getColumnCount(); i++) {

                    if (cursor2.getString(i) != null) {

                        if(cursor2.getString(i).contains("_ASK")) {
                            try {
                                AskBidModel askBidModel = new AskBidModel();
                                askBidModel.setAsk(Long.valueOf(cursor2.getString(i)));
                                askBidModel.setBid(Long.valueOf(cursor2.getString(i + 1)));
                                curr.setName(cursor2.getColumnName(i));
                                curr.setCurrency(askBidModel);
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }

                currencyModels.add(curr);
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
            mTvBankName.setText("" + mOrganizationModel.getTitle());
            mTvRegion.setText("Регион (область): " + mOrganizationModel.getRegion());
            mTvCity.setText("Город: " + mOrganizationModel.getCity());
            mTvAddressName.setText("Адрес: " + mOrganizationModel.getAddress());
            mTvPhoneName.setText("Телефон: " + mOrganizationModel.getPhone());
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
