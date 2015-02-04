/*
* Copyright 2011 Lauri Nevala.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.examples.android.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import HELPERS.DateHelper;


public class CalendarView extends Activity {

	public Calendar month;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> items; // container to store some random calendar items
    public Dictionary<String, Hashtable<String, String>> events;
    public ArrayList<String> headerTitles;
	public JSONObject jobj;

    private float x1,x2;
    static final int MIN_DISTANCE = 150;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar);
	    month = Calendar.getInstance();
	    onNewIntent(getIntent());


	    
	    items = new ArrayList<String>();
        events = new Hashtable<String, Hashtable<String, String>>();
        headerTitles = new ArrayList<String>();
	    adapter = new CalendarAdapter(this, month);
	    
	    final GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(adapter);
	    
	    handler = new Handler();
	    handler.post(calendarUpdater);
	    
	    TextView title  = (TextView) findViewById(R.id.title);
	    title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        try {
            makeTest();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        TextView previous  = (TextView) findViewById(R.id.previous);
	    previous.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				prevMonth();
			}
		});
	    
	    TextView next  = (TextView) findViewById(R.id.next);
	    next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
            nextMonth();
				
			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = (TextView)v.findViewById(R.id.date);


                String dateText = date.getText().toString();
                boolean isNum = tryParse(dateText);

                if(date instanceof TextView && !date.getText().equals("") && isNum ) {
                    setSelectedBackground(v, gridview);


                    if(((RelativeLayout)v.findViewById(R.id.indicatorContainer)).getVisibility() == View.VISIBLE){
                        showDetailContainer(findViewById(R.id.dateDetailContainer), true);
                    }

                    Calendar d = Calendar.getInstance();
                    d.set(month.get(Calendar.YEAR), month.get(Calendar.MONTH), Integer.parseInt(dateText));
                    //d.setFirstDayOfWeek(Calendar.MONDAY); //NOTE set this to ignore locale and instead use mondays as first day of week on all locales
                    int ddd = d.get(Calendar.WEEK_OF_MONTH);
                    TextView weekText = (TextView) findViewById(R.id.weekText);
                    weekText.setText("Week " + ddd);
                }
		    }
		});


        gridview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                    //switch months on swipe
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            x1 = event.getX();
                            break;
                        case MotionEvent.ACTION_UP:
                            x2 = event.getX();
                            float deltaX = x2 - x1;
                            if (Math.abs(deltaX) > MIN_DISTANCE)
                            {
                                // Left to Right swipe action
                                if (x2 > x1)
                                {
                                   // Toast.makeText(getApplicationContext(), "Left to Right swipe [Next] " + "x1 " + x1 + " x2 " + x2 , Toast.LENGTH_SHORT).show ();
                                    prevMonth();
                                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_left);
                                    gridview.startAnimation(anim);
                                }

                                // Right to left swipe action
                                else
                                {
                                    //Toast.makeText(getApplicationContext(), "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                                    nextMonth();
                                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_right);
                                    gridview.startAnimation(anim);
                                }

                            }
                            break;
                    }
                    return false;

            }
        });





        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = (TextView) v.findViewById(R.id.date);
                //use tryparse to determine if the selected item is actually a date
                String dateText = date.getText().toString();
                boolean isNum = tryParse(dateText);


                if (date instanceof TextView && !date.getText().equals("") && isNum) {
                    setSelectedBackground(v, gridview);
                    Intent intent = new Intent();
                    String day = date.getText().toString();

                    if (day.length() == 1) {
                        day = "0" + day;
                    }
                    // return chosen date as string format
                    intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month) + "-" + day);
                    setResult(RESULT_OK, intent);
                    v.playSoundEffect(SoundEffectConstants.CLICK);
                    //TODO - TEST ON DEVICES WITH NO VIBR
                    Vibrator vibr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibr.vibrate(20); //vibrate for whatever millis
                    finish();

                }
                return false;
            }
        });

        //clikc listeners of green circle
        TextView greenCircle = (TextView) findViewById(R.id.circle_green);
        greenCircle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Green circle clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //clikc listeners of red circle
        TextView redCircle = (TextView) findViewById(R.id.circle_red);
        redCircle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Red circle clicked", Toast.LENGTH_SHORT).show();
            }
        });
	}




    private void nextMonth(){
        if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {
            month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
        } else {
            month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
        }
        refreshCalendar();
    }

    private void prevMonth(){
        if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {
            month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
        } else {
            month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
        }
        refreshCalendar();


    }

    private void makeTest() throws JSONException {
        DateHelper helper = new DateHelper(this, (TextView)findViewById(R.id.weekText), jobj, this);
        helper.runAsyncGetter(jobj);
        JSONObject temp = jobj;

    }



    private void showDetailContainer(View v, boolean show){

        LinearLayout tv = (LinearLayout) v;
        Animation anim = null;
        if(show) {
            tv.setVisibility(View.VISIBLE);
            anim = AnimationUtils.loadAnimation(this, R.anim.from_left);
        }else{
            tv.setVisibility(View.INVISIBLE);

        }
        tv.startAnimation(anim);

    }


    //set background color to selected view in the gridview, set to defaults the non selected items
    private void setSelectedBackground(View v, GridView gridview ){
        //loop through all children of the gridview and set them to a colour
        //else, the previously selected items will stay as chosen colors - something we don't wanna happen
        //(ignore first 7 elements as these are day headers)
                int childCount = gridview.getChildCount();
                for(int i = 7; i < childCount; i++){
                    TextView tv =(TextView) gridview.getChildAt(i).findViewById(R.id.date);
                    //dont change text color of current date
                    if(tv.getCurrentTextColor() != -11375886) {
                        tv.setTextColor(Color.parseColor("#7a7a7a"));
                    }


                    gridview.getChildAt(i).setBackgroundResource(R.drawable.back);
                }
                //set the date text color of the clicked item
		    	TextView date = (TextView)v.findViewById(R.id.date);
		        date.setTextColor(Color.WHITE);
                v.setBackgroundResource(R.drawable.back_selected);


    }

    //a simple implementation of C# tryparse() function,
    //returns true if string can be parsed into integer
    public boolean tryParse(String str){
        try
        {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException nfe)
        {
            return false;
        }
    }


	public void refreshCalendar()
	{
		TextView title  = (TextView) findViewById(R.id.title);
		
		adapter.refreshDays();
		adapter.notifyDataSetChanged();				
		handler.post(calendarUpdater);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}
	
	public void onNewIntent(Intent intent) {
		String date = intent.getStringExtra("date");
		String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
		month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
	}
	
	public Runnable calendarUpdater = new Runnable() {
		
		@Override
		public void run() {
			items.clear();

            JSONObject temp = jobj;

            try {
                if(temp != null) {//test show the date string on the ui -- that is, if the temp isn't empty

                    String d = "";
                    String fullS = "";


                    //loop through all of the json dates and put them into events hashtable
                    for(int i =0; i < temp.getJSONArray("dates").length(); i++){


                        fullS += " " + temp.getJSONArray("dates").getJSONObject(i).getString("date");
                        String numEvents = temp.getJSONArray("dates").getJSONObject(i).getString("count");//get number of events for date


                        Hashtable t = new Hashtable<String, String>();
                        d = temp.getJSONArray("dates").getJSONObject(i).getString("date");//get dates
                        String[] parts = d.split("-");
                        String month = Integer.toString(Integer.parseInt(parts[1]) - 1);
                        String day = parts[2];


                        t.put(day, numEvents);
                        events.put(month, t);//dates work yaayklhlhl

                    }

                    ((TextView) findViewById(R.id.weekText)).setText(fullS);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //hastables of day => number of events
            Hashtable table = new Hashtable<String, String>();
            Hashtable table2 = new Hashtable<String, String>();
            table.put("20", "5");
            table.put("24", "6");
            table2.put("5", "15");
            table2.put("10", "20");
            //concrete days with number of events


            //hashtables of month => day/events hashtable
           // events.put("0", table);
            events.put("2", table2);

			adapter.setItems(events);
			adapter.notifyDataSetChanged();
		}
	};
}
