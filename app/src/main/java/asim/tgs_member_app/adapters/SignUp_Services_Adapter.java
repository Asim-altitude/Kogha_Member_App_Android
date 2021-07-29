package asim.tgs_member_app.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.Order_Service_Info;
import asim.tgs_member_app.utils.Job_Selection_Notifier;
import asim.tgs_member_app.utils.ServiceSelectionCallBack;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public class SignUp_Services_Adapter extends BaseAdapter
{

    private List<Order_Service_Info> services;
    String[] service_id;
    private Context context;

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

    SharedPreferences sharedPreferences;
    public SignUp_Services_Adapter(List<Order_Service_Info> orderList, Context context) {
        this.services = orderList;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);
       // service_id = sharedPreferences.getString("service_id","").split(Pattern.quote("-"));

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
            convertView = LayoutInflater.from(context).inflate(R.layout.job_service_data_item,null);

        CheckBox service_name = (CheckBox) convertView.findViewById(R.id.service_name);
        service_name.setText(services.get(position).getService_name());

       /* if (selected_index!=position) {
            holder.service_name.setBackgroundResource(R.drawable.service_unselected);
            holder.service_name.setTextColor(context.getResources().getColor(R.color.theme_primary));
        }
        else
        {
            holder.service_name.setBackgroundResource(R.drawable.service_selected);
            holder.service_name.setTextColor(context.getResources().getColor(R.color.white_color));
        }*/


       /*try{

               for (int j=0;j<service_id.length;j++){
                   if (services.get(position).getService_id().equalsIgnoreCase(service_id[j].toString())){
                     service_name.setChecked(true);
                   } else {
                       service_name.setChecked(false);
                   }
               }

       }
       catch (Exception e){
           e.printStackTrace();
       }
*/

        service_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    services.get(position).setSelected(1);

                }
                else {
                    services.get(position).setSelected(-1);
                }

                if (isChecked && serviceSelectionCallBack!=null){
                    serviceSelectionCallBack.onServiceSelected(services.get(position),s_code,position);

                }
            }
        });

        return convertView;
    }



}
