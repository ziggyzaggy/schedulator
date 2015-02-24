package com.examples.android.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ENTITIES.Event;

/**
 * Created by ziggyzaggy on 23/02/2015.
 *
 * This is where the users will be able to see the schedule for the day, add new events etc etc
 */
public class DayView extends Activity{

    private String receivedIntent;
    private TextView headerDateTV;
    private Event eventObj;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_view);

        receivedIntent = getIntent().getStringExtra("passedDate");
        eventObj = getIntent().getParcelableExtra("eventObj");

        headerDateTV = (TextView) findViewById(R.id.topBarDayView);
        headerDateTV.setText(receivedIntent);


        //ensure that an event was passed from parent activity
        //if it wasn't then user had chosen a blank day with no events
        if(eventObj == null){
            LinearLayout root = (LinearLayout) findViewById(R.id.rootDayView);
            View v = (View) findViewById(R.id.dayViewIncludedTimesView);
            //remove the times from root view
            root.removeView(v);


            String colorValue = "#" + getResources().getString(R.color.mainBlue).substring(3);

            //let user know that there are no events for that date
            TextView nullTextView = new TextView(this);
            nullTextView.setText(Html.fromHtml("Add a <u><font color='" + colorValue +"'>New</font></u> Event"));
            nullTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            nullTextView.setGravity(Gravity.CENTER);
            nullTextView.setTextSize(25);
            nullTextView.setPadding(0,50,0,50);
            nullTextView.setHighlightColor(getResources().getColor(R.color.mainBlue));
            nullTextView.setTextColor(getResources().getColorStateList(R.color.clickable_textview_states));

            nullTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Unimplemented: new event create", Toast.LENGTH_SHORT).show();
                }
            });


            root.addView(nullTextView);

        }

        headerDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.time1am);

                tv.setText("accepted " + eventObj.getNumAccepted());
            }
        });

    }
}
