package asim.tgs_member_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Notification_Data;

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
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView==null)
        {
            convertView = layoutInflater.inflate(R.layout.notification_item,null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.notification_title);
        TextView body = (TextView) convertView.findViewById(R.id.notification_body);

        Notification_Data data = (Notification_Data) getItem(position);

        title.setText(data.getTitle());
        body.setText(data.getBody());

        return convertView;
    }

}
