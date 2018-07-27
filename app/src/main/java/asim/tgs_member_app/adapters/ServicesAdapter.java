package asim.tgs_member_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import asim.tgs_member_app.R;

/**
 * Created by PC-GetRanked on 4/5/2018.
 */

public class ServicesAdapter extends BaseAdapter {

    private Context context;
    private List<String> services;
    private LayoutInflater inflater;

    public ServicesAdapter(Context context, List<String> services)
    {
        this.services = services;
        this.context = context;

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
        ImageView servce_icon = (ImageView) convertView.findViewById(R.id.service_icon);

        servce_name.setText(services.get(position));
        if (services.get(position).equalsIgnoreCase("Bodyguard"))
            servce_icon.setImageResource(R.drawable.bodyguard_icon);
        if (services.get(position).equalsIgnoreCase("Bodyguard Cum driver"))
            servce_icon.setImageResource(R.drawable.bodyguard_cum_driver);
        if (services.get(position).equalsIgnoreCase("Bumble Ride"))
            servce_icon.setImageResource(R.drawable.bumble_ride);
        if (services.get(position).equalsIgnoreCase("Driver"))
            servce_icon.setImageResource(R.drawable.driver_icon);

        return convertView;
    }
}
