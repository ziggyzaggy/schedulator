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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;


public class CalendarView extends Activity {

	public Calendar month;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> items; // container to store some random calendar items
    public Dictionary<String, String> events;
    public ArrayList<String> headerTitles;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar);
	    month = Calendar.getInstance();
	    onNewIntent(getIntent());
	    
	    items = new ArrayList<String>();
        events = new Hashtable<String, String>();
        headerTitles = new ArrayList<String>();
	    adapter = new CalendarAdapter(this, month);
	    
	    final GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(adapter);
	    
	    handler = new Handler();
	    handler.post(calendarUpdater);
	    
	    TextView title  = (TextView) findViewById(R.id.title);
	    title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	    
	    TextView previous  = (TextView) findViewById(R.id.previous);
	    previous.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
				}
				refreshCalendar();
			}
		});
	    
	    TextView next  = (TextView) findViewById(R.id.next);
	    next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
				}
				refreshCalendar();
				
			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //loop through all children of the gridview and set them to a colour
                //else, the previously selected items will stay as chosen colors - something we don't wanna happen
                int childCount = gridview.getChildCount();
                for(int i = 0; i < childCount; i++){
                    TextView tv =(TextView) gridview.getChildAt(i).findViewById(R.id.date);
                    tv.setTextColor(Color.parseColor("#7a7a7a"));

                    gridview.getChildAt(i).setBackgroundResource(R.drawable.back);
                }
                //set the date text color of the clicked item
		    	TextView date = (TextView)v.findViewById(R.id.date);
		        date.setTextColor(Color.WHITE);
                v.setBackgroundResource(R.drawable.back_selected);

		    }
		});

        //obsolete ignore
        /*
        gridview.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                TextView tvcoords = (TextView) v.findViewById(R.id.xycoords);
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        tvcoords.setText(" x - " + event.getX());

                    break;
                }

                return false;
            }
        });
        */
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = (TextView)v.findViewById(R.id.date);
               // int parseint = Integer.parseInt(date.getText().toString()); //parse date text into an integer for future comparisons
                //use tryparse to determine if the selected item is actually a date
                String dateText = date.getText().toString();
                boolean isNum = tryParse(dateText);

                if(date instanceof TextView && !date.getText().equals("") ) {

                    Intent intent = new Intent();
                    String day = date.getText().toString();
                    if(day.length()==1) {
                        day = "0"+day;
                    }
                    // return chosen date as string format
                    intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day);
                    setResult(RESULT_OK, intent);
                    v.playSoundEffect(SoundEffectConstants.CLICK);
                    finish();
                }



                return false;
            }
        });


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
		handler.post(calendarUpdater); // generate some random calendar items				
		
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
			// format random values. You can implement a dedicated class to provide real values
			for(int i=0;i<31;i++) {
				Random r = new Random();
				
				if(r.nextInt(10)>6)
				{
                    Log.i("integer", " " + i);
					items.add(Integer.toString(i));
                    //generate number of events into the events hashtable
                    events.put(Integer.toString(i), Integer.toString(i+20));
				}
			}


            headerTitles.add("Sun");
            headerTitles.add("Mon");
            headerTitles.add("Tue");
            headerTitles.add("Wed");
            headerTitles.add("Thu");
            headerTitles.add("Fri");
            headerTitles.add("Sat");

			adapter.setItems(items, events, headerTitles);
			adapter.notifyDataSetChanged();
		}
	};
}
