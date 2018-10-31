package asim.tgs_member_app.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import asim.tgs_member_app.R;
import asim.tgs_member_app.chat.ChatActivity;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.Notification_Data;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Asim Shahzad on 1/30/2018.
 */
public class NotificationAdapter extends BaseAdapter {

    private List<Notification_Data> notifications;
    private Context context;
    private LayoutInflater layoutInflater;


    public NotificationAdapter(List<Notification_Data> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView==null)
        {
            convertView = layoutInflater.inflate(R.layout.notification_item,null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.notification_title);
        TextView body = (TextView) convertView.findViewById(R.id.notification_body);
        TextView time = (TextView) convertView.findViewById(R.id.notification_time);
        LinearLayout main_item_layout = (LinearLayout) convertView.findViewById(R.id.main_item_layout);

        main_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifications.get(position).isChat())
                {

                    String chat_id = notifications.get(position).getChat_id();
                    String[] order_id = chat_id.split(Pattern.quote("_"));

                    if (order_id.length>0)
                        CheckOrderStatus(order_id[order_id.length-1],chat_id);

                }

            }
        });

        Notification_Data data = (Notification_Data) getItem(position);

        title.setText(data.getTitle());
        body.setText(data.getBody());
        time.setText(data.getTime());




        return convertView;
    }

    private ProgressDialog progress;
    private void showDialog()
    {
        progress = new ProgressDialog(context);
        progress.setMessage("Checking status");
        progress.show();
    }

    private void hideLoading()
    {
        if (progress!=null)
            progress.dismiss();

    }

    AsyncHttpClient asynClient = new AsyncHttpClient();
    private void CheckOrderStatus(final String order_id,final String chat_id)
    {
        asynClient.setConnectTimeout(20000);

        asynClient.get(context, Constants.Host_Address + "orders/check_order_status/" + order_id + "/tgs_appkey_amin", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideLoading();
                    String response = new String(responseBody);
                    Log.e("response",response);
                    JSONObject object = new JSONObject(response);
                    if (!object.getString("message").contains("Completed"))
                        context.startActivity(new Intent(context, ChatActivity.class).putExtra("chat_id",chat_id));
                    else
                        UtilsManager.showAlertMessage(context,"","Order is completed");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideLoading();
                    String response = new String(responseBody);
                    Log.e("response",response);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }

}
