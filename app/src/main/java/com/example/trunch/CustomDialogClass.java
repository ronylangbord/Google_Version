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
public class CustomDialogClass extends Dialog implements View.OnClickListener {

    public Activity c;
    public Button yes, no;
    public String restName;
    TextView alertTextView;

    public CustomDialogClass(Activity a, String restName) {
        super(a);
        this.c = a;
        this.restName = restName;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.first_alert);

        alertTextView = (TextView) findViewById(R.id.text_first_alert);
        alertTextView.setText("Are you sure you want to eat at " + restName + " ?");


        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_yes:
               showGreatChoice(restName);
                waitForTrunch(restName, view);
                dismiss();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void showGreatChoice(String restName) {
        GreatChoiceDialog greatChoiceDialog = new GreatChoiceDialog(this.c, restName);
        greatChoiceDialog.show();
    }

    //sends a notification for the alarm.
    private void waitForTrunch(String restName, View view) {
        AlarmsUtils.startCheckerAlarm(view, this.getContext(), restName);
    }
}