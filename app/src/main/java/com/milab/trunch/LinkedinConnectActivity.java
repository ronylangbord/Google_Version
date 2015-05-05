package com.milab.trunch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by or on 4/3/2015.
 */



public class LinkedinConnectActivity extends Activity{

    //=========================================
    //				Constants
    //=========================================
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";
    private static final String linkedinHost = "api.linkedin.com";
    private static final String topCardUrl = "https://" + linkedinHost
            + "/v1/people/~:(first-name," + "last-name,headline,picture-url)?format=json";
    private static final String USER_CONNECT_URL = "http://rony.milab.idc.ac.il/user_connect.php";
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String GET_PACKAGE_HASH = "com.example.trunch";

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    ObjectMapper mMapper;
    User mUser;
    View mSplashScreen;


    //=========================================
    //				Activity Lifecycle
    //=========================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linkedin_activity);

        final Activity thisActivity = this;


        // Init Fields
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mMapper = new ObjectMapper();
        mSplashScreen = findViewById(R.id.splash_screen_linkedin);

        //Compute application package and hash
//        Button liShowPckHashButton = (Button) findViewById(R.id.hash_button);
//        liShowPckHashButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    PackageInfo info = getPackageManager().getPackageInfo(
//                            GET_PACKAGE_HASH,
//                            PackageManager.GET_SIGNATURES);
//                    for (Signature signature : info.signatures) {
//                        MessageDigest md = MessageDigest.getInstance("SHA");
//                        md.update(signature.toByteArray());
//
//                        ((TextView) findViewById(R.id.pckText)).setText(info.packageName);
//
//                        ((TextView) findViewById(R.id.pckHashText)).setText(Base64.encodeToString(md.digest(), Base64.NO_WRAP));
//                    }
//                } catch (PackageManager.NameNotFoundException e) {
//                    Log.d(TAG, e.getMessage(), e);
//                } catch (NoSuchAlgorithmException e) {
//                    Log.d(TAG, e.getMessage(), e);
//                }
//            }
//        });



        //Initialize session
        Button liLoginButton = (Button) findViewById(R.id.linkedinButton);
        liLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LISessionManager.getInstance(getApplicationContext()).init(thisActivity, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                       getUserDetailsFromLinkedin();
                    }
                    @Override
                    public void onAuthError(LIAuthError error) {
                        Toast.makeText(getApplicationContext(), "failed "
                                + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }, true);
            }
        });
    }

    //=========================================
    //				Private Methods
    //=========================================

    private void returnToMainActivity() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("user", mUser);
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    private void saveUserAndReturnToMain(String userDetailsJson) {
        // save userId sharePrefs
        String androidKey = getAndroidKey();
        SharedPrefUtils.saveUserID(mSharedPreferences,androidKey);
        // make user
        try {
            Map map = (Map) mMapper.readValue(userDetailsJson, Object.class);
            String userStringData = addAndroidKeyToData((String) map.get("responseData"), androidKey);
            mUser = mMapper.readValue(userStringData ,User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // save user to server and return to MainActivity
        new SaveUserAsync().execute(USER_CONNECT_URL);
    }

    private String addAndroidKeyToData(String responseData, String androidKey) {
        String allData = "";
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            jsonObject.put("android_id", androidKey);
            allData = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allData;
    }

    private void getUserDetailsFromLinkedin() {
        mSplashScreen.setVisibility(View.VISIBLE);
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(LinkedinConnectActivity.this, topCardUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                saveUserAndReturnToMain(response.toString());
            }

            @Override
            public void onApiError(LIApiError error) {
                mSplashScreen.setVisibility(View.GONE);
                LISessionManager.getInstance(getApplicationContext()).clearSession();
                Toast.makeText(getApplicationContext(), "failed: please try again..."
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    private ArrayList<NameValuePair> getUserPostParams() {
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("android_id",mUser.getAndroidId()));
        postParameters.add(new BasicNameValuePair("firstName", mUser.getFirstName()));
        postParameters.add(new BasicNameValuePair("lastName", mUser.getLastName()));
        postParameters.add(new BasicNameValuePair("headline", mUser.getHeadline()));
        postParameters.add(new BasicNameValuePair("pictureUrl", mUser.getPictureUrl()));
        return postParameters;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    private String getAndroidKey() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }

    //=========================================
    //		JsonDownload AsyncTask Class
    //=========================================

    private class SaveUserAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // sign user to Data-base
            return RequestManger.requestPost(params[0], getUserPostParams());

        }

        @Override
        protected void onPostExecute(String json) {
            String userDetailsJson =  json;
            if (userDetailsJson != null) {
                // return to MainActivity
                returnToMainActivity();

            } else {
                SharedPrefUtils.saveUserID(mSharedPreferences, null);
                LISessionManager.getInstance(getApplicationContext()).clearSession();
                mSplashScreen.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "failed: please try again..."
                        , Toast.LENGTH_LONG).show();
            }
        }
    }




}
