package com.trunch.trunch.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.trunch.trunch.R;
import com.trunch.trunch.Strings;
import com.trunch.trunch.Urls;
import com.trunch.trunch.utilities.RequestManger;
import com.trunch.trunch.utilities.SharedPrefUtils;
import com.trunch.trunch.activities.SecondActivity;
import com.trunch.trunch.activities.TrunchActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by or on 4/3/2015.
 */
public class TrunchCheckerService extends BroadcastReceiver {

    //=========================================
    //				Constants
    //=========================================
    int mNotificationId = 001;

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    String mRestName;
    String mTrunchers;
    String mUserId;
    PendingIntent mNotificationPendingIntent;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;

    @Override
    public void onReceive(Context context, Intent intent) {
        mSharedPreferences = context.getSharedPreferences(SharedPrefUtils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mUserId = SharedPrefUtils.getUserId(mSharedPreferences);
        boolean hasTrunch = SharedPrefUtils.hasTrunch(mSharedPreferences);
        mRestName = intent.getStringExtra(Strings.restName);
        if (!hasTrunch) {
            new AsynkTrunchChecker().execute(Urls.GET_TRUNCH);
        } else {
            mTrunchers = SharedPrefUtils.getTrunchers(mSharedPreferences);
            cancelAlarm(SecondActivity.getSyncPendingIntent(context));
            showNotification(context);

        }
    }

    private void showNotification(Context context) {
        mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.applogo1)
                 .setContentTitle("You have a Trunch!").setContentText("Find out who you are eating lunch with.");
        // The PendingIntent to launch our activity if the user selects this
        // notification
        mNotificationPendingIntent = PendingIntent.getActivity(context, 0,
                trunchActivityIntent(context), PendingIntent.FLAG_CANCEL_CURRENT);
        // Send the notification.

        mBuilder.setContentIntent(mNotificationPendingIntent);
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mBuilder.setLights(Color.GREEN, 3000, 3000);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mNotifyMgr = (NotificationManager) context.getApplicationContext()
                                            .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    private void cancelAlarm(PendingIntent syncPendingIntent) {
        syncPendingIntent.cancel();
    }

    private Intent trunchActivityIntent(Context context) {
        Intent intentForTrunch = new Intent(context, TrunchActivity.class);
        intentForTrunch.putExtra(Strings.trunchers, mTrunchers);
        intentForTrunch.putExtra(Strings.restName, mRestName);


        intentForTrunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentForTrunch;
    }



    private class AsynkTrunchChecker extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> GetParameters = new ArrayList<NameValuePair>();
            GetParameters.add(new BasicNameValuePair(Strings.android_id,mUserId));
            GetParameters.add(new BasicNameValuePair(Strings.rest ,mRestName));
            return RequestManger.requestPost(params[0], GetParameters);
        }

        @Override
        protected void onPostExecute(String trunchers) {
            if (trunchers != null) {
                SharedPrefUtils.UpdateTrunchResult(mSharedPreferences,trunchers ,mRestName ,true);
            }
        }
    }
}
