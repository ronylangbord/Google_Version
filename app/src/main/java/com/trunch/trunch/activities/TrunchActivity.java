package com.trunch.trunch.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trunch.trunch.R;
import com.trunch.trunch.Strings;
import com.trunch.trunch.instances.User;
import com.trunch.trunch.components.ShuffleDialogClass;
import com.trunch.trunch.components.TrunchDialogClass;
import com.squareup.picasso.Picasso;
import com.trunch.trunch.utilities.SharedPrefUtils;

import java.io.IOException;

/**
 * Created by or on 4/3/2015.
 */
public class TrunchActivity extends ActionBarActivity {



    //=========================================
    //				Fields
    //=========================================

    String restName;
    String trunchersString;
    Button shuffleButton;
    User mUser;
    ObjectMapper mObjectMapper;
    TextView m_FirstTruncherText;
    TextView m_SecondTruncherText;
    private ImageView m_FirstTruncherImage;
    private ImageView m_SecondTruncherImage;
    private ImageView m_ChosenRest;
    private String m_FirstTruncherImageUrl;
    private String m_SecondTruncherImageUrl;
    SharedPreferences mSharedPreferences;


    //=========================================
    //				Activity Lifecycle
    //=========================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.its_a_trunch);

        mSharedPreferences = getSharedPreferences(SharedPrefUtils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPrefUtils.UpdateTrunchResult(mSharedPreferences, "No Trunch","No Trunch", false);

        mObjectMapper = new ObjectMapper();

        restName = SharedPrefUtils.getChosenRest(mSharedPreferences);
        trunchersString = SharedPrefUtils.getTrunchers(mSharedPreferences);
        mUser = (User) getIntent().getParcelableExtra(Strings.user);

        User[] trunchers = new User[0];
        try {
            trunchers = mObjectMapper.readValue(trunchersString, User[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //showTrunchDialog(trunchers, restName);

        shuffleButton = (Button) findViewById(R.id.la_shuffle_button);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShuffleDialogClass shuffle = new ShuffleDialogClass(TrunchActivity.this);
                shuffle.show();
            }
        });

        m_FirstTruncherImage = (ImageView) findViewById(R.id.truncher1);
        m_SecondTruncherImage = (ImageView) findViewById(R.id.truncher2);
        m_ChosenRest = (ImageView) findViewById(R.id.restaurant);

        m_FirstTruncherImageUrl = trunchers[0].getPictureUrl();
        m_SecondTruncherImageUrl = trunchers[1].getPictureUrl();

        Picasso.with(this).load(m_FirstTruncherImageUrl).into(m_FirstTruncherImage);
        Picasso.with(this).load(m_SecondTruncherImageUrl).into(m_SecondTruncherImage);

        String imgName = restName.toLowerCase().replaceAll(" ", "_");
        int path = getResources().getIdentifier(imgName, "drawable", getPackageName());
        m_ChosenRest.setImageResource(path);


        String firstTruncherStr = trunchers[0].getFirstName() + " "
                + trunchers[0].getLastName()+ "\n" + trunchers[0].getHeadline();
        String secondTruncherStr = trunchers[1].getFirstName() + " "
                + trunchers[1].getLastName() + "\n" + trunchers[1].getHeadline();

        m_FirstTruncherText = (TextView) findViewById(R.id.truncher1text);
        m_FirstTruncherText.setText(firstTruncherStr);
        m_SecondTruncherText = (TextView) findViewById(R.id.truncher2text);
        m_SecondTruncherText.setText(secondTruncherStr);





       // truncher1des.setText(truncher1Name + " " + trunchers[0].getHeadline());
        //truncher2des.setText(truncher2Name + " " + trunchers[1].getHeadline());

        ///Picasso.with(this).load(truncher1Url).into(truncher1);
        //Picasso.with(this).load(truncher2Url).into(truncher2);
       // Picasso.with(this).load("http://mimamoo.com/images/stories/jreviews/35655_giraffe_1329198005.JPG").into(chosenRest);

    }

    public void showTrunchDialog(User[] trunchers, String restaurant) {
        TrunchDialogClass trunchDialog = new TrunchDialogClass(this, restName, trunchers);
        trunchDialog.show();
    }
}