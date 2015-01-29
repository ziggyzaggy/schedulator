package HELPERS;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.examples.android.calendar.CalendarView;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kris on 28/01/2015.
 */
public class DateHelper {
    private Context mContext;
    private TextView container;
    private ProgressDialog dialog;
    private JSONObject jobj;
    private CalendarView refClass;

    public DateHelper(Activity a, TextView container, JSONObject jobj, CalendarView c){
        this.mContext = a;
        this.container = container;
        this.dialog = new ProgressDialog(mContext);
        this.jobj = jobj;
        this.refClass = c;
    }

    public DateHelper(){
        //dialog = new ProgressDialog();


    }


    public void runAsyncGetter(JSONObject jobj){
        AsyncDateGetter getter = new AsyncDateGetter();
        getter.execute("test");
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public TextView getContainer() {
        return container;
    }

    public void setContainer(TextView container) {
        this.container = container;
    }


    private class AsyncDateGetter extends AsyncTask<String, Void, JSONObject>{

        protected void onPreExecute(){
            dialog.setMessage("Getting Event Dates");
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            List<NameValuePair> pars = new ArrayList<NameValuePair>();
            JSONParser parser = new JSONParser();
            JSONObject jobj = null;
            String theURL = "http://192.168.1.115:1337/schedulator/getDates.php";
            try {
                jobj = parser.makeHttpRequest(theURL, "GET", pars);
                Log.d("jobj: ", "" + jobj);
            }catch(java.io.IOException e) {
                Log.e("io",""+e);
            }


            return jobj;
        }

        protected void onPostExecute(JSONObject result){
            if(dialog.isShowing()) {
                dialog.hide();
                dialog.dismiss();
            }

            refClass.jobj = result;//pass resulting json back to referenced class


            if(container != null){
                try {
                    container.setText("" + result.getJSONArray("dates").getString(0)); //test that the right json is passed
                    refClass.refreshCalendar(); //call the refresh in referenced class
                    //TODO - (should rewrite classes as with this method inside of this class rather than have it in the view)
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
