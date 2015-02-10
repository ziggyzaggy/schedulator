package ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examples.android.calendar.R;

import java.util.ArrayList;

import ENTITIES.Group;

/**
 * Created by ziggyzaggy on 10/02/2015.
 */
public class GroupsAdapter extends BaseAdapter {

    private ArrayList<Group> groups;
    private Context mContext;

    public GroupsAdapter(Context c) {
        groups = new ArrayList<>();
        mContext = c;
        makeGroups(2);

    }

    private void makeGroups(int num){
        for(int i = 0; i < num; i++){
            Group g = new Group(i, 1, "Group number " + i);
            groups.add(g);
        }
    }

    public GroupsAdapter(){

    }

    @Override
    public int getCount() {
        return groups.size();
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

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.group_list_item, null);

        }

        TextView grpName = (TextView) v.findViewById(R.id.txtGroupName);
        grpName.setText(groups.get(position).getGroupName());


        return v;
    }
}
