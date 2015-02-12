package ADAPTERS;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examples.android.calendar.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ENTITIES.Friend;
import ENTITIES.User;

/**
 * Created by ziggyzaggy on 09/02/2015.
 */
public class FriendListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Friend> friendsList;
    private TextView nameTV;
    private TextView avatarTV;

    public FriendListAdapter(Context c){
        mContext = c;
        friendsList = new ArrayList<>();
        makeFriends(100);

    }

    public void makeFriends(int number){
        for(int i = 0; i < number; i++){
            Friend f = new Friend(i, false, "user no " + i, "example@example.com");
            friendsList.add(f);




        }

    }

    public void updateCheckedFriends(ArrayList<Integer> checkedFriendsList){
        //uncheck all users
        for(int j = 0; j < friendsList.size(); j++){
            friendsList.get(j).setCheckedInList(false);
        }
        //check users at positions at the checkedFriendsList positions
        for(int i : checkedFriendsList){
            friendsList.get(i).setCheckedInList(true);
        }
        notifyDataSetChanged();
    }

    //NOTE: getViewTypeCount and getItemViewType methods basically are telling the adapter to never recycle the views,
    // this fixes the problems with the row states (highlighting of selected items in the list)
    // but could result in using up too much memory (e.g. 100 firends results in approx 65 mb of allocated memory !!! vs ~15mb on recycled views),
    //(500 friends results in ~200mb of memory and scrolling freezes)
    // should implement and keep actual states in the entity

    //update - entity User now holds checked state,
    // updateCheckedFriends and getView methods are now responsible of keeping the views checked
    // memory usage back to normal
    //keep above note for future possible android quirks
/*
    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
*/

    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        viewHolder holder = null;

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.friend_list_item, null);
            holder = new viewHolder();
            holder.nameTV = (TextView) v.findViewById(R.id.nameTV);
            holder.avatarTv = (TextView) v.findViewById(R.id.avatarTV);
            v.setTag(holder);
        }else{
            holder = (viewHolder) v.getTag();
        }

        holder.nameTV.setText(friendsList.get(position).getName());
        holder.avatarTv.setBackgroundResource(R.drawable.ic_contact_picture);
        int colorToSet = 0;

        if(friendsList.get(position).isCheckedInList()){
            colorToSet = mContext.getResources().getColor(R.color.mainBlue);
        }else{
            colorToSet = mContext.getResources().getColor(android.R.color.transparent);
        }
        holder.nameTV.setBackgroundColor(colorToSet);

        return v;
    }


    static class viewHolder{
        TextView nameTV;
        TextView avatarTv;
    }

}
