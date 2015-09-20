package com.lesson20.converterlab;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lesson20.converterlab.models.OrganizationModel;

public class OrganizationView extends LinearLayout {

	private TextView mTvOrgTitle;
	private TextView mTvRegion;
	private TextView mTvCity;
	private TextView mTvPhone;
	private TextView mTvAddress;

	public OrganizationView(Context context) {
		super(context);

		inflate(context, R.layout.view_organization, this);

		findViews();
	}

	private void findViews() {
		mTvOrgTitle	= (TextView) findViewById(R.id.tvTitle_VI);
		mTvRegion	= (TextView) findViewById(R.id.tvRegion_VI);
		mTvCity		= (TextView) findViewById(R.id.tvCity_VI);
		mTvPhone	= (TextView) findViewById(R.id.tvPhone_VI);
		mTvAddress	= (TextView) findViewById(R.id.tvAddress_VI);
	}

	public void setOrganizationModel(OrganizationModel _model) {
		mTvOrgTitle	.setText(_model.getTitle());
		mTvRegion	.setText(_model.getRegion());
		mTvCity		.setText(_model.getCity());
		mTvPhone	.setText(_model.getPhone());
		mTvAddress	.setText(_model.getAddress());
	}

}
