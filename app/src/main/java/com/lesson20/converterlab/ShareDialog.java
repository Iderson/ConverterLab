package com.lesson20.converterlab;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareDialog extends DialogFragment implements View.OnClickListener {
    private View rootView;
    private BankModel mBankInfo;
    private ImageView mBitmapInfo;
    private TextView mTv;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        getDialog().setTitle("Contact Info");
//        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
//        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
//        getDialog().getWindow().setLayout(width, height);
//
        rootView = inflater.inflate(R.layout.fragment_bank_share, null);
        view = inflater.inflate(R.layout.activity_details, null);
        mBitmapInfo = (ImageView) rootView.findViewById(R.id.ivBitmapInfo_FS);
        mTv= (TextView) rootView.findViewById(R.id.tvAddress_FS);
        Button mShare = (Button) rootView.findViewById(R.id.btnShare_FS);

        if(mBankInfo != null && view != null)
            loadImage();

        mShare.setOnClickListener(this);
        return rootView;

    }

    private void loadImage() {
//        Bitmap bitmap = drawBitmap(mBankInfo.getName() + '\n' + mBankInfo.getRegion());
//        mBitmapInfo.setImageBitmap(bitmap);
        Bitmap bitmap = viewToBitmap(view);
        mBitmapInfo.setImageBitmap(bitmap);
    }


    @Override
    public void onClick(View v) {
        dismiss();
    }

    public void setBankInfo(BankModel _bankInfo) {
         mBankInfo = _bankInfo;
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth() + 100, view.getHeight() + 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private Bitmap drawBitmap(String text) {
        Paint paint = new Paint();
        float textSize = 40;
        int textColor = Color.BLACK;
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
}
