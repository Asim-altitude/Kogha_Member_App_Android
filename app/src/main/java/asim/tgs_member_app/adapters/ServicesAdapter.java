package asim.tgs_member_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Pattern;

import asim.tgs_member_app.R;

/**
 * Created by PC-GetRanked on 4/5/2018.
 */

public class ServicesAdapter extends BaseAdapter {

    private Context context;
    private List<String> services,sub_services;
    private LayoutInflater inflater;

    public ServicesAdapter(Context context, List<String> services, List<String> sub_services)
    {
        this.services = services;
        this.context = context;
        this.sub_services = sub_services;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null)
            convertView = inflater.inflate(R.layout.service_list_item,null);

        TextView servce_name = (TextView) convertView.findViewById(R.id.service_name);
        TextView sub_item = (TextView) convertView.findViewById(R.id.sub_item);
        ImageView servce_icon = (ImageView) convertView.findViewById(R.id.service_icon);
        ListView listView = convertView.findViewById(R.id.sub_services_list);

         if (services.get(position).equalsIgnoreCase("Bodyguard"))
            servce_icon.setImageResource(R.drawable.bodyguard_icon);
        if (services.get(position).equalsIgnoreCase("Bodyguard Cum driver"))
            servce_icon.setImageResource(R.drawable.bodyguard_cum_driver);
        if (services.get(position).equalsIgnoreCase("Bumble Ride"))
            servce_icon.setImageResource(R.drawable.bumble_ride);
        if (services.get(position).equalsIgnoreCase("Driver"))
            servce_icon.setImageResource(R.drawable.driver_icon);

        try {
            SubServicesAdapter subServicesAdapter = new SubServicesAdapter(context, sub_services.get(position).split(Pattern.quote(",")));
            listView.setAdapter(subServicesAdapter);
            setListViewHeightBasedOnChildren(listView);

        }
        catch (Exception e){
            e.printStackTrace();
        }
         servce_name.setText(services.get(position));
       // sub_item.setText("("+sub_services.get(position)+")");


        return convertView;
    }

    public void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null)
        {
            int totalHeight = 0;
            int size = listAdapter.getCount();
            for (int i = 0; i < size; i++)
            {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            totalHeight = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight;
            listView.setLayoutParams(params);

        }

    }

}
