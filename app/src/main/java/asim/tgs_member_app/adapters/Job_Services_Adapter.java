package asim.tgs_member_app.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;


import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Order_Service_Info;
import asim.tgs_member_app.utils.Job_Selection_Notifier;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public class Job_Services_Adapter extends BaseAdapter
{

    private List<Order_Service_Info> services;
    private Context context;
    private LayoutInflater layoutInflater;
    private View layoutView;

    private int selected_index =0;


    public int getSelected_index() {
        return selected_index;
    }

    public void setSelected_index(int selected_index) {
        this.selected_index = selected_index;
    }

    public Job_Services_Adapter(List<Order_Service_Info> orderList, Context context) {
        this.services = orderList;
        this.context = context;
    }

    Job_Selection_Notifier job_selection_notifier;

    public void setJob_selection_notifier(Job_Selection_Notifier job_selection_notifier) {
        this.job_selection_notifier = job_selection_notifier;
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.pending_job_service_item,null);

        RadioButton service_name =  convertView.findViewById(R.id.service_name);


        if (services.get(position).getHour().equalsIgnoreCase("0")){
            service_name.setText(services.get(position).getService_name());

        }else {
            service_name.setText(services.get(position).getService_name() + ": " + services.get(position).getHour() + "hours");
        }

        if (selected_index!=position) {
            service_name.setChecked(false);//R.drawable.service_unselected);
            // holder.service_name.setTextColor(context.getResources().getColor(R.color.theme_primary));
        }
        else
        {
            service_name.setChecked(true);
            // holder.service_name.setBackgroundResource(R.drawable.service_selected);
            //holder.service_name.setTextColor(context.getResources().getColor(R.color.white_color));
        }

        service_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (job_selection_notifier!=null)
                        job_selection_notifier.onJobSelection(position);
                }
            }
        });

        return convertView;
    }



    private String getDate(String date)
    {
        String[] arr = date.split(" ");
        String[] mDate = arr[0].split("-");

        //return mDate[2]+" DEC "+mDate[0];
        return arr[0];
    }

    private String getTime(String dateTime)
    {
        if (dateTime!=null) {
            String[] arr = dateTime.split(" ");
            // String[] mDate = arr[0].split("-");

            return arr[1];
        }
        return "00:00:00";
    }

}
