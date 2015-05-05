package com.milab.trunch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    //=========================================
    //				Constants
    //=========================================

    private static final long MIN_TIME_BETWEEN_JSON_DOWNLOAD = 1000 * 60 * 60 * 24; //one day
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";
    private static final String urlGetTags = "http://www.mocky.io/v2/54ba8366e7c226ad0b446eff";
    private static final String urlGetRest = "http://www.mocky.io/v2/552421c1cb84087608d88880";
    private static final String urlGetUser = "http://www.mocky.io/v2/552e883749f6abea07a3586d";

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    View mSplashScreenView;
    TextView mTitleView;
    SearchView mTempView;
    Typeface robotoFont;
    User mUser;
    Toolbar mToolbar;
    ObjectMapper mMapper;
    //=========================================
    //				Activity Lifecycle
    //=========================================
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.silver_medal);
        mToolbar.setLogo(R.drawable.trunch_logo_small);


        // Init Fields
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mSplashScreenView = findViewById(R.id.splash_screen);
        mTitleView = (TextView) findViewById(R.id.titleView);
        mTempView = (SearchView) findViewById(R.id.tempView);
        mMapper = new ObjectMapper();
        robotoFont  = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

        // set daily reminder
        AlarmsUtils.setReminderAlarm(this);

        //set the font in the main activity to be roboto Font and a shadow to the text
        mTitleView.setTypeface(robotoFont);
        int radius = 15;
        int xOffSet= 10;
        int yOffSet = 10;
        int shadowColor = Color.BLACK;
        mTitleView.setShadowLayer(radius, xOffSet, yOffSet, shadowColor);

        // check if the user isn't logged in
        if (!SharedPrefUtils.isLoggedIn(mSharedPreferences)) {
            // start linkedinConnectActivity
            linkedinConnect();
        } else {
            // get user details from server
            new GetUserAsync().execute(urlGetUser);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void restOfTheActivity() {
        loadUserImage();
        long lastTimeDownloaded = SharedPrefUtils.lastTimeDownloaded(mSharedPreferences);
        long timeDifference = System.currentTimeMillis() - lastTimeDownloaded;
        // Compare to MIN_TIME_BETWEEN_JSON_DOWNLOAD and act accordingly.
        if (timeDifference > MIN_TIME_BETWEEN_JSON_DOWNLOAD) {
            // show the splash screen
            mSplashScreenView.setVisibility(View.VISIBLE);
            // go get JSON from server
            downloadJSON();

        } else {
            // init tempView
            initTempView();
        }
    }




    //=========================================
    //				Private Methods
    //=========================================


    private void loadUserImage() {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageBitmap(bitmap);
                Drawable image = imageView.getDrawable();
                mToolbar.setNavigationIcon(image);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(this).load(mUser.getPictureUrl()).into(target);
    }

    private void initTempView() {
        mTempView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.putExtra("user", mUser);
                startActivity(intent);
            }
        });

    }

    private void downloadJSON() {
        // create asyncTask which in on doInBackground makes an HTTPRequest to server to get JSON
        // onPostExecute if all went well it calls parseAndInit method
        new downloadJsonAsync().execute(urlGetTags, urlGetRest);
    }

    //=========================================
    //		    linkedinConnect
    //=========================================






    private void linkedinConnect() {
        Intent intent = new Intent(this, LinkedinConnectActivity.class);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                // get user
                mUser = (User) data.getParcelableExtra("user");
                restOfTheActivity();
            }
            if (resultCode == RESULT_CANCELED) {
                linkedinConnect();
            }
        }
    }

    //=========================================
    //		JsonDownload AsyncTask Class
    //=========================================

    private class downloadJsonAsync extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            // Get json from server
            String jsonTags = RequestManger.requestGet(params[0]);
            String jsonRest = RequestManger.requestGet(params[1]);
            return new String[]{(jsonTags),(jsonRest)};
        }

        @Override
        protected void onPostExecute(String[] json) {
            String jsonTags =  json[0];
            String jsonRest = json[1];
            if ((jsonTags != null) && (jsonRest != null)) {
                // save json to sharePrefs
                SharedPrefUtils.saveRestData(mSharedPreferences, jsonTags, jsonRest);
                // init tempView
               initTempView();
                // remove splash
                mSplashScreenView.setVisibility(View.GONE);
            } else {
                retryDownloadJSON(); // somthing went wrong on server so we will try again
            }
        }
    }

    // if something went wrong
    private void retryDownloadJSON() {
        // if we fail to get JSON display an error screen and a retry button.
        // The retry button will repeat the downloadJSONasync when pressed.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Network Unavailable!");
        builder.setMessage("Sorry there was an error getting data from the Internet.");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downloadJSON();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //=========================================
    //		GetUserAsync AsyncTask Class
    //=========================================

    private class GetUserAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // make params
            ArrayList<NameValuePair> GetParameters = new ArrayList<NameValuePair>();
            GetParameters.add(new BasicNameValuePair("android_id",
                    SharedPrefUtils.getUserId(mSharedPreferences)));
            // sign user to Data-base
            return RequestManger.requestPost(params[0],GetParameters);
            //return RequestManger.requestGet(params[0]);

        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                // make user
                try {
                    mUser = mMapper.readValue(json, User.class);
                } catch (IOException e) {
                    new GetUserAsync().execute(urlGetUser);
                }
                restOfTheActivity();

            } else {
                new GetUserAsync().execute(urlGetUser);
            }
        }
    }


}