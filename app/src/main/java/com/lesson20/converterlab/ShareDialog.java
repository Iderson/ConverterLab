package com.lesson20.converterlab;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareDialog extends DialogFragment implements View.OnClickListener {
    private View rootView;
    private BankModel mBankInfo;
    private ImageView mIvBitmapInfo;
//    private LinearLayout view;
    private ShareActionProvider mShareActionProvider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
//        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
//        getDialog().getWindow().setLayout(width, height);
//todo: Create layout for Bank info and populate it
        rootView = inflater.inflate(R.layout.fragment_bank_share, null);
        View rootView2 = inflater.inflate(R.layout.activity_details, null);
//        view = (LinearLayout)rootView2.findViewById(R.id.llInfo);
        mIvBitmapInfo = (ImageView) rootView.findViewById(R.id.ivBitmapInfo_FS);
        Button mShare = (Button) rootView.findViewById(R.id.btnShare_FS);

//        if(mBankInfo != null && view != null)
//            loadImage();

        mShare.setOnClickListener(this);
        return rootView;

    }

    private void loadImage() {
//        Bitmap bitmap = viewToBitmap(view);
//        mIvBitmapInfo.setImageBitmap(bitmap);
    }


    @Override
    public void onClick(View v) {
        Uri bmpUri = getLocalBitmapUri(mIvBitmapInfo);
        if (bmpUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "Share Image with Currency"));
        } else Toast.makeText(getActivity(), "Failed to share!", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public void setBankInfo(BankModel _bankInfo) {
         mBankInfo = _bankInfo;
    }

//todo: correct drawing
    public Bitmap viewToBitmap(View view) {
        view.setDrawingCacheEnabled(true);

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(16, 16, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth() + 100, view.getHeight() + 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
