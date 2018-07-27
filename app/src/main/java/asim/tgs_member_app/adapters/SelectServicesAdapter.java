package asim.tgs_member_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.custom.AdaptiveGridView;
import asim.tgs_member_app.models.MemberService;
import asim.tgs_member_app.models.Service_Slot;
import asim.tgs_member_app.utils.UpdateHeight;

/**
 * Created by PC-GetRanked on 4/5/2018.
 */

public class SelectServicesAdapter extends BaseAdapter {

    private Context context;
    private List<MemberService> services;
    private LayoutInflater inflater;
    private List<ArrayList<Service_Slot>> item_list;

    public SelectServicesAdapter(Context context, List<MemberService> services, List<ArrayList<Service_Slot>> item_list)
    {
        this.services = services;
        this.context = context;
        this.item_list = item_list;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    UpdateHeight updateHeight;
    public void setListener(UpdateHeight updateHeight)
    {
        this.updateHeight = updateHeight;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = inflater.inflate(R.layout.service_selection_item, null);

        TextView servce_name = (TextView) convertView.findViewById(R.id.service_name);
        ImageView servce_icon = (ImageView) convertView.findViewById(R.id.service_icon);
        ImageView selected = (ImageView) convertView.findViewById(R.id.selection_image);
        LinearLayout sub_services_container = (LinearLayout) convertView.findViewById(R.id.sub_services_container);

        final LinearLayout slot1 = (LinearLayout) convertView.findViewById(R.id.slot_1);
        LinearLayout slot2 = (LinearLayout) convertView.findViewById(R.id.slot_2);
        LinearLayout slot3 = (LinearLayout) convertView.findViewById(R.id.slot_3);

        TextView slot_name1 = (TextView) convertView.findViewById(R.id.slot_name1);
        TextView slot_name2 = (TextView) convertView.findViewById(R.id.slot_name2);
        TextView slot_name3 = (TextView) convertView.findViewById(R.id.slot_name3);


        slot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item_list.get(position).get(0).isActive())
                    item_list.get(position).get(0).setActive(true);
                else
                    item_list.get(position).get(0).setActive(false);

                updateHeight.updateheight();
                notifyDataSetChanged();
            }
        });


        slot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item_list.get(position).get(1).isActive())
                    item_list.get(position).get(1).setActive(true);
                else
                    item_list.get(position).get(1).setActive(false);

                updateHeight.updateheight();
                notifyDataSetChanged();
            }
        });

        slot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item_list.get(position).get(2).isActive())
                    item_list.get(position).get(2).setActive(true);
                else
                    item_list.get(position).get(2).setActive(false);

                updateHeight.updateheight();
                notifyDataSetChanged();
            }
        });


        try {
            if (item_list.get(position).get(0).isActive()) {
                slot1.setBackgroundResource(R.drawable.selected_service_item_bg);

            }
            else {
                slot1.setBackgroundResource(R.drawable.unselected_service_item_bg);

            }

            if (item_list.get(position).get(1).isActive()) {
                slot2.setBackgroundResource(R.drawable.selected_service_item_bg);
            }
            else {
                slot2.setBackgroundResource(R.drawable.unselected_service_item_bg);
            }

            if (item_list.get(position).get(2).isActive()) {
                slot3.setBackgroundResource(R.drawable.selected_service_item_bg);
            }
            else {
                slot3.setBackgroundResource(R.drawable.unselected_service_item_bg);
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        servce_name.setText(services.get(position).getName());
        if (services.get(position).getName().equalsIgnoreCase("Bodyguard"))
            servce_icon.setImageResource(R.drawable.bodyguard_icon);
        if (services.get(position).getName().equalsIgnoreCase("Bodyguard Cum driver"))
            servce_icon.setImageResource(R.drawable.bodyguard_cum_driver);
        if (services.get(position).getName().equalsIgnoreCase("Bumble Ride"))
            servce_icon.setImageResource(R.drawable.bumble_ride);
        if (services.get(position).getName().equalsIgnoreCase("Driver"))
            servce_icon.setImageResource(R.drawable.driver_icon);


        for (int i=0;i<item_list.get(position).size();i++)
        {
            if (i==0)
            {
                slot_name1.setText(item_list.get(position).get(i).getSlot_name());
                slot1.setVisibility(View.VISIBLE);

            }
            if (i==1)
            {
                slot_name2.setText(item_list.get(position).get(i).getSlot_name());
                slot2.setVisibility(View.VISIBLE);
            }
            if (i==2)
            {
                slot_name3.setText(item_list.get(position).get(i).getSlot_name());
                slot3.setVisibility(View.VISIBLE);
            }
        }

        if (item_list.get(position).size()==1) {
            slot2.setVisibility(View.INVISIBLE);
            slot3.setVisibility(View.INVISIBLE);
        }
        if (item_list.get(position).size()==2)
            slot3.setVisibility(View.INVISIBLE);


        if(services.get(position).isSelected()) {
            selected.setImageResource(R.drawable.check_select);
            sub_services_container.setVisibility(View.VISIBLE);


        }
        else {
            selected.setImageResource(R.drawable.uncheck_box);
            sub_services_container.setVisibility(View.GONE);
            //updateHeight.updateheight();


        }


        return convertView;
    }
}
