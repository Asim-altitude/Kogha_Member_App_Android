package asim.tgs_member_app.fragments.notifications_tabs;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.adapters.NotificationAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.Notification_Data;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PC-GetRanked on 4/24/2018.
 */

public class JobNotificationFrame extends Fragment {


    private ListView notification_list;
    private NotificationAdapter adapter;
    private List<Notification_Data> list;

    private TextView empty_notification;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_notification_list,null);
        notification_list = (ListView) rootView.findViewById(R.id.notification_list);
        empty_notification = (TextView) rootView.findViewById(R.id.empty_notification);

        if (list == null)
            list = new ArrayList<>();

            Bundle bundle = getArguments();

        if (bundle!=null) {

            String title = bundle.getString("title");
            String body = bundle.getString("body");

            Notification_Data data = new Notification_Data();
            data.setTitle(title);
            data.setBody(body);
            data.setShown(true);
            data.setId("1");
            list.add(data);

            empty_notification.setVisibility(View.GONE);

        }

        adapter = new NotificationAdapter(list,getContext());
        notification_list.setAdapter(adapter);

        notification_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        Constants.cancelNotification(getContext(), Constants.NOTICIATION_ID);
        loadSavedNofications();

        if (list.size()<=0) {
            empty_notification.setVisibility(View.VISIBLE);
            empty_notification.setText("No "+getResources().getString(R.string.notifications));
        }

        return rootView;
    }

    private void loadSavedNofications() {
        Gson gson = new Gson();
        list.clear();
        Type type = new TypeToken<ArrayList<Notification_Data>>() {
        }.getType();
        String json = getContext().getSharedPreferences(Constants.USER_JOB_NOTIFICATION, MODE_PRIVATE).getString("job_notifications", "");
        if (json != null) {
            if (!json.equals("")) {
                ArrayList<Notification_Data> temp = gson.fromJson(json, type);
                for (int i = 0; i < temp.size(); i++) {
                    temp.get(i).setShown(true);
                    list.add(temp.get(i));

                }

                adapter.notifyDataSetChanged();
                updateNotifications();

            }
        }

    }

    private void updateNotifications() {

        if (list!=null)
        {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            getContext().getSharedPreferences(Constants.USER_JOB_NOTIFICATION,MODE_PRIVATE)
                    .edit()
                    .putString("job_notifications", json)
                    .apply();
        }

    }
}
