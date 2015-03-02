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
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
import android.transition.Explode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import ADAPTERS.CalendarAdapter;
import ADAPTERS.FriendListAdapter;
import ADAPTERS.GroupsAdapter;
import ADAPTERS.NavAdapter;
import ENTITIES.Event;
import ENTITIES.Friend;
import GLOBALS.CurrentUser;
import HELPERS.DateHelper;
import HELPERS.ScrollTracker;
import StaticUtils.Utils;


public class CalendarView extends Activity {

	public Calendar month;
	public CalendarAdapter adapter;
    public FriendListAdapter friendAdapter;
    public NavAdapter navAdapter;
	public Handler handler;
    ArrayList<Event> eventObjs;
	public JSONObject jobj;

    private TextView titleText;
    private float x1,x2;
    private ScrollTracker mScrollTracker;
    private int initialFriendsTextHeight;

    private Boolean isRightDrawerOpened;
    private ArrayList<Integer> curSelectedFriends;
    private DrawerLayout mDrawerLayout;
    private ListView mRightDrawerList;
    private TextView mGroupsTV;
    private TextView mFriendsTV;
    private ListView mNavListView;

    private LinearLayout mRightDrawerGroupContainer;
    private RelativeLayout mRightDrawerFriendContainer;
    static final int MIN_DISTANCE = 150;


	public void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar);

        //getWindow().setBackgroundDrawable(null);


	    month = Calendar.getInstance();
	    onNewIntent(getIntent());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.friends_drawer);
        isRightDrawerOpened = false;

        mRightDrawerGroupContainer = (LinearLayout) findViewById(R.id.groupsContainer);
        mRightDrawerFriendContainer = (RelativeLayout) findViewById(R.id.drawerFriendContainer);
        mGroupsTV = (TextView) findViewById(R.id.groupsTV);
        mFriendsTV = (TextView) findViewById(R.id.friendsTV);

        initialFriendsTextHeight = 0;

        curSelectedFriends = new ArrayList<>();

        final Animation animFromRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_right);
        final Animation animFromLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_left);
        final Animation animScaleUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.expand_height_width);

        titleText  = (TextView) findViewById(R.id.title);

        eventObjs = new ArrayList<>();
	    adapter = new CalendarAdapter(this, month);
        friendAdapter = new FriendListAdapter(this);
        GroupsAdapter gAdapter = new GroupsAdapter(this);
        navAdapter = new NavAdapter(this);

        mNavListView = (ListView) findViewById(R.id.nav_drawer_listView);
        mNavListView.setAdapter(navAdapter);



        mRightDrawerList = (ListView) findViewById(R.id.right_drawer);
        View header = getLayoutInflater().inflate(R.layout.friendslist_listview_header, mRightDrawerList, false);
        mRightDrawerList.addHeaderView(header, null, false);

	    mRightDrawerList.setAdapter(friendAdapter);

        mScrollTracker = new ScrollTracker(mRightDrawerList);

        final ListView mRightGroupsDrawerList = (ListView) findViewById((R.id.right_drawer_groups));
        mRightGroupsDrawerList.setAdapter(gAdapter);

	    final GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(adapter);
	    
	    handler = new Handler();
	    handler.post(calendarUpdater);
	    

	    titleText.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        try {
            makeTest();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mFriendsTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mRightDrawerGroupContainer.setVisibility(View.VISIBLE);
                mRightDrawerGroupContainer.startAnimation(animScaleUp);
                mRightDrawerFriendContainer.setVisibility(View.GONE);
            }
        });

        mGroupsTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRightDrawerFriendContainer.setVisibility(View.VISIBLE);
                mRightDrawerFriendContainer.startAnimation(animScaleUp);
                mRightDrawerGroupContainer.setVisibility(View.GONE);
            }
        });


		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = (TextView)v.findViewById(R.id.date);

                String dateText = date.getText().toString();
                boolean isNum = Utils.intTryParse(dateText);

                if(date instanceof TextView && !date.getText().equals("") && isNum ) {
                    setSelectedBackground(v, gridview);


                    if(v.findViewById(R.id.indicatorContainer).getVisibility() == View.VISIBLE){
                        //TODO: test this shit, should replace the bind textview and instead use the selected views 'memory'
                        if(v.getTag() != null){CalendarAdapter.viewHolder holder = (CalendarAdapter.viewHolder) v.getTag();}


                        TextView boundField = (TextView) v.findViewById(R.id.bind);
                        setDetailInfo(boundField.getText().toString());
                        showDetailContainer(findViewById(R.id.dateDetailContainerText), true);

                    }else{
                        showDetailContainer(findViewById(R.id.dateDetailContainerText), false);
                    }

                    Calendar d = Calendar.getInstance();
                    d.set(month.get(Calendar.YEAR), month.get(Calendar.MONTH), Integer.parseInt(dateText));
                    //d.setFirstDayOfWeek(Calendar.MONDAY); //NOTE set this to ignore locale and instead use mondays as first day of week on all locales
                    int ddd = d.get(Calendar.WEEK_OF_MONTH);
                    TextView weekText = (TextView) findViewById(R.id.weekText);
                    weekText.setText("Week " + ddd  + " " + CurrentUser.name);
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
                                    prevMonth();
                                    gridview.startAnimation(animFromLeft);
                                    titleText.startAnimation(animFromLeft);
                                }

                                // Right to left swipe action
                                else
                                {
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
                String dateText = date.getText().toString();
                boolean isNum = Utils.intTryParse(dateText);


                if (date instanceof TextView && !date.getText().equals("") && isNum) {
                    setSelectedBackground(v, gridview);
                    //Intent intent = new Intent();
                    String day = date.getText().toString();

                    if (day.length() == 1) {
                        day = "0" + day;
                    }
                    // return chosen date as string format
                  /*  intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month) + "-" + day);
                    setResult(RESULT_OK, intent);
                    v.playSoundEffect(SoundEffectConstants.CLICK);
                    //TODO - TEST ON DEVICES WITH NO VIBR
                    Vibrator vibr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibr.vibrate(20); //vibrate for whatever millis
                    finish();*/

                    Vibrator vibr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibr.vibrate(20); //vibrate for whatever millis

                    Intent dayViewIntent = new Intent(v.getContext(), DayView.class);
                    dayViewIntent.putExtra("passedDate", day + "-" + android.text.format.DateFormat.format("MM-yyyy", month));

                    //make sure thtat the selected date has any events bound to it
                    if(v.findViewById(R.id.indicatorContainer).getVisibility() == View.VISIBLE) {
                        TextView boundField = (TextView) v.findViewById(R.id.bind);
                        //get the object from arraylist according to the bound key
                        Event event = getDetailInfo(boundField.getText().toString());
                        dayViewIntent.putExtra("eventObj", event);
                    }
                    startActivity(dayViewIntent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left_fancy);

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

        final TextView friendCircle = (TextView) findViewById(R.id.circle_friends);
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
                    //getCheckedFriends();
                    //artificial delay to start updating the calendar after the drawer closing animation is finished
                    mDrawerLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getCheckedFriends();
                        }
                    }, 200);
                    isRightDrawerOpened = false;
                }

            }

        });


        mNavListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navAdapter.setSelectedIndex(position);
            }
        });



        mRightDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectedListItemBackground();

                //region deprecated
                //view.setBackgroundColor(getResources().getColor(R.color.mainBlue));

                //Drawable d = view.getBackground().getCurrent();
                //TODO - save position of checked items and set their color

                //TextView name = (TextView)view.findViewById(R.id.nameTV);





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
                //endregion
            }
        });


        mRightDrawerList.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int mLastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("SCROLL", "view scroll y " + view.getScrollY());
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {


                int offset = mScrollTracker.calculateIncrementalOffset(firstVisibleItem, visibleItemCount);
                if(initialFriendsTextHeight == 0){
                    initialFriendsTextHeight = mFriendsTV.getHeight();
                }

                if(offset > initialFriendsTextHeight){
                    offset = initialFriendsTextHeight;
                }

                int curHeight = mFriendsTV.getHeight();

                if(offset > 0 && curHeight < initialFriendsTextHeight){
                    mFriendsTV.setHeight( curHeight + offset);
                }

                if(offset < 0 && curHeight > 0){
                    mFriendsTV.setHeight( curHeight + offset);
                }

                if(curHeight > initialFriendsTextHeight){
                    mFriendsTV.setHeight(initialFriendsTextHeight);
                }

                Log.v("Offset", " " + offset);
                Log.v("Height", " " + mFriendsTV.getHeight() + " H " + initialFriendsTextHeight);


                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                   //TODO: Do something on scroll down

                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {

                    //scroll up

                }
                mLastFirstVisibleItem=firstVisibleItem;

            }
        });

	}


    private void setSelectedListItemBackground(){
        sendCheckedFriendsToAdapter();
    }


    private void sendCheckedFriendsToAdapter(){
        SparseBooleanArray checkeds = mRightDrawerList.getCheckedItemPositions();
        //create array to store last checked friends
        ArrayList<Integer> curList = new ArrayList<>();

        for (int i = 0; i < mRightDrawerList.getAdapter().getCount(); i++) {
            //IMPORTANT: NOTE that checked is incremented by one, this is to overcome the deficiency of android assigning a position for the header in the listview
            //because the header takes up the position - the checked items returned are also wrong
            //this is very dirty, but will have to do until i find a better solution
            if (checkeds.get(i+1)) {//get checked friends
                curList.add(i);//add position of checked friend to arraylist
            }
        }

        friendAdapter.updateCheckedFriends(curList);

    }

    private void getCheckedFriends(){
        SparseBooleanArray checkeds = mRightDrawerList.getCheckedItemPositions();
        //create array to store last checked friends
        ArrayList<Integer> curList = new ArrayList<>();

        boolean needsUpdate = false;//flag if the view needs updating

        String str = "";

        for (int i = 0; i < mRightDrawerList.getAdapter().getCount(); i++) {
            //NOTE the increment of 1, please see  "IMPORTANT NOTE" in sendCheckedFriendsToAdapter()
            if (checkeds.get(i+1)) {//get checked friends
                Friend friend =(Friend) friendAdapter.getItem(i);
                str += "-POS " + i +" UserName " + friend.getName() + "-**";
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


    }

    //get bound object from the eventObjs arraylist and set details
    private void setDetailInfo(String text) {
        TextView tv = (TextView) findViewById(R.id.dateDetailContainerText);
        tv.setText(text);

        Event e = eventObjs.get(Integer.parseInt(text));
        tv.setText("Accepted Events: " + e.getNumAccepted() + " Pending events: " + e.getNumPending() + " Mine Pending Events: " + e.getNumMinePending());
    }

    private Event getDetailInfo(String boundKey){
        Event e = eventObjs.get(Integer.parseInt(boundKey));

        if(e != null){
            return e;
        }

        return null;
    }


    //animates exit/intro of detail container
    private void showDetailContainer(View v, boolean show){

        TextView tv = (TextView) v;
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
