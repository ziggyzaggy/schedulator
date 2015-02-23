package com.examples.android.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ziggyzaggy on 23/02/2015.
 *
 * This is where the users will be able to see the schedule for the day, add new events etc etc
 */
public class DayView extends Activity{

    private String receivedIntent;
    private TextView headerDateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_view);

        receivedIntent = getIntent().getStringExtra("passedDate");

        headerDateTV = (TextView) findViewById(R.id.topBarDayView);
        headerDateTV.setText(receivedIntent);

        headerDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
