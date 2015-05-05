package com.example.trunch;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ronylangbord on 4/10/15.
 */
public class GreatChoiceDialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button ok_button;
    public String restName;
    TextView mTextGreatChoice;


    public GreatChoiceDialog(Activity a, String restName) {
        super(a);
        this.c = a;
        this.restName = restName;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_great_choice);

        mTextGreatChoice = (TextView) findViewById(R.id.text_show_great_choice);
        mTextGreatChoice.setText(restName + " is a GREAT choice!");
        ok_button = (Button) findViewById(R.id.ok_button);

        ok_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_button:

                dismiss();
                break;

            default:
                break;
        }
        dismiss();
    }

}
