package com.examples.android.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.transition.Explode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ENTITIES.Event;
import StaticUtils.Utils;

/**
 * Created by ziggyzaggy on 23/02/2015.
 *
 * This is where the users will be able to see the schedule for the day, add new events etc etc
 */
public class DayView extends Activity{

    private String receivedIntent;
    private TextView headerDateTV;
    private Event eventObj;
    private float x1,x2;


    private ScrollView mRootScrollView;
    static final int MIN_DISTANCE = 250;

    @Override
    public void finish() {
        //override finish method of activity to override the default exit animation
        super.finish();
        DayView.this.overridePendingTransition(R.anim.entering_fade_in, R.anim.exit_to_right_fancy);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_view);
        LinearLayout root = (LinearLayout) findViewById(R.id.rootDayView);
        receivedIntent = getIntent().getStringExtra("passedDate");
        eventObj = getIntent().getParcelableExtra("eventObj");

        headerDateTV = (TextView) findViewById(R.id.topBarDayView);
        headerDateTV.setText(receivedIntent);

        mRootScrollView = (ScrollView) findViewById(R.id.rootScrollDayView);


        //ensure that an event was passed from parent activity
        //if it wasn't then user had chosen a blank day with no events
        if(eventObj == null){

           // View v = (View) findViewById(R.id.dayViewIncludedTimesView);
            //remove the times from root view
           // root.removeView(v);


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

        }else{
            setUpTimes();
        }

        headerDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.time1am);

                tv.setText("accepted " + eventObj.getNumAccepted());
            }
        });


        mRootScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //region swipie swipie


                boolean isX = false;


                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();


                        break;



                    case MotionEvent.ACTION_MOVE:


                      /*  x2 = event.getX(); make the whole view pullable, will finsih if good few hours to waste
                        float moveDeltaX = x2-x1;

                        int negativePull = 5;

                        if(!(Math.round(moveDeltaX) > MIN_DISTANCE)){
                            mRootScrollView.setPadding(Math.round(moveDeltaX),
                                    mRootScrollView.getPaddingTop(),
                                    - Math.round(moveDeltaX),
                                    mRootScrollView.getPaddingBottom());
                        }else{
                            if (negativePull > 0){
                             negativePull -= moveDeltaX;
                            }

                            if(negativePull < 0){
                                negativePull = 1;
                            }
                            mRootScrollView.setPadding(Math.round(moveDeltaX) - negativePull,
                                    mRootScrollView.getPaddingTop(),
                                    -negativePull,
                                    mRootScrollView.getPaddingBottom());
                        }

                        if(moveDeltaX > 0){
                            isX = true;
                        }
                        headerDateTV.setText("" + Math.round(moveDeltaX));

                    */



                        break;
                    //endregion
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE)
                        {

                            // Left to Right swipe action
                            if (x2 > x1)
                            {
                                //close activity with left to right swipe
                                finish();

                            }

                            // Right to left swipe action
                            else
                            {

                            }
                        }
                        break;
                }
                return isX;
                //endregion
            }
        });



    }

    //method to populate the times dynamically
    private void setUpTimes(){
        ViewGroup parent = (ViewGroup) findViewById(R.id.rootDayView);

        for (int i = 1; i < 25; i++){
            //inflate layout from a layout xml, view returned into retview is the actual inflated view
            View retView = LayoutInflater.from(this).inflate(R.layout.single_time, parent, false);
            //set header text
            TextView head = (TextView) retView.findViewById(R.id.timeHeaderTV);
            head.setText(i +" PM");

            RelativeLayout container = (RelativeLayout) retView.findViewById(R.id.actualTimesWrapper);

            View retRealTimeView = LayoutInflater.from(this).inflate(R.layout.single_event_preview, container, false);
            ((TextView)retRealTimeView.findViewById(R.id.realTimeTV)).setText("whatever:15");
            ((TextView)retRealTimeView.findViewById(R.id.realTimeDescTV)).setText("test");


            //Test stage, the first times layout should have same height and same top margin as the next ones, if the do on various screen sizes then the current dp conversion algorithm is correct
            //WHERES MY RULER
            //total height of an hours container = 300dp = 60 minutes -> 300/60 = 5dp = 1 minutes
            retRealTimeView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 45 * (int)getResources().getDisplayMetrics().density));
            RelativeLayout.LayoutParams realtimeViewMarginParams = (RelativeLayout.LayoutParams) retRealTimeView.getLayoutParams();
            realtimeViewMarginParams.setMargins(0, 75 * (int)getResources().getDisplayMetrics().density, 0, 0);// eg. this event starts at 15 minutes past hour
            retRealTimeView.setLayoutParams(realtimeViewMarginParams);

            container.addView(retRealTimeView);

            parent.addView(retView);//add the view to the parent
        }


    }

}
