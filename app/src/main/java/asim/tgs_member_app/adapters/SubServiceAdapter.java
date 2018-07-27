package asim.tgs_member_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import asim.tgs_member_app.R;
import asim.tgs_member_app.custom.AdaptiveGridView;
import asim.tgs_member_app.models.Service_Slot;

/**
 * Created by Asim Shahzad on 12/28/2017.
 */
public class SubServiceAdapter extends BaseAdapter
{
    private Context context;
    private List<Service_Slot> slots;
    private LayoutInflater layoutInflater;

    public SubServiceAdapter(Context context, List<Service_Slot> slots) {
        this.context = context;
        this.slots = slots;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return slots.size();
    }

    @Override
    public Object getItem(int position) {
        return slots.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView==null)
        {
           convertView = layoutInflater.inflate(R.layout.service_slot_item,null);
        }

        LinearLayout slot_bg = (LinearLayout) convertView.findViewById(R.id.slot_bg);
        TextView fac_time_slot = (TextView) convertView.findViewById(R.id.fac_book_time_slot);

        Service_Slot slot = (Service_Slot) getItem(position);

        fac_time_slot.setText(slot.getSlot_name());

        if (slot.isActive())
            slot_bg.setBackgroundResource(R.drawable.selected_service_item_bg);
        else
        {
          /*  if (slot.isActive())
                 slot_bg.setBackgroundResource(R.drawable.selected_service_item_bg);
            else
                slot_bg.setBackgroundResource(R.drawable.fac_book_slot_disabled_background);*/
            slot_bg.setBackgroundResource(R.drawable.unselected_service_item_bg);
        }


        return convertView;

    }



}
