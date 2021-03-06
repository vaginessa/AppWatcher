package com.google.android.finsky.api;

import android.accounts.Account;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DfeApiContext
{
    private static final String PLAY_VERSION_NAME = "5.12.10";
    private static final int PLAY_VERSION_CODE = 80421000;
    private static final String CLIENT_ID = "am-google";
    final Context mContext;
    private final Account mAccount;
    private final Map<String, String> mHeaders;
    private String mLastAuthToken;


    private DfeApiContext(final Context context, final Account account, final String authToken, final String deviceId,
                            final Locale locale, final String mccmnc,
                            final String clientId, final String loggingId, final int filterLevel) {
        this.mHeaders = new HashMap<String, String>();
        mContext = context;
        mAccount = account;
        mLastAuthToken = authToken;
        this.mHeaders.put("X-DFE-Device-Id", deviceId);//Long.toHexString(PlayG.androidId.get()));
        this.mHeaders.put("Accept-Language", locale.getLanguage() + "-" + locale.getCountry());
        if (!TextUtils.isEmpty((CharSequence)mccmnc)) {
            this.mHeaders.put("X-DFE-MCCMNC", mccmnc);
        }
        if (!TextUtils.isEmpty((CharSequence)clientId)) {
            this.mHeaders.put("X-DFE-Client-Id", clientId);
        }
        if (!TextUtils.isEmpty((CharSequence)clientId)) {
            this.mHeaders.put("X-DFE-Logging-Id", loggingId);
        }
        this.mHeaders.put("User-Agent", makeUserAgentString(PLAY_VERSION_NAME, 3, PLAY_VERSION_CODE));
        this.mHeaders.put("X-DFE-Filter-Level", String.valueOf(filterLevel));
    }



    public static DfeApiContext create(final Context context, final Account account,final String authTokenStr, final String deviceId, final int filterLevel) {
        final TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        return new DfeApiContext(
                    context, account, authTokenStr,deviceId,
                    Locale.getDefault(), tm.getSimOperator(), CLIENT_ID, "", filterLevel
        );
    }


    private String makeUserAgentString(final String versionName, final int api, final int versionCode) {
        return String.format(Locale.US, "Android-Finsky/%s (api=%d,versionCode=%d,sdk=%d,device=%s,hardware=%s,product=%s)",
                versionName,
                api, versionCode, Build.VERSION.SDK_INT, sanitizeHeaderValue(Build.DEVICE), sanitizeHeaderValue(Build.HARDWARE), sanitizeHeaderValue(Build.PRODUCT)
        );
    }


    private static String sanitizeHeaderValue(final String s) {
        return Uri.encode(s).replace("(", "").replace(")", "");
    }
    
    public Account getAccount() {
        return this.mAccount;
    }

    String getAccountName() {
        final Account account = this.getAccount();
        if (account == null) {
            return null;
        }
        return account.name;
    }


    Map<String, String> getHeaders() throws AuthFailureError {
        synchronized (this) {
            final HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.putAll(this.mHeaders);
            hashMap.put("Authorization", "GoogleLogin auth=" + this.mLastAuthToken);
//            if (AppLog.DEBUG) {
//                for(String key: hashMap.keySet()) {
//                    AppLog.d("HTTP Header: "+key+" = "+hashMap.get(key));
//                }
//            }
            return hashMap;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[PlayDfeApiContext headers={");
        int n = 1;
        for (final String s : this.mHeaders.keySet()) {
            if (n != 0) {
                n = 0;
            }
            else {
                sb.append(", ");
            }
            sb.append(s).append(": ").append(this.mHeaders.get(s));
        }
        sb.append("}]");
        return sb.toString();
    }
}
