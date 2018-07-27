package asim.tgs_member_app.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class ImmediateJobAdapter extends BaseAdapter
{

    private List<SuggestedJobObject> list;
    private Context context;
    private LayoutInflater layoutInflater;

    public ImmediateJobAdapter(List<SuggestedJobObject> list, Context context) {
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
        TextView meet_date = (TextView) convertView.findViewById(R.id.meet_datetime);
        TextView total = (TextView) convertView.findViewById(R.id.order_total);
        TextView instructions = (TextView) convertView.findViewById(R.id.instructions);
        TextView job_hrs = (TextView) convertView.findViewById(R.id.job_hours);
        //jobs_starts_in_btn

        LinearLayout job_starts_in = (LinearLayout) convertView.findViewById(R.id.jobs_starts_in_btn);

        job_starts_in.setVisibility(View.GONE);

        Button accept_job = (Button) convertView.findViewById(R.id.accept_job);
        Button cancel_job = (Button) convertView.findViewById(R.id.reject_job);
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

        meet_date.setText(object.getDatetime_meet());
        meet_location.setText(object.getMeet_loc());
        instructions.setText(object.getInstructions());
        total.setText(object.getOrder_total());
        job_hrs.setText(object.getDatetime_ordered());

        if (list.get(position).isShow_options()) {
            option_layout.setVisibility(View.VISIBLE);
        }
        else
        {
            option_layout.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void CancelJob(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to cancel?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                list.get(position).setShow_options(false);
                confirmation.dismiss();
                notifyDataSetChanged();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener()
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
        builder.setMessage("Are you sure you want to accept?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AcceptJobApi(position);
                list.get(position).setShow_options(false);
                confirmation.dismiss();
                notifyDataSetChanged();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener()
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
    private void AcceptJobApi(int position)
    {
         httpClient.setConnectTimeout(40000);

         settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
         String member_id = settings.getString(Constants.PREFS_USER_ID,"0");
         key ="tgs_appkey_amin";// settings.getString(Constants.PREFS_ACCESS_TOKEN,"tgs_appkey_amin");


        Log.e("suggested jobs", Constants.Host_Address + "members/accept_order/"+member_id+"/"+key);
        httpClient.get(context, Constants.Host_Address + "members/accept_order/"+member_id+"/"+list.get(position).getOrder_id()+"/"+key, new AsyncHttpResponseHandler() {

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

                    UtilsManager.showAlertMessage(context,"","Job accepted succesfully");
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
}
