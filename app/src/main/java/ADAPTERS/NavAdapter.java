package ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examples.android.calendar.R;


/**
 * Created by kristjan on 02/03/2015.
 */
public class NavAdapter extends BaseAdapter{
    private int selectedIndex;
    private Context mContext;
    private static boolean wasSelected;
   // private String[] navItemsArr = {"My Calendar", "Pepperoni", "Salami", "Beef"};
   private static final String[] navItemsArr = GLOBALS.Constants.navIdNames();

    public NavAdapter(Context c){
        mContext = c;
        selectedIndex = 0;
        wasSelected = false;
    }


    public void setSelectedIndex(int i){
        selectedIndex = i;
        wasSelected = true;
        notifyDataSetChanged();//fix to below comment
    } //isn't getting picked up by adapter, notifydatasetchanged maybe?
    public int getSelectedIndex(){
        return selectedIndex;
    }


    @Override
    public int getCount() {
        return navItemsArr.length;
    }

    @Override
    //return friend object from friendsList arraylist at given position
    public Object getItem(int position) {
        return navItemsArr[position];
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
            v = vi.inflate(R.layout.nav_list_item, null);
            holder = new viewHolder();
            holder.nameTV = (TextView) v.findViewById(R.id.navListItemNameTV);
            v.setTag(holder);
        }else{
            holder = (viewHolder) v.getTag();
        }

        holder.nameTV.setText(navItemsArr[position]);
        int textColorToSet = 0;

        if(wasSelected) {
            if (position == selectedIndex) {
                textColorToSet = mContext.getResources().getColor(R.color.white);
            } else {
                textColorToSet = mContext.getResources().getColor(android.R.color.black);
            }
            holder.nameTV.setTextColor(textColorToSet);
        }

        return v;
    }


    static class viewHolder{
        TextView nameTV;
    }

}
