package com.milab.trunch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by or on 4/3/2015.
 */
public class TrunchActivity extends ActionBarActivity {


    //=========================================
    //				Constants
    //=========================================


    //=========================================
    //				Fields
    //=========================================

    String restName;
    String trunchersString;
    Button shuffleButton;
    User mUser;
    ObjectMapper mObjectMapper;
    TextView truncher1des;
    TextView truncher2des;
    private ImageView truncher1;
    private ImageView truncher2;
    private ImageView chosenRest;
    private String truncher1Url;
    private String truncher2Url;
    private String truncher1Name;
    private String truncher2Name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.its_a_trunch);

        mObjectMapper = new ObjectMapper();

        restName = getIntent().getStringExtra("restName");
        trunchersString = getIntent().getStringExtra("trunchers");
        mUser = (User) getIntent().getParcelableExtra("user");

        User[] trunchers = new User[0];
        try {
            trunchers = mObjectMapper.readValue(trunchersString, User[].class);
            //mUser = mObjectMapper.readValue(j)
        } catch (IOException e) {
            e.printStackTrace();
        }

        showTrunchDialog(trunchers, restName);

        shuffleButton = (Button) findViewById(R.id.shuffle_button);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShuffleDialogClass shuffle = new ShuffleDialogClass(TrunchActivity.this);
                shuffle.show();
            }
        });

        truncher1 = (ImageView) findViewById(R.id.truncher1);
        truncher1Url = trunchers[0].getPictureUrl();
        truncher1Name = trunchers[0].getFirstName();
        truncher2 = (ImageView) findViewById(R.id.truncher2);
        truncher2Url = trunchers[1].getPictureUrl();
        truncher2Name = trunchers[1].getFirstName();
        chosenRest = (ImageView) findViewById(R.id.restaurant);


        truncher1des = (TextView) findViewById(R.id.truncher1text);
        truncher2des = (TextView) findViewById(R.id.truncher2text);

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