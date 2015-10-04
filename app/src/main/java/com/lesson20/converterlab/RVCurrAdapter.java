package com.lesson20.converterlab;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lesson20.converterlab.models.CurrencyModel;

import java.util.ArrayList;

public class RVCurrAdapter extends RecyclerView.Adapter<RVCurrAdapter.ContactViewHolder> {

    private ArrayList<CurrencyModel> mCurrency;
    private Activity         mActivity;

    RVCurrAdapter(Activity activity, ArrayList<CurrencyModel> _currency){
        mCurrency = _currency;
        this.mActivity = activity;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.currency_item,
                        viewGroup,
                        false);

        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder personViewHolder, int i) {

        String name = mCurrency.get(i).getName();
        long ask = mCurrency.get(i).getCurrency().getAsk();
        long bid = mCurrency.get(i).getCurrency().getBid();

        personViewHolder.mHolderName.setText(name);
        personViewHolder.mHolderAsk.setText(String.valueOf(ask));
        personViewHolder.mHolderBid.setText(String.valueOf(bid));
    }

    @Override
    public int getItemCount() {
        return mCurrency.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView        mHolderName;
        TextView        mHolderAsk;
        TextView        mHolderBid;
        ImageView       imgUp;
        ImageView       imgDown;

        CardView        mContainer;

        public ContactViewHolder(View itemView) {
            super(itemView);

            mHolderName         = (TextView)    itemView.findViewById(R.id.tvName_CI);
            mHolderAsk          = (TextView)    itemView.findViewById(R.id.tvValueAsk_CI);
            mHolderBid          = (TextView)    itemView.findViewById(R.id.tvValueBid_CI);

//            imgUp       = (ImageButton) itemView.findViewById(R.id.btnLink_OI);
//            imgDown     = (ImageButton) itemView.findViewById(R.id.btnLocation_OI);

        }
    }
}