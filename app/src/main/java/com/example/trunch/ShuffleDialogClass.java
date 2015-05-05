package com.example.trunch;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ronylangbord on 4/13/15.
 */
public class ShuffleDialogClass extends Dialog implements View.OnClickListener {


    Activity activity;
    Button ok, startOver;
    TextView alertText;



    public ShuffleDialogClass(Activity a) {
        super(a);
        this.activity = a;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuffle_alert);

        ok = (Button) findViewById(R.id.ok_button_shuffle);
        startOver = (Button) findViewById(R.id.start_over_button);
        alertText = (TextView) findViewById(R.id.trunch_text);

        ok.setOnClickListener(this);
        startOver.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_button_shuffle:
                dismiss();
                break;
            case R.id.start_over_button:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();

    }
}
