package HELPERS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.examples.android.calendar.CalendarView;
import com.examples.android.calendar.R;

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
    private ProgressBar prgBar;

    public DateHelper(Activity a, TextView container, JSONObject jobj, CalendarView c){
        this.mContext = a;
        this.container = container;
        this.dialog = new ProgressDialog(mContext, AlertDialog.THEME_HOLO_LIGHT);
        prgBar = (ProgressBar) a.findViewById(R.id.prgBarDates);
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

            prgBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            List<NameValuePair> pars = new ArrayList<NameValuePair>();
            JSONParser parser = new JSONParser();
            JSONObject jobj = null;
            //db password sched47
            String theURL = "http://sched.comoj.com/getDates.php";
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

            prgBar.setVisibility(View.GONE);

            refClass.jobj = result;//pass resulting json back to referenced class


                    /*
                   result.getJSONArray("dates").getJSONObject(1).getJSONObject("2").getString("date"); //test that the right json is passed
                    */

                    refClass.refreshCalendar(); //call the refresh in referenced class
                    //TODO - (rewrite classes so that refreshcalendar is not in the view class) - k

            }
        }
    }


