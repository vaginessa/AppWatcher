package com.anod.appwatcher.installed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anod.appwatcher.App;
import com.anod.appwatcher.R;
import com.anod.appwatcher.adapters.AppViewHolder;
import com.anod.appwatcher.adapters.AppViewHolderBase;
import com.anod.appwatcher.adapters.AppViewHolderDataProvider;
import com.anod.appwatcher.model.AppInfo;
import com.anod.appwatcher.utils.AppIconLoader;
import com.anod.appwatcher.utils.PackageManagerUtils;

import java.util.ArrayList;
import java.util.List;

import info.anodsplace.android.widget.recyclerview.ArrayAdapter;

/**
 * @author alex
 * @date 2015-08-30
 */
public class InstalledAppsAdapter extends ArrayAdapter<String, AppViewHolderBase> {
    protected final AppViewHolder.OnClickListener mListener;
    protected final Context mContext;
    private final AppViewHolderDataProvider mDataProvider;
    private final PackageManagerUtils mPMUtils;

    final AppIconLoader mIconLoader;

    public InstalledAppsAdapter(Context context, PackageManagerUtils pmutils, AppViewHolderDataProvider dataProvider, AppViewHolder.OnClickListener listener) {
        super(new ArrayList<String>());
        mContext = context;
        mListener = listener;
        mDataProvider = dataProvider;

        mPMUtils = pmutils;
        mIconLoader = App.provide(context).iconLoader();

    }

    @Override
    public int getItemViewType(int position) {
        return 2;
    }

    @Override
    public AppViewHolderBase onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_app, parent, false);
        v.setClickable(true);
        v.setFocusable(true);

        return new InstalledAppViewHolder(v, mDataProvider, mIconLoader, mListener);

    }

    @Override
    public void onBindViewHolder(AppViewHolderBase holder, int position) {
        String packageName = getItem(position);
        AppInfo app = mPMUtils.packageToApp(packageName);
        /**
         *
         * int rowId, String appId, String pname, int versionNumber, String versionName,
         String title, String creator, Bitmap icon, int status, String uploadDate, String priceText, String priceCur, Integer priceMicros, String detailsUrl) {
         */
        holder.bindView(position, app);
    }

    @Override
    public void addAll(List<String> objects) {
        super.addAll(objects);
        mDataProvider.setTotalCount(getItemCount());
    }
}
