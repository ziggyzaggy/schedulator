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

import ENTITIES.User;

/**
 * Created by ziggyzaggy on 09/02/2015.
 */
public class FriendListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<User> friendsList;
    private TextView nameTV;
    private TextView avatarTV;

    public FriendListAdapter(Context c){
        mContext = c;
        friendsList = new ArrayList<>();
        makeFriends(100);

    }

    public void makeFriends(int number){
        for(int i = 0; i < number; i++){
            User u = new User(i, false, "user no " + i, "example@example.com");
            friendsList.add(u);
        }

    }

    //NOTE: getViewTypeCount and getItemViewType methods basically are telling the adapter to never recycle the views,
    // this fixes the problems with the row states (highlighting of selected items in the list)
    // but could result in using up too much memory (e.g. 100 firends results in approx 65 mb of allocated memory !!! vs ~15mb on recycled views),
    // should implement and keep actual states in the model

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }


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

        /*nameTV = (TextView) v.findViewById(R.id.nameTV);
        avatarTV = (TextView) v.findViewById(R.id.avatarTV);

        nameTV.setText(friendsList.get(position).getName());*/
        //avatarTV.setText("" + friendsList.get(position).getUserId());
       // avatarTV.setBackgroundResource(R.drawable.ic_contact_picture);

        return v;
    }


    static class viewHolder{
        TextView nameTV;
        TextView avatarTv;
    }

}
