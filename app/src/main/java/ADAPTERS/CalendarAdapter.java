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

package ADAPTERS;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.examples.android.calendar.R;

import java.util.ArrayList;
import java.util.Calendar;

import ENTITIES.Event;

public class CalendarAdapter extends BaseAdapter {
	static final int FIRST_DAY_OF_WEEK = 1; // Sunday = 0, Monday = 1
	
	
	private Context mContext;
    private viewHolder holder;

    private java.util.Calendar month;
    private Calendar selectedDate;
    private ArrayList<Event> eventObjs;

    
    public CalendarAdapter(Context c, Calendar monthCalendar) {
    	month = monthCalendar;
    	selectedDate = (Calendar)monthCalendar.clone();
    	mContext = c;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.eventObjs = new ArrayList<>();
        refreshDays();
    }
    
    public void setItems(ArrayList<Event> eventies) {
       /* for(int i = 0;i != events.size();i++){
            for(int j = 0; j < events.get(i).size(); j++){
                if(events.get(i).get(j).length() == 1){
                    events.get(i).put(Integer.toString(j), events.get(i).get(j));
                }
            }
        }*/
        this.eventObjs = eventies;
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
        holder = null;

        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);

            holder = new viewHolder();
            holder.dateTV = (TextView) v.findViewById(R.id.date);

            holder.indicLayout = (RelativeLayout) v.findViewById(R.id.indicatorContainer);
            holder.bIndicator = (TextView) v.findViewById(R.id.blueIndicator);
            holder.rIndicator = (TextView) v.findViewById(R.id.redIndicator);
            holder.gIndicator = (TextView) v.findViewById(R.id.greenIndicator);
            holder.bind = (TextView) v.findViewById(R.id.bind);

            v.setTag(holder);
        	
        }else{
            holder = (viewHolder) v.getTag();
        }




        
        // disable empty days from the beginning
        if(days[position].equals("")) {
        	holder.dateTV.setClickable(false);
            holder.dateTV.setFocusable(false);
            v.setBackgroundResource(R.drawable.back); //remove back from empty days
            v.setClickable(false);
        }
        else {
        	// mark current day as focused
        	if(month.get(Calendar.YEAR)== selectedDate.get(Calendar.YEAR) && month.get(Calendar.MONTH)== selectedDate.get(Calendar.MONTH) && days[position].equals(""+selectedDate.get(Calendar.DAY_OF_MONTH))) {

                holder.dateTV.setTextColor(Color.parseColor("#526AF2"));
                v.setBackgroundResource(R.drawable.back);
        	}
        	else {
        		v.setBackgroundResource(R.drawable.back);
                holder.dateTV.setTextColor(mContext.getResources().getColor(R.color.mainGrayText));;
        	}
        }


        /*select a concrete item
        if(position == 6){
            v.setBackgroundResource(R.drawable.back_no_right_border);
        }
*/
        holder.dateTV.setText(days[position]);
        
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


        //this is where the date icons are getting set for the correct dates/months
        //months are distinquished numerically i.e. 0-januery 11-december

        Boolean isThisYear = false;
        Boolean isThisMonth = false;
        Boolean isThisDay = false;
        Boolean hasMinePending = false;
        Boolean hasAccepted = false;
        Boolean hasPending = false;
        String y = Integer.toString(month.get(Calendar.YEAR));
        String m = Integer.toString(month.get(Calendar.MONTH)) ;
        String d = days[position];
        int bindPos = 0;
        int actualPos = 0;


        if(eventObjs.size() > 0) {

            for (Event e : eventObjs) {
                if (y.equals(e.getYear())) {
                    isThisYear = true;

                    if (m.equals(e.getMonth())) {
                        isThisMonth = true;

                        if (d.equals(e.getDay())) {
                            isThisDay = true;
                            actualPos = bindPos;

                            if (e.isHasMinePending())
                                hasMinePending = true;

                            if (e.isHasAccepted())
                                hasAccepted = true;

                            if (e.isHasPending())
                                hasPending = true;
                        }
                    }
                }

                bindPos++;
            }
        }

        if(date.length()>0 && eventObjs!=null && isThisDay && isThisMonth && isThisYear) {

            holder.bind.setText("" + actualPos);

            //does that even work?
            holder.boundInt = actualPos;

            holder.indicLayout.setVisibility(View.VISIBLE);
            if(hasMinePending) {

                holder.rIndicator.setVisibility(View.VISIBLE);
            }
            if(hasAccepted){

                holder.gIndicator.setVisibility(View.VISIBLE);
            }
            if(hasPending){
                holder.bIndicator.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.indicLayout.setVisibility(View.GONE);
        }


        //customize headers

        for(int i = 0; i < 7; i++){
            //set mon-friday
            if(position == i) {
                //set generic header background resources
                //dayView.setTextSize(25);
                holder.dateTV.setTextColor(mContext.getResources().getColor(R.color.mainBlue));
                //set sat, sun
                if(i == 5 || i == 6){
                    holder.dateTV.setTextColor(mContext.getResources().getColor(R.color.mainRed));
                }
            }
        }
        return v;
    }



    public static class viewHolder{
        TextView dateTV;
        RelativeLayout indicLayout;
        TextView bIndicator;
        TextView gIndicator;
        TextView rIndicator;
        TextView bind;
        int boundInt;
    }

    
    public void refreshDays()
    {


    	
    	int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = month.get(Calendar.DAY_OF_WEEK);
        
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
        String[] headers = new String[] {"M", "T", "W", "T", "F", "S", "S"};


        //populate day headers
        System.arraycopy(headers, 0, days, 0, 7);
        // populate days
        int dayNumber = 1;
        int daysLength = days.length;
        for(int i=j-1;i<daysLength;i++) {
        	days[i] = ""+dayNumber;
        	dayNumber++;
        }
    }

    // references to our items
    public String[] days;
}