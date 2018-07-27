package asim.tgs_member_app.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.utils.NotifyUpdates;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class SuggestedJobAdapter extends BaseAdapter
{

    private List<SuggestedJobObject> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private NotifyUpdates notifyUpdates;
    public void setNotifier(NotifyUpdates notifier)
    {
        notifyUpdates = notifier;
    }

    public SuggestedJobAdapter(List<SuggestedJobObject> list, Context context) {
        this.list = list;
        this.context = context;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            convertView = layoutInflater.inflate(R.layout.suggested_jobs,null);
        }


        TextView meet_location = (TextView) convertView.findViewById(R.id.meet_location);
        TextView destination_location = (TextView) convertView.findViewById(R.id.destination_location);
        TextView meet_date = (TextView) convertView.findViewById(R.id.meet_datetime);
        TextView posted_date = (TextView) convertView.findViewById(R.id.posted_datetime);
        TextView total = (TextView) convertView.findViewById(R.id.order_total);
        TextView instructions = (TextView) convertView.findViewById(R.id.instructions);
        TextView job_hrs = (TextView) convertView.findViewById(R.id.job_hours);
        TextView timer_text = (TextView) convertView.findViewById(R.id.time_job);

        Button accept_job = (Button) convertView.findViewById(R.id.accept_job);
        Button cancel_job = (Button) convertView.findViewById(R.id.reject_job);
        LinearLayout meet_time_layout = (LinearLayout) convertView.findViewById(R.id.meet_time_layout);
        final LinearLayout option_layout = (LinearLayout) convertView.findViewById(R.id.option_layout);


        accept_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AcceptJob(position);

            }
        });
        cancel_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CancelJob(position);

            }
        });


        SuggestedJobObject object = list.get(position);

        if (object.getBooking_type().equalsIgnoreCase("Immediate") || object.getBooking_type().equalsIgnoreCase("")) {
            meet_time_layout.setVisibility(View.GONE);
            list.get(position).setDatetime_meet("required immediatly");
        }
        else {
            meet_time_layout.setVisibility(View.VISIBLE);
        }

        if (object.getInstructions().equals("N/A"))
            object.setInstructions("no instructions ");

        meet_date.setText(ApplyFormat(object.getDatetime_meet()));
        meet_location.setText(object.getMeet_loc());

        instructions.setText(object.getInstructions());

        destination_location.setText(object.getDestination());

        String comma_seperated_price = object.getMember_share();
        try {
            DecimalFormat formatter = new DecimalFormat("##,###.00");

            if (!object.getMember_share().contains("."))
                comma_seperated_price = formatter.format(Integer.parseInt(object.getMember_share()));
            else
                comma_seperated_price = formatter.format(Double.parseDouble(object.getMember_share()));

            if (comma_seperated_price.equalsIgnoreCase(".00"))
                comma_seperated_price = "0.00";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        total.setText(Constants.currency+comma_seperated_price);
        job_hrs.setText(object.getNo_of_hours()+" hrs");
        timer_text.setText(object.getJob_starts_in());
        posted_date.setText(object.getJob_posted_time());

       calculatejobpostedTime(position,object.getDatetime_ordered(),object.getServer_time());

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss");
            Date date = new Date();
            date = simpleDateFormat.parse(list.get(position).getDatetime_meet());
            String formmated_date = new SimpleDateFormat("yyyy/MM/dd - h:mm:ss").format(date);
            // meet_date.setText(new SimpleDateFormat("yyyy/MM/dd-h:mm:ss").parse(list.get(position).getDatetime_meet()).toString());
            meet_date.setText(formmated_date);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        meet_date.setText(ApplyFormat(object.getDatetime_meet()));

       /* if (list.get(position).isShow_options()) {
            option_layout.setVisibility(View.VISIBLE);
        }
        else
        {
            option_layout.setVisibility(View.GONE);
            if (list.get(position).getJob_starts_in().equalsIgnoreCase("not started yet"))
                showCountDown(timer_text,list.get(position),position);
        }*/

        return convertView;
    }

    private String ApplyFormat(String date_string)
    {
        String date_final = "";
        String am_pm = "pm";
        if (date_string.contains("AM") || date_string.contains("AM") || date_string.contains("Am"))
            am_pm = "am";
        try {
            date_string = date_string.replace("AM","").replace("PM","");
            Log.e("date",date_string);
            Date current_date = new SimpleDateFormat("dd-MM-yyyy h:mm").parse(date_string);
            // return new SimpleDateFormat("dd-MM-yyyy h:mm").format(current_date)+" "+am_pm+"";
            date_final = new SimpleDateFormat("dd MMM yyyy h:mm").format(current_date)+" "+am_pm+"";
            return date_final;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date_final+" "+am_pm;
    }


    private CountDownTimer jobstartsin_timer;
    private void showCountDown(final TextView timer_text, final SuggestedJobObject suggestedJobObject,final int position)
    {
        String start_date = suggestedJobObject.getDatetime_meet();
        String ordered_date = suggestedJobObject.getDatetime_ordered();

        try {
            String dateString = start_date;
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateString);
            Date convertedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateString);
           // convertedDate = dateFormat.parse(dateString);
            Calendar old_cal = Calendar.getInstance();
            old_cal.setTime(convertedDate);
            int hrs = old_cal.get(Calendar.HOUR_OF_DAY);
            int min = old_cal.get(Calendar.MINUTE);
            int sec = old_cal.get(Calendar.SECOND);

            int month = old_cal.get(Calendar.MONTH);
            month++;
            int day = old_cal.get(Calendar.DAY_OF_MONTH);

            Calendar calender = Calendar.getInstance();
            int current_month = calender.get(Calendar.MONTH);
            int current_day = calender.get(Calendar.DAY_OF_MONTH);

            current_month++;
            if (month==current_month)
            {
                if (current_day==day)
                {
                    int current_hrs = calender.get(Calendar.HOUR_OF_DAY);
                    int current_min = calender.get(Calendar.MINUTE);
                    int current_sec = calender.get(Calendar.SECOND);

                    if (current_hrs>=12)
                        current_hrs = current_hrs - 12;

                    if (hrs>=12)
                        hrs = hrs - 12;

                    if (hrs==0)
                        hrs = 12;


                    int rem_hrs = hrs - current_hrs;
                    int rem_minutes = min - current_min;
                    int rem_seconds = sec - current_sec;

                    if (rem_minutes<0)
                    {
                        int tem_min = rem_hrs * 60;
                        tem_min = tem_min - (rem_minutes*-1);
                        rem_hrs = tem_min/60;
                        rem_minutes = tem_min%60;
                    }
                    if (rem_seconds<0)
                    {
                        int tem_sec = rem_minutes * 60;
                        rem_minutes = tem_sec /60;
                        rem_seconds = tem_sec%60;
                    }

                    String calcuated_time = "calculating...";

                    if (rem_hrs<10 && rem_minutes<10 && rem_seconds>=10)
                        calcuated_time= "0"+rem_hrs+":"+"0"+rem_minutes+":"+rem_seconds;
                    else if (rem_hrs<10 && rem_minutes>=10 && rem_seconds>=10)
                        calcuated_time= "0"+rem_hrs+":"+rem_minutes+":"+rem_seconds;
                    else if (rem_hrs>=10 && rem_minutes<10 && rem_seconds>=10)
                        calcuated_time = rem_hrs+":0"+rem_minutes+":"+rem_seconds;
                    else if (rem_hrs>=10 && rem_minutes>=10 && rem_seconds<10)
                        calcuated_time = rem_hrs+":"+rem_minutes+":0"+rem_seconds;

                    list.get(position).setJob_posted_time(calcuated_time);


                    notifyDataSetChanged();


                }
                else if (current_day-day==1)
                {
                    //timer_text.setText(day-current_day+" days");
                    list.get(position).setJob_posted_time("Yesterday");
                    notifyDataSetChanged();
                }
                else if (day!=0)
                {
                    //timer_text.setText(day-current_day+" days");
                    list.get(position).setJob_posted_time(day-current_day+" days ago");
                    notifyDataSetChanged();
                }
                else if (day==0)
                {

                }
            }
            else
            {
                list.get(position).setJob_starts_in("month ago");
                notifyDataSetChanged();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private void CancelJob(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(R.string.reject_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                list.get(position).setShow_options(false);
                confirmation.dismiss();
                notifyDataSetChanged();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmation.dismiss();
            }
        });



        confirmation= builder.create();
        confirmation.show();
    }

    Dialog confirmation;
    private void AcceptJob(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(R.string.accept_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AcceptJobApi(position);
                confirmation.dismiss();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               confirmation.dismiss();
            }
        });



        confirmation= builder.create();
        confirmation.show();
    }

    AsyncHttpClient httpClient = new AsyncHttpClient();
    ProgressDialog progressDialog;
    SharedPreferences settings;
    String key;
    private void AcceptJobApi(final int position)
    {
         httpClient.setConnectTimeout(40000);

         settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
         String member_id = settings.getString(Constants.PREFS_USER_ID,"0");
         key ="tgs_appkey_amin";// settings.getString(Constants.PREFS_ACCESS_TOKEN,"tgs_appkey_amin");


        Log.e("suggested jobs",  Constants.Host_Address + "members/accept_order/"+member_id+"/"+list.get(position).getOrder_id()+"/"+list.get(position).getOrder_item_id()+"/"+list.get(position).getMember_share()+"/"+key+"/"+list.get(position).getCustomer_id());
        httpClient.get(context, Constants.Host_Address + "members/accept_order/"+member_id+"/"+list.get(position).getOrder_id()+"/"+list.get(position).getOrder_item_id()+"/"+list.get(position).getMember_share()+"/"+key+"/"+list.get(position).getCustomer_id(), new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Accepting job...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String responseData = new String(responseBody,"UTF-8");
                    Log.e("response success",responseData);
                    JSONObject object = new JSONObject(responseData);
                    JSONObject data = object.getJSONObject("data");
                    String datetime_accepted = data.getString("datetime_accepted");
                    String datetime_reached = data.getString("datetime_reached");
                    String order_item_id = data.getString("order_item_id");
                    list.remove(position);
                    notifyDataSetChanged();
                    notifyUpdates.callToFragment();

                    Toast.makeText(context,"Job accepted",Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    Toast.makeText(context,"Unable to accept job ",Toast.LENGTH_LONG).show();

                    String responseData = new String(responseBody,"UTF-8");
                    Log.e("response failure",responseData);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void calculatejobpostedTime(final int position,String posted_date,String server_time)
    {
        try {
           // String dateString = start_date;
           // SimpleDateFormat dateFormat = new SimpleDateFormat(dateString);
            posted_date = posted_date.replace(" AM","").replace(" PM","");
            server_time = server_time.replace(" AM","").replace(" PM","");
            posted_date = posted_date.replace("/","-")+":03";
            final Date convertedDate = new SimpleDateFormat("dd-MM-yyyy h:mm:ss").parse(posted_date);
            // convertedDate = dateFormat.parse(dateString);
            Calendar old_cal = Calendar.getInstance();
            old_cal.setTime(convertedDate);
            int hrs = old_cal.get(Calendar.HOUR_OF_DAY);
            int min = old_cal.get(Calendar.MINUTE);
            int sec = old_cal.get(Calendar.SECOND);

            int month = old_cal.get(Calendar.MONTH);
            month++;
            int day = old_cal.get(Calendar.DAY_OF_MONTH);

          /*  SimpleDateFormat dateFormat_server = new SimpleDateFormat(current_date);
            final Date convertedDate_server = new SimpleDateFormat("yyyy-MM-dd h:mm:ss").parse(current_date);
*/
            Calendar calender = Calendar.getInstance();
            final Date server_time_current = new SimpleDateFormat("yyyy-MM-dd h:mm:ss").parse(server_time);
            calender.setTime(server_time_current);

            int current_month = calender.get(Calendar.MONTH);
            int current_day = calender.get(Calendar.DAY_OF_MONTH);

            current_month++;
            if (month==current_month)
            {
                if (current_day==day)
                {
                    int current_hrs = calender.get(Calendar.HOUR_OF_DAY);
                    int current_min = calender.get(Calendar.MINUTE);
                    int current_sec = calender.get(Calendar.SECOND);

                    if (current_hrs>=12)
                        current_hrs = current_hrs - 12;

                    if (hrs>=12)
                        hrs = hrs - 12;



                    int rem_hrs = current_hrs - hrs;
                    int rem_minutes = current_min - min;
                    int rem_seconds = current_sec - sec;

                    if (rem_minutes<0)
                    {
                        int tem_min = rem_hrs * 60;
                        tem_min = tem_min - (rem_minutes*-1);
                        rem_hrs = tem_min/60;
                        rem_minutes = tem_min%60;
                    }
                    if (rem_seconds<0)
                    {
                        int tem_sec = rem_minutes * 60;
                        rem_minutes = tem_sec /60;
                        rem_seconds = tem_sec%60;
                    }

                    String calcuated_time = context.getResources().getString(R.string.calculating);

                    if (rem_hrs<10 && rem_minutes<10 && rem_seconds>=10)
                        calcuated_time= "0"+rem_hrs+":"+"0"+rem_minutes+":"+rem_seconds;
                    else if (rem_hrs<10 && rem_minutes>=10 && rem_seconds>=10)
                        calcuated_time= "0"+rem_hrs+":"+rem_minutes+":"+rem_seconds;
                    else if (rem_hrs>=10 && rem_minutes<10 && rem_seconds>=10)
                        calcuated_time = rem_hrs+":0"+rem_minutes+":"+rem_seconds;
                    else if (rem_hrs>=10 && rem_minutes>=10 && rem_seconds<10)
                        calcuated_time = rem_hrs+":"+rem_minutes+":0"+rem_seconds;

                    if (rem_hrs>0)
                        list.get(position).setJob_posted_time(rem_hrs+" "+context.getResources().getString(R.string.hours)+" "+context.getResources().getString(R.string.ago)+"");
                    else if (rem_hrs==0)
                        list.get(position).setJob_posted_time(rem_minutes+" "+context.getResources().getString(R.string.mins)+" "+context.getResources().getString(R.string.ago)+"");
                    else if (rem_minutes==0)
                        list.get(position).setJob_posted_time(rem_seconds+" "+context.getResources().getString(R.string.seconds)+" "+context.getResources().getString(R.string.ago)+"");

                    if (rem_hrs==0 && rem_minutes==0)
                        list.get(position).setJob_posted_time(rem_seconds+" "+context.getResources().getString(R.string.seconds)+" "+context.getResources().getString(R.string.ago)+"");

                    notifyDataSetChanged();


                }
                else if (current_day-day==1)
                {
                    //timer_text.setText(day-current_day+" days");
                    list.get(position).setJob_posted_time(context.getResources().getString(R.string.yesterday));
                    notifyDataSetChanged();
                }
                else if (day!=0)
                {
                    //timer_text.setText(day-current_day+" days");
                    list.get(position).setJob_posted_time(current_day-day+" "+context.getResources().getString(R.string.day)+" "+context.getResources().getString(R.string.ago)+"");
                    notifyDataSetChanged();
                }
                else if (day==0)
                {

                }
            }
            else
            {
                list.get(position).setJob_posted_time("1 "+context.getResources().getString(R.string.month)+" "+context.getResources().getString(R.string.ago)+"");
                notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
