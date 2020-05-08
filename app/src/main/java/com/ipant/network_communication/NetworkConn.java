package com.ipant.network_communication;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ProgressBar;

import com.ipant.BuildConfig;
import com.ipant.utils.AppConstants;
import com.ipant.utils.PersistentCookieStore;
import com.ipant.utils.iPantApp;

import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created on 8/10/2018
 */

public class NetworkConn {
    private static final NetworkConn ourInstance = new NetworkConn();
    private ProgressBar progressBar;
    private boolean isFailureResponse = false;
    private Application application;


    public static NetworkConn getInstance() {
        return ourInstance;
    }

    private NetworkConn() {

    }


    private onRequestResponse requestResponse;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static final String HEADER_AUTHENTICATION = "Authorization";
    private static final String HEADER_VERSION = "version";
    private static final String HEADER_TOKEN = "token";
    private static final String HEADER_USER_ID = "userid";

    private static final String HEADER_DEVICE_TYPE = "device_type";
    private static final String HEADER_DEVICE_ID = "device_id";
    private static final String HEADER_CURRENT_TIME_ZONE = "current_time_zone";
    private static final String HEADER_LANGUAGE = "language";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";


    private Dialog loaderDialog;

    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public void makeRequest(Request request, final String eventType, final onRequestResponse requestResponse) {
        //   requestResponse = (onRequestResponse) context;
        if (iPantApp.checkNetworkConnectivity()) {
            getOkHttp3Client().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    requestResponse.onRequestFailed("Request failed, please try again.");

                    Log.e("Failure Reson", e.getLocalizedMessage() + "   " + e.getMessage() + "   " + e.getCause());
                    e.printStackTrace();

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String stringResponse = response.body().string();
                    response.body().close();

                    try {


                        Log.e(eventType, stringResponse);

                        if (stringResponse != null) {

                            JSONObject jsonObject = new JSONObject(stringResponse);
                            String strMessage = "";
                            int apiResultStatus = jsonObject.getInt("status");


                            if (jsonObject.has("errorcode") &&
                                    (jsonObject.getInt("errorcode") == 401 || jsonObject.getInt("errorcode") == 203 || jsonObject.getInt("errorcode") == 467)) {

                                if (jsonObject.getInt("errorcode") == 401 || jsonObject.getInt("errorcode") == 203) {
                                    requestResponse.onSessionExpire();
                                } else if (jsonObject.getInt("errorcode") == 467) {
                                    requestResponse.onAppHardUpdate();
                                }
                                return;
                            }

                            if (apiResultStatus == 1) {
                                requestResponse.onResponse(stringResponse, eventType);

                            } else {

                                if (jsonObject.has("message")) {
                                    strMessage = jsonObject.getString("message");
                                    requestResponse.onRequestFailed(strMessage);
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        } else {
            Log.e("No Network Connection", "No Network Connection");
            requestResponse.onNoNetworkConnectivity();
        }


    }


    public void makeBankRequest(Application context, Request request, final String eventType, final onRequestResponse requestResponse) {
        //   requestResponse = (onRequestResponse) context;
        this.application = context;
        if (iPantApp.checkNetworkConnectivity()) {
            getBanKOkHttp3Client().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    requestResponse.onRequestFailed("Request failed, please try again.");

                    Log.e("Failure Reson", e.getLocalizedMessage() + "   " + e.getMessage() + "   " + e.getCause());
                    e.printStackTrace();

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String stringResponse = response.body().string();
                    response.body().close();

                    try {


                        Log.e(eventType, stringResponse);

                        if (stringResponse != null) {
                            requestResponse.onResponse(stringResponse, eventType);


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        } else {
            Log.e("No Network Connection", "No Network Connection");
            requestResponse.onNoNetworkConnectivity();
        }


    }


    public OkHttpClient getOkHttp3Client() {
        try {


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(hostnameVerifier)
                    .sslSocketFactory(getSSLFactory(), (X509TrustManager) trustAllCerts[0])
                    .connectTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
            ;
            return okHttpClient;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public OkHttpClient getBanKOkHttp3Client() {
        try {


            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(application.getApplicationContext()), CookiePolicy.ACCEPT_ALL);


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .hostnameVerifier(hostnameVerifier)
                    .sslSocketFactory(getSSLFactory(), (X509TrustManager) trustAllCerts[0])
                    .connectTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
            ;
            return okHttpClient;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };


    public SSLSocketFactory getSSLFactory() {

        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustAllCerts, new java.security.SecureRandom());
            return context.getSocketFactory();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Request createGetRequest(String url) {
        // changes made by vikash
//        String lan = "";
//        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("English")) {
//            lan = "en";
//        }
//        else {
//            lan = "sw";
//        }


        // RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(HEADER_VERSION, "" + BuildConfig.VERSION_NAME)
                .addHeader(HEADER_CONTENT_TYPE, "application/json")
                .addHeader(HEADER_AUTHENTICATION, AppConstants.USER_DATA_BEAN_OBJ == null ? "" : "" + AppConstants.USER_DATA_BEAN_OBJ.getData().get(0).getAuthorization())
                .addHeader(HEADER_CURRENT_TIME_ZONE, TimeZone.getDefault().getID())
                .addHeader(HEADER_LANGUAGE, AppConstants.LANGUAGE)
                .addHeader(HEADER_DEVICE_TYPE, "android")
                .addHeader(HEADER_DEVICE_ID, AppConstants.FCM_TOKEN)
                .build();

        //Log.e("Print",lan);


        return request;
    }


    public Request createPostRequest(String url, String json) {
        // changes made by vikash
//        String lan = "";
//        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("English")) {
//            lan = "en";
//        }
//        else {
//            lan = "sw";
//        }


        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader(HEADER_VERSION, "" + BuildConfig.VERSION_NAME)
                .addHeader(HEADER_CONTENT_TYPE, "application/json")
                .addHeader(HEADER_AUTHENTICATION, AppConstants.USER_DATA_BEAN_OBJ == null ? "" : "" + AppConstants.USER_DATA_BEAN_OBJ.getData().get(0).getAuthorization())
                .addHeader(HEADER_CURRENT_TIME_ZONE, TimeZone.getDefault().getID())
                .addHeader(HEADER_LANGUAGE, AppConstants.LANGUAGE)
                .addHeader(HEADER_DEVICE_TYPE, "android")
                .addHeader(HEADER_DEVICE_ID, AppConstants.FCM_TOKEN)
                .build();

        return request;
    }


    public Request createPutRequest(String url, String json) {
        // changes made by vikash
//        String lan = "";
//        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("English")) {
//            lan = "en";
//        }
//        else {
//            lan = "sw";
//        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader(HEADER_VERSION, "" + BuildConfig.VERSION_NAME)
                .addHeader(HEADER_CONTENT_TYPE, "application/json")
                .addHeader(HEADER_AUTHENTICATION, AppConstants.USER_DATA_BEAN_OBJ == null ? "" : "" + AppConstants.USER_DATA_BEAN_OBJ.getData().get(0).getAuthorization())
                .addHeader(HEADER_CURRENT_TIME_ZONE, TimeZone.getDefault().getID())
                .addHeader(HEADER_LANGUAGE, AppConstants.LANGUAGE)
                .addHeader(HEADER_DEVICE_TYPE, "android")
                .addHeader(HEADER_DEVICE_ID, AppConstants.FCM_TOKEN)
                .build();

        return request;
    }

    public Request createFormDataRequest(String url, RequestBody requestBody) {
        // changes made by vikash
//        String lan = "";
//        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("English")) {
//            lan = "en";
//        }
//        else {
//            lan = "sw";
//        }

        //   RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader(HEADER_VERSION, "" + BuildConfig.VERSION_NAME)
                .addHeader(HEADER_CONTENT_TYPE, "application/json")
                .addHeader(HEADER_AUTHENTICATION, AppConstants.USER_DATA_BEAN_OBJ == null ? "" : "" + AppConstants.USER_DATA_BEAN_OBJ.getData().get(0).getAuthorization())
                .addHeader(HEADER_CURRENT_TIME_ZONE, TimeZone.getDefault().getID())
                .addHeader(HEADER_LANGUAGE, AppConstants.LANGUAGE)
                .addHeader(HEADER_DEVICE_TYPE, "android")
                .addHeader(HEADER_DEVICE_ID, AppConstants.FCM_TOKEN)
                .build();

        return request;
    }

    public interface onRequestResponse {
        public void onResponse(String response, String eventType);
        public void onNoNetworkConnectivity();
        public void onRequestRetry();
        public void onRequestFailed(String msg);
        public void onSessionExpire();
        public void onAppHardUpdate();
    }


    public boolean checkNetworkConnectivity(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }

    /**
     * Is failure message requires
     */
    public void isFailureResponse(boolean isFailureRequire) {
        isFailureResponse = isFailureRequire;
    }


}

