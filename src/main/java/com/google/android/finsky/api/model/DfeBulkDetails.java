package com.google.android.finsky.api.model;

import com.android.volley.Response;
import com.anod.appwatcher.BuildConfig;
import com.anod.appwatcher.utils.CollectionsUtils;
import com.google.android.finsky.api.DfeApi;
import com.google.android.finsky.protos.nano.Messages.Details;
import com.google.android.finsky.protos.nano.Messages.DocV2;

import java.util.ArrayList;
import java.util.List;

import info.anodsplace.android.log.AppLog;

public class DfeBulkDetails extends DfeBaseModel<Details.BulkDetailsResponse>
{
    private Details.BulkDetailsResponse mBulkDetailsResponse;
    private final DfeApi mDfeApi;
    private List<String> mDocIds;

    private final CollectionsUtils.Predicate<? super Document> mResponseFiler;

    public DfeBulkDetails(final DfeApi dfeApi, CollectionsUtils.Predicate<Document> responseFilter) {
        super();
        mDfeApi = dfeApi;
        mResponseFiler = responseFilter;
    }


    public void setDocIds(List<String> docIds) {
        mDocIds = docIds;
    }

    @Override
    protected void execute(Response.Listener<Details.BulkDetailsResponse> responseListener, Response.ErrorListener errorListener) {
        mDfeApi.getDetails(mDocIds, true, responseListener, errorListener);
    }

    public List<Document> getDocuments() {
        ArrayList<Document> list;
        if (this.mBulkDetailsResponse == null) {
            list = null;
        }
        else {
            list = new ArrayList<>();
            for (int i = 0; i < this.mBulkDetailsResponse.entry.length; ++i) {
                final DocV2 doc = this.mBulkDetailsResponse.entry[i].doc;
                if (doc == null) {
                    if (BuildConfig.DEBUG) {
                        AppLog.d("Null document for requested docId: %s ", this.mDocIds.get(i));
                    }
                }
                else {
                    list.add(new Document(doc));
                }
            }
        }
        if (mResponseFiler == null || list == null) {
            return list;
        }
        return CollectionsUtils.INSTANCE.filter(list, mResponseFiler);
    }

    @Override
    public boolean isReady() {
        return this.mBulkDetailsResponse != null;
    }

    @Override
    public void onResponse(final Details.BulkDetailsResponse mBulkDetailsResponse) {
        this.mBulkDetailsResponse = mBulkDetailsResponse;
        this.notifyDataSetChanged();
    }

}