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
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.lesson20.converterlab.database.ConverterContentProvider;
import com.lesson20.converterlab.database.ConverterDBHelper;
import com.lesson20.converterlab.models.OrganizationModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    private Toolbar mToolbar;
    private ShareActionProvider mShareActionProvider;
    private String mID = "";

    TextView mTvBankName;
    TextView mTvWebsiteName;
    TextView mTvRegion;
    TextView mTvAddressName;
    TextView mTvCity;
    TextView mTvPhoneName;
    TextView mTvEmailName;
    TextView mTvGBP;
    TextView mTvUSD;
    TextView mTvEUR;
    TextView mTvRUB;
    TextView mTvPLN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initUI();
        Intent intent = getIntent();
        mID = intent.getStringExtra("_id");
        getSupportLoaderManager().initLoader(0, null, this);

    }

    private void initUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTvBankName             = (TextView) findViewById(R.id.tvBankName_AD);
        mTvRegion               = (TextView) findViewById(R.id.tvRegion_AD);
        mTvCity                 = (TextView) findViewById(R.id.tvCity_AD);
        mTvWebsiteName          = (TextView) findViewById(R.id.tvWebsiteName_AD);
        mTvAddressName          = (TextView) findViewById(R.id.tvAddressName_AD);
        mTvPhoneName            = (TextView) findViewById(R.id.tvPhoneName_AD);
        mTvGBP                  = (TextView) findViewById(R.id.tvGBP_AD);
        mTvUSD                  = (TextView) findViewById(R.id.tvUSD_AD);
        mTvEUR                  = (TextView) findViewById(R.id.tvEUR_AD);
        mTvRUB                  = (TextView) findViewById(R.id.tvRUB_AD);
        mTvPLN                  = (TextView) findViewById(R.id.tvPLN_AD);
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
//                ShareDialog dialog = new ShareDialog();
//                dialog.setBankInfo( new BankModel("text","text","text","text","text"));
//                dialog.show(getFragmentManager(), "Edit Contact");
//
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ConverterContentProvider.CONTENT_URI;
        String selection = ConverterDBHelper.FIELD_ROW_ID + "=?";
        String[] selectionArgs = { mID };
        return new CursorLoader(this, uri, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String id = "";
        OrganizationModel organizationModel = null;
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
            id += data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_ROW_ID));
            if (id.equals(mID)) {
                title += data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_TITLE));
                region += data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_REGION));
                city += data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_CITY));
                phone += data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_PHONE));
                address += data.getString(data.getColumnIndex(ConverterDBHelper.FIELD_ADDRESS));
                populateInfo(new OrganizationModel(
                        id,
                        title,
                        region,
                        city,
                        phone,
                        address,
                        link));
            }
            data.moveToNext();

        }
    }

    private void populateInfo(OrganizationModel _organizationModel) {
        try {
            mTvBankName.setText("" + _organizationModel.getTitle());
            mTvRegion.setText("" + _organizationModel.getRegion());
            mTvCity.setText("" + _organizationModel.getCity());
            mTvWebsiteName.setText("" + _organizationModel.getLink());
            mTvAddressName.setText("" + _organizationModel.getAddress());
            mTvPhoneName.setText("" + _organizationModel.getPhone());
//        mTvGBP.setText(_organizationModel.);
//        mTvUSD.setText(_organizationModel.);
//        mTvEUR.setText(_organizationModel.);
//        mTvRUB.setText(_organizationModel.);
//        mTvPLN.setText(_organizationModel.);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
