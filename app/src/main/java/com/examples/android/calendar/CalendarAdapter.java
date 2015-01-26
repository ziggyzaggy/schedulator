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

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class CalendarAdapter extends BaseAdapter {
	static final int FIRST_DAY_OF_WEEK = 1; // Sunday = 0, Monday = 1
	
	
	private Context mContext;

    private java.util.Calendar month;
    private Calendar selectedDate;
    private ArrayList<String> items;
    private Dictionary<String, Hashtable<String, String>> events;
    private ArrayList<String> headerTitles;
    
    public CalendarAdapter(Context c, Calendar monthCalendar) {
    	month = monthCalendar;
    	selectedDate = (Calendar)monthCalendar.clone();
    	mContext = c;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();
        this.events = new Hashtable<String, Hashtable<String, String>>();
        this.headerTitles = new ArrayList<>();
        refreshDays();
    }
    
    public void setItems(ArrayList<String> items, Dictionary<String, Hashtable<String, String>> events, ArrayList<String> headerTitles) {
       /* for(int i = 0;i != events.size();i++){
            for(int j = 0; j < events.get(i).size(); j++){
                if(events.get(i).get(j).length() == 1){
                    events.get(i).put(Integer.toString(j), events.get(i).get(j));
                }
            }
        }*/
    	this.items = items;
        this.events = events;
        this.headerTitles = headerTitles;
    }
    

    public int getCount() {
        return days.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
    	TextView dayView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);
        	
        }
        dayView = (TextView)v.findViewById(R.id.date);



        
        // disable empty days from the beginning
        if(days[position].equals("")) {
        	dayView.setClickable(false);
        	dayView.setFocusable(false);
            v.setBackgroundResource(R.drawable.back); //remove back from empty days
            v.setClickable(false);
        }
        else {
        	// mark current day as focused
        	if(month.get(Calendar.YEAR)== selectedDate.get(Calendar.YEAR) && month.get(Calendar.MONTH)== selectedDate.get(Calendar.MONTH) && days[position].equals(""+selectedDate.get(Calendar.DAY_OF_MONTH))) {
        		//v.setBackgroundResource(R.drawable.item_background_focused);
                v.setBackgroundResource(android.R.color.transparent);
                ((TextView) v.findViewById(R.id.date)).setTextColor(Color.parseColor("#526AF2"));
                ((TextView) v.findViewById(R.id.date_icon)).setBackgroundColor(Color.parseColor("#526AF2"));
                v.setBackgroundResource(R.drawable.back);
        	}
        	else {
        		v.setBackgroundResource(R.drawable.back);
                ((TextView) v.findViewById(R.id.date)).setTextColor(Color.parseColor("#7a7a7a"));
                ((TextView) v.findViewById(R.id.date_icon)).setBackgroundColor(Color.parseColor("#aaaaaa"));
        	}
        }


        /*select a concrete item
        if(position == 6){
            v.setBackgroundResource(R.drawable.back_no_right_border);
        }
*/
        dayView.setText(days[position]);
        
        // create date string for comparison
        String date = days[position];
    	
        if(date.length()==1) {
    		date = "0"+date;
    	}
    	String monthStr = ""+(month.get(Calendar.MONTH)+1);
    	if(monthStr.length()==1) {
    		monthStr = "0"+monthStr;
    	}
       
        // show icon if date is not empty and it exists in the items array
        TextView iw = (TextView)v.findViewById(R.id.date_icon);
        //this is where the date icons are getting set for the correct dates/months
        //months are distinquished numerically i.e. 0-januery 11-december

       // int key = Integer.parseInt(((Map.Entry) events.get(Integer.toString(month.get(Calendar.MONTH))).entrySet()).getKey().toString());

        Boolean isThisMonth = false;
        Boolean isThisDay = false;
        String m = Integer.toString(month.get(Calendar.MONTH)) ;
        String d = days[position];
        Hashtable<String, String> yo = new Hashtable<>();
        if(events.get(m) != null) {
            isThisMonth = true;

            yo = events.get(m);
            if(yo.get(d) != null){
                isThisDay = true;
            }
        }
        //Hashtable<String, String> t = events.get(Integer.toString(month.get(Calendar.MONTH)));

        if(date.length()>0 && events!=null && isThisDay && isThisMonth) {
        	iw.setVisibility(View.VISIBLE);
            iw.setText(events.get(m).get(d)); //sets text to events number
        }
        else {
        	iw.setVisibility(View.INVISIBLE);
        }


        //customize headers

        for(int i = 0; i < 7; i++){
            //set mon-friday
            if(position == i) {
                //set generic header background resources
                dayView.setTextSize(25);
                iw.setVisibility(View.GONE); //remove the date_icons from headers (GONE doesn't take up the space for the element)
                dayView.setTextColor(mContext.getResources().getColor(R.color.mainBlue));
                //set sat, sun
                if(i == 5 || i == 6){
                    dayView.setTextColor(mContext.getResources().getColor(R.color.mainRed));
                }
            }
        }
        return v;
    }
    
    public void refreshDays()
    {
    	// clear items
    	items.clear();
    	
    	int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = (int)month.get(Calendar.DAY_OF_WEEK);
        
        // figure size of the array
        if(firstDay==1){
        	days = new String[lastDay+(FIRST_DAY_OF_WEEK*6)+7];
        }
        else {
        	//days = new String[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1)];
            days = new String[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1) + 7];
        }
        
        int j=FIRST_DAY_OF_WEEK;
        
        // populate empty days before first real day (remove + 7 to remove the header hogus bogus)
        if(firstDay>1) {
	        for(j=0;j<firstDay-FIRST_DAY_OF_WEEK+7;j++) {
	        	days[j] = "";
	        }
        }
	    else {
	    	for(j=0;j<FIRST_DAY_OF_WEEK*13;j++) {
	        	days[j] = "";
	        }
	    	j=FIRST_DAY_OF_WEEK*6+8; // sunday => 2, monday => 8 //orig.j=FIRST_DAY_OF_WEEK*6+1
	    }

        //array of headers
        String[] headers = new String[] {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};


        //populate day headers
        for(int i = 0; i < 7; i++ ){

            days[i] = headers[i];

        }
        // populate days
        int dayNumber = 1;
        for(int i=j-1;i<days.length;i++) {
        	days[i] = ""+dayNumber;
        	dayNumber++;
        }
    }

    // references to our items
    public String[] days;
}