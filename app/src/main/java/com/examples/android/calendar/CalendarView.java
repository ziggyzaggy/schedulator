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
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

import ADAPTERS.CalendarAdapter;
import ADAPTERS.FriendListAdapter;
import ADAPTERS.GroupsAdapter;
import ENTITIES.Event;
import HELPERS.AnimHelper;
import HELPERS.DateHelper;
import StaticUtils.Utils;


public class CalendarView extends Activity {

	public Calendar month;
	public CalendarAdapter adapter;
    public FriendListAdapter friendAdapter;
	public Handler handler;
    ArrayList<Event> eventObjs;
	public JSONObject jobj;

    private TextView titleText;
    private float x1,x2;

    private Boolean isRightDrawerOpened;
    private ArrayList<Integer> curSelectedFriends;
    private DrawerLayout mDrawerLayout;
    private ListView mRightDrawerList;
    static final int MIN_DISTANCE = 150;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar);
	    month = Calendar.getInstance();
	    onNewIntent(getIntent());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.friends_drawer);
        isRightDrawerOpened = false;

        curSelectedFriends = new ArrayList<>();

        final Animation animFromRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_right);
        final Animation animFromLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_left);
        final Animation animFromLeftLesser = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_left_lesser);

        titleText  = (TextView) findViewById(R.id.title);

        eventObjs = new ArrayList<>();
	    adapter = new CalendarAdapter(this, month);
        friendAdapter = new FriendListAdapter(this);
        GroupsAdapter gAdapter = new GroupsAdapter(this);



        mRightDrawerList = (ListView) findViewById(R.id.right_drawer);
	    mRightDrawerList.setAdapter(friendAdapter);

        ListView mRightGroupsDrawerList = (ListView) findViewById((R.id.right_drawer_groups));
        mRightGroupsDrawerList.setAdapter(gAdapter);

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


		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = (TextView)v.findViewById(R.id.date);


                String dateText = date.getText().toString();
                boolean isNum = Utils.intTryParse(dateText);

                if(date instanceof TextView && !date.getText().equals("") && isNum ) {
                    setSelectedBackground(v, gridview);


                    if(((RelativeLayout)v.findViewById(R.id.indicatorContainer)).getVisibility() == View.VISIBLE){
                        TextView boundField = (TextView) v.findViewById(R.id.bind);
                        setDetailInfo(boundField.getText().toString());
                        showDetailContainer(findViewById(R.id.dateDetailContainer), true);

                    }else{
                        showDetailContainer(findViewById(R.id.dateDetailContainer), false);
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
                    //region swipes
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
                                    gridview.startAnimation(animFromLeft);
                                    titleText.startAnimation(animFromLeft);
                                }

                                // Right to left swipe action
                                else
                                {
                                    //Toast.makeText(getApplicationContext(), "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                                    nextMonth();
                                    gridview.startAnimation(animFromRight);
                                    titleText.startAnimation(animFromRight);
                                }
                            }
                            break;
                    }
                    return false;
                //endregion

            }
        });





        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = (TextView) v.findViewById(R.id.date);
                //use tryparse to determine if the selected item is actually a date
                String dateText = date.getText().toString();
                boolean isNum = Utils.intTryParse(dateText);


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

        TextView friendCircle = (TextView) findViewById(R.id.circle_friends);
        friendCircle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.END);

            }
        });

        //set drawer listener to distinguish between right and left drawer closes
        mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.circle_friends, R.string.drawer_open_desc, R.string.drawer_close_desc){
            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);

                ListView lw = (ListView) findViewById(R.id.right_drawer);
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayoutContainer);

                Rect bounds = new Rect();
                //get bounds of main view
                mainLayout.getHitRect(bounds);
                //if right drawer in bounds of main view
                if(lw.getLocalVisibleRect(bounds)){
                    isRightDrawerOpened = true;
                }

            }

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                if(isRightDrawerOpened){
                    getCheckedFriends();

                    isRightDrawerOpened = false;
                }

            }

        });



        mRightDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //view.setBackgroundColor(getResources().getColor(R.color.mainBlue));

                //Drawable d = view.getBackground().getCurrent();
                //TODO - save position of checked items and set their color

                //TextView name = (TextView)view.findViewById(R.id.nameTV);



               setSelectedListItemBackground(view);

               /* if(name.getCurrentTextColor() == getResources().getColor(R.color.mainBlue)){
                    name.setTextColor(getResources().getColor(R.color.white));
                }else{
                    name.setTextColor(getResources().getColor(R.color.mainBlue));
                }*/

               /* view.setBackgroundResource(R.drawable.list_item_selected);
                Drawable d = view.getBackground();

                if(d == getResources().getDrawable(R.drawable.list_item_selected)){
                    String teeeee = "";
                }*/

                String o = "temp";
            }
        });


        mRightDrawerList.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int mLastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                   //TODO: Do something on scroll down
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    //TODO: do something on scroll up
                }
                mLastFirstVisibleItem=firstVisibleItem;

            }
        });

	}


    private void setSelectedListItemBackground(View v){

        if(v.getBackground() != null) {

            ColorDrawable col = (ColorDrawable) v.getBackground();
            int colCode = col.getColor();
            // Toast.makeText(getApplicationContext(), "" + colCode + " " + getResources().getColor(R.color.mainBlue), Toast.LENGTH_SHORT).show(); //-13388315 ?mainblue?
            if(colCode == getResources().getColor(R.color.mainBlue)){
                v.setBackgroundColor(Color.TRANSPARENT);
            }else{
                v.setBackgroundColor(getResources().getColor(R.color.mainBlue));
            }
        }else{
            v.setBackgroundColor(getResources().getColor(R.color.mainBlue));
        }
    }


    private void getCheckedFriends(){
        SparseBooleanArray checkeds = mRightDrawerList.getCheckedItemPositions();
        //create array to store last checked friends
        ArrayList<Integer> curList = new ArrayList<>();

        boolean needsUpdate = false;//flag if the view needs updating

        String str = "";

        for (int i = 0; i < mRightDrawerList.getAdapter().getCount(); i++) {
            if (checkeds.get(i)) {//get checked friends
                str += "POS " + i +" ";
                curList.add(i);//add position of checked friend to arraylist
            }
        }

        if(curSelectedFriends.size() > 0){
           if(!Utils.equalLists(curList, curSelectedFriends)) {//check lists for equality
               //lists differ - update and save checked friends
               curSelectedFriends = curList;
               needsUpdate = true;
           }
        }else if(curList.size() > 0){
            curSelectedFriends = curList;
            needsUpdate = true;
        }

        Toast.makeText(getApplicationContext(), str + " " + needsUpdate, Toast.LENGTH_SHORT).show();

        if(needsUpdate) {
            try {
                makeTest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    //get bound object from the eventObjs arraylist and set details
    private void setDetailInfo(String text) {
        TextView tv = (TextView) findViewById(R.id.dateDetailContainerText);
        tv.setText(text);

        Event e = eventObjs.get(Integer.parseInt(text));
        tv.setText("Accepted Events: " + e.getNumAccepted() + " Pending events: " + e.getNumPending() + " Mine Pending Events: " + e.getNumMinePending());
    }


    //animates exit/intro of detail container
    private void showDetailContainer(View v, boolean show){

        LinearLayout tv = (LinearLayout) v;
        Animation anim = null;
        if(show) {
            tv.setVisibility(View.VISIBLE);
            anim = AnimationUtils.loadAnimation(this, R.anim.from_left_lesser);
        }else{
            if(tv.getVisibility() == View.VISIBLE) {
                tv.setVisibility(View.INVISIBLE);
                anim = AnimationUtils.loadAnimation(this, R.anim.exit_to_left);
            }

        }
        if(anim != null)
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

    //@deprecated - use Utils#intTryParse Instead
    @Deprecated
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
		adapter.refreshDays();
		adapter.notifyDataSetChanged();				
		handler.post(calendarUpdater);
		titleText.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}
	
	public void onNewIntent(Intent intent) {
		String date = intent.getStringExtra("date");
		String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
		month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
	}
	
	public Runnable calendarUpdater = new Runnable() {
		
		@Override
		public void run() {
            JSONObject temp = jobj;
            eventObjs.clear(); // gotta be careful with the bound field


            try {
                if(temp != null) {//test show the date string on the ui -- that is, if the temp isn't empty

                    String d = "";
                    String fullS = "";


                    //loop through all of the json dates and put them into events hashtable
                    int size = temp.getJSONArray("dates").length();
                    for(int i =0; i < size; i++){


                        fullS += " " + temp.getJSONArray("dates").getJSONObject(i).getString("date");
                        String numEvents = temp.getJSONArray("dates").getJSONObject(i).getString("count");//get number of events for date

                        d = temp.getJSONArray("dates").getJSONObject(i).getString("date");//get dates
                        String[] parts = d.split("-");
                        String month = Integer.toString(Integer.parseInt(parts[1]) - 1);
                        String day = parts[2];

                        //lets try it with an object
                        Event eObj = new Event(day, month, "2015", Integer.parseInt(numEvents), 1, 0);
                        eventObjs.add(eObj);

                    }

                    ((TextView) findViewById(R.id.weekText)).setText(fullS);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Event eTest = new Event("20", "1", "2015", 1,0,1);
            Event eTest2 = new Event("13", "1", "2015", 0,2,5);
            eventObjs.add(eTest);
            eventObjs.add(eTest2);

			adapter.setItems(eventObjs);
			adapter.notifyDataSetChanged();
		}
	};
}
