package com.lesson20.converterlab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lesson20.converterlab.models.OrganizationModel;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ContactViewHolder> implements View.OnClickListener {

    private static final int KEY_DETAIL = 111;
    private static final int KEY_LINK = 222;
    private static final int KEY_LOCATION = 333;
    private static final int KEY_CALL = 444;
    private LayoutInflater mLf;
    private List<OrganizationModel> mOrgList;
    private Activity         mActivity;
    private int lastPosition = -1;


    RVAdapter(Activity activity, List<OrganizationModel> _orgList){
        mOrgList = _orgList;
        this.mActivity = activity;
        this.mLf        = LayoutInflater.from(mActivity);
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_item,
                        viewGroup,
                        false);

//        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflater.inflate(R.layout.custom_item, viewGroup, false);
        return new ContactViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final ContactViewHolder personViewHolder, int i) {

        String title = mOrgList.get(i).getTitle();
        String city = mOrgList.get(i).getCity();
        String region = mOrgList.get(i).getRegion();
        String phone = mOrgList.get(i).getPhone();
        String address = mOrgList.get(i).getAddress();

        personViewHolder.mHolderName         .setText(title);
        personViewHolder.mHolderCity         .setText(city);
        personViewHolder.mHolderRegion       .setText(region);
        personViewHolder.mHolderPhoneMain    .setText(phone);
        personViewHolder.mHolderAddress      .setText(address);

        personViewHolder.btnDetails.setTag(R.id.key_details, mOrgList.get(i).getId());
        personViewHolder.btnDetails.setOnClickListener(this);

        personViewHolder.btnLink.setTag(R.id.key_link, mOrgList.get(i).getLink());
        personViewHolder.btnLink.setOnClickListener(this);

        personViewHolder.btnLocation.setTag(R.id.key_location, mOrgList.get(i));
        personViewHolder.btnLocation.setOnClickListener(this);

        personViewHolder.btnCall.setTag(R.id.key_call, mOrgList.get(i).getPhone());
        personViewHolder.btnCall.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        return mOrgList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnLink_CI:
                try{
                    String url =  (String) v.getTag(R.id.key_link);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(url));
                    mActivity.startActivity(browserIntent);

                } catch (Exception ex) {
                    Toast.makeText(mActivity.getApplicationContext(),
                            "Web address has not found " +
                                    ex.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnLocation_CI:

                OrganizationModel model =  (OrganizationModel) v.getTag(R.id.key_location);
                Uri uriLoc = Uri.parse("geo:0,0?q="
                        + model.getAddress() +
                        " " + model.getTitle());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uriLoc);
                mapIntent.setPackage("com.google.android.apps.maps");
                mActivity.startActivity(mapIntent);
                break;
            case R.id.btnCall_CI:
                try{
                    String phoneNumber =  (String) v.getTag(R.id.key_call);
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:+38" + phoneNumber));
                    mActivity.startActivity(dialIntent);

                } catch (Exception ex) {
                    Toast.makeText(mActivity.getApplicationContext(),
                            "Phone number has not found " +
                                    ex.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

        break;
            case R.id.btnDetails_CI:
                String org =  (String) v.getTag(R.id.key_details);
                Intent detailIntent = new Intent(mActivity, DetailsActivity.class);
                detailIntent.putExtra("_id", org);

                mActivity.startActivity(detailIntent);
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

//            mView = (OrganizationView) itemView;
//            mView = new OrganizationView(itemView.getContext());


        }
    }
}