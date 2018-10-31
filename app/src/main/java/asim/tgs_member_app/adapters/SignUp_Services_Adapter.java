package asim.tgs_member_app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Order_Service_Info;
import asim.tgs_member_app.utils.Job_Selection_Notifier;
import asim.tgs_member_app.utils.ServiceSelectionCallBack;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public class SignUp_Services_Adapter extends RecyclerView.Adapter<SignUp_Services_Adapter.MyViewHolder>
{

    private List<Order_Service_Info> services;
    private Context context;
    private LayoutInflater layoutInflater;
    private View layoutView;

    private int selected_index =-1;

    private int s_code = 0;

    public void setS_code(int s_code) {
        this.s_code = s_code;
    }

    ServiceSelectionCallBack serviceSelectionCallBack;

    public void setServiceSelectionCallBack(ServiceSelectionCallBack serviceSelectionCallBack) {
        this.serviceSelectionCallBack = serviceSelectionCallBack;
    }

    public int getSelected_index() {
        return selected_index;
    }

    public void setSelected_index(int selected_index) {
        this.selected_index = selected_index;
    }

    public SignUp_Services_Adapter(List<Order_Service_Info> orderList, Context context) {
        this.services = orderList;
        this.context = context;
    }

    Job_Selection_Notifier job_selection_notifier;

    public void setJob_selection_notifier(Job_Selection_Notifier job_selection_notifier) {
        this.job_selection_notifier = job_selection_notifier;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_service_data_item, parent, false);
        MyViewHolder _ViewHolder = new MyViewHolder(layoutView);

        return _ViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        holder.service_name.setText(services.get(position).getService_name());

        if (selected_index!=position) {
            holder.service_name.setBackgroundResource(R.drawable.service_unselected);
            holder.service_name.setTextColor(context.getResources().getColor(R.color.theme_primary));
        }
        else
        {
            holder.service_name.setBackgroundResource(R.drawable.service_selected);
            holder.service_name.setTextColor(context.getResources().getColor(R.color.white_color));
        }

        holder.service_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // job_selection_notifier.onJobSelection(position);
                serviceSelectionCallBack.onServiceSelected(services.get(position),s_code,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return services.size();

    }

    //holder
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView service_name;;


        public MyViewHolder(View itemView) {
            super(itemView);
            service_name = (TextView) itemView.findViewById(R.id.service_name);

        }

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
