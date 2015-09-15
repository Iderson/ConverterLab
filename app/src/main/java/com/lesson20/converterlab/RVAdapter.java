package com.lesson20.converterlab;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ContactViewHolder> implements View.OnClickListener {

    private List<BankModel> mBankModels;
    private Activity         mActivity;
    private int lastPosition = -1;


    RVAdapter(Activity activity, List<BankModel> _notifications){
        this.mBankModels = _notifications;
        this.mActivity = activity;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_item,
                        viewGroup,
                        false);

        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder personViewHolder, int i) {
        String mName = mBankModels.get(i).getName();
        String mCity = mBankModels.get(i).getCity();
        String mRegion = mBankModels.get(i).getRegion();
        String mPhoneMain = mBankModels.get(i).getPhoneMain();
        String mAddress = mBankModels.get(i).getAddress();

        personViewHolder.mHolderName         .setText(mName);
        personViewHolder.mHolderCity         .setText(mCity);
        personViewHolder.mHolderRegion       .setText(mRegion);
        personViewHolder.mHolderPhoneMain    .setText(mPhoneMain);
        personViewHolder.mHolderAddress      .setText(mAddress);

        personViewHolder.btnDetails.setOnClickListener(this);
        personViewHolder.btnLink.setOnClickListener(this);
        personViewHolder.btnLocation.setOnClickListener(this);
        personViewHolder.btnCall.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mBankModels.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnLink_CI:

                break;
            case R.id.btnLocation_CI:
                break;
            case R.id.btnCall_CI:
                break;
            case R.id.btnDetails_CI:
                mActivity.startActivity(new Intent(mActivity, DetailsActivity.class));
                break;
        }
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView        mHolderName;
        TextView        mHolderCity;
        TextView        mHolderRegion;
        TextView        mHolderPhoneMain;
        TextView        mHolderAddress;

        ImageButton     btnDetails;
        ImageButton     btnLink;
        ImageButton     btnLocation;
        ImageButton     btnCall;

        CardView        mContainer;

        public ContactViewHolder(View itemView) {
            super(itemView);

            mHolderName         = (TextView)    itemView.findViewById(R.id.tvName_CI);
            mHolderCity         = (TextView)    itemView.findViewById(R.id.tvCity_CI);
            mHolderRegion       = (TextView)    itemView.findViewById(R.id.tvRegion_CI);
            mHolderPhoneMain    = (TextView)    itemView.findViewById(R.id.tvPhone_CI);
            mHolderAddress      = (TextView)    itemView.findViewById(R.id.tvAddress_CI);
            mContainer          = (CardView)    itemView.findViewById(R.id.cvNotificationInfo_AC);

            btnLink     = (ImageButton) itemView.findViewById(R.id.btnLink_CI);
            btnLocation = (ImageButton) itemView.findViewById(R.id.btnLocation_CI);
            btnCall     = (ImageButton) itemView.findViewById(R.id.btnCall_CI);
            btnDetails  = (ImageButton) itemView.findViewById(R.id.btnDetails_CI);


        }
    }
}