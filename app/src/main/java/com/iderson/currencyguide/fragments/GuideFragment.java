package com.iderson.currencyguide.fragments;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iderson.currencyguide.MainActivity;
import com.iderson.currencyguide.R;
import com.iderson.currencyguide.adapter.RVOrgAdapter;
import com.iderson.currencyguide.database.CurrencyContentProvider;
import com.iderson.currencyguide.database.CurrencyDBHelper;
import com.iderson.currencyguide.models.OrganizationModel;
import com.iderson.currencyguide.service.Helper;
import com.iderson.currencyguide.service.LoadCompleteReceiver;

import java.util.ArrayList;
import java.util.List;

public class GuideFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RelativeLayout mLayout;
    private FragmentActivity mActivity;

    private LoadCompleteReceiver myBroadcastReceiver;
    private List<OrganizationModel> mBankList;
    private RecyclerView mRvBanks;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mQueryStr = "";
    private RVOrgAdapter mRvAdapter = null;
    private String DATA = "SEARCH";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        mRvBanks = (RecyclerView) mLayout.findViewById(R.id.rvBanks_AM);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swpRefreshLayout_AM);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });
//get data from database


        Helper.admobLoader(mLayout.getContext(), getResources(), mLayout.findViewById(R.id.adView));
        return mLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mActivity = getActivity();
        mActivity.getSupportLoaderManager().initLoader(0, null, this);
        String string = savedInstanceState.getString(DATA);
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
        super.onActivityCreated(savedInstanceState);
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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Helper.isOnline(mActivity, false, true)) {
                            ((MainActivity) getActivity()).startFragment("query");
                        }
//                        initUI();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void populateRV(List<OrganizationModel> _list) {
        if (mRvAdapter != null) mRvAdapter.notifyDataSetChanged();
        else {
            LinearLayoutManager llm = new LinearLayoutManager(mActivity);
            mRvBanks.setLayoutManager(llm);
            mRvBanks.setItemAnimator(new DefaultItemAnimator());
        }
        mRvAdapter = new RVOrgAdapter(mActivity, _list);
        mRvBanks.setAdapter(mRvAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = CurrencyContentProvider.CONTENT_URI;
        return new CursorLoader(mActivity, uri, null, null, null, null);
    }

    /*
    * Database listener to populate currency list
    */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String id = "";
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
            mBankList = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                id = data.getString(data.getColumnIndex(CurrencyDBHelper.FIELD_ROW_ID));
                title = data.getString(data.getColumnIndex(CurrencyDBHelper.FIELD_TITLE));
                region = data.getString(data.getColumnIndex(CurrencyDBHelper.FIELD_REGION));
                city = data.getString(data.getColumnIndex(CurrencyDBHelper.FIELD_CITY));
                phone = data.getString(data.getColumnIndex(CurrencyDBHelper.FIELD_PHONE));
                address = data.getString(data.getColumnIndex(CurrencyDBHelper.FIELD_ADDRESS));
                link = "" + data.getString(data.getColumnIndex(CurrencyDBHelper.FIELD_LINK));
                if ((!mQueryStr.equals("") &&
                        (title.toLowerCase().contains(mQueryStr.toLowerCase().trim()) ||
                                city.toLowerCase().contains(mQueryStr.toLowerCase().trim()) ||
                                region.toLowerCase().contains(mQueryStr.toLowerCase().trim())))
                        || mQueryStr.equals(""))
                    mBankList.add(new OrganizationModel(
                            id,
                            title,
                            region,
                            city,
                            phone,
                            address,
                            link));
                data.moveToNext();
            }
            populateRV(mBankList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
