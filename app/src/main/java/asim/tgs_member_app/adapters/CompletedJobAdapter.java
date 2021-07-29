package asim.tgs_member_app.adapters;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.CompletedJobObject;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.utils.UtilsManager;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class CompletedJobAdapter extends BaseAdapter
{
    private static final String TAG = "CompletedJobAdapter";

    private List<CompletedJobObject> list;
    private Context context;
    private LayoutInflater layoutInflater;

    public CompletedJobAdapter(List<CompletedJobObject> list, Context context) {
        this.list = list;
        this.context = context;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    SimpleDateFormat current_format = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat desired_format = new SimpleDateFormat("dd MMM yyyy");

    boolean hasInstructions = false,hasUniform = false;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            convertView = layoutInflater.inflate(R.layout.completed_jobs_item,null);
        }
        TextView meet_location = (TextView) convertView.findViewById(R.id.meet_location);
        TextView drop_location = (TextView) convertView.findViewById(R.id.destination);
        TextView meet_date = (TextView) convertView.findViewById(R.id.meet_date);
        TextView meet_time = (TextView) convertView.findViewById(R.id.meet_time);
        TextView accepted_datetime = (TextView) convertView.findViewById(R.id.accepted_datetime);
        TextView started_datetime = (TextView) convertView.findViewById(R.id.started_datetime);
        TextView order_datetime = (TextView) convertView.findViewById(R.id.order_datetime);
        TextView time_completed = (TextView) convertView.findViewById(R.id.time_job);
        TextView order_id = (TextView) convertView.findViewById(R.id.order_id);
        TextView main_service_name = (TextView) convertView.findViewById(R.id.service_name);
        ListView delivery_service_list = convertView.findViewById(R.id.delivery_service_list);

        LinearLayout doc_item_lay = (LinearLayout) convertView.findViewById(R.id.document_item_layout);
        final LinearLayout doc_info_lay = (LinearLayout) convertView.findViewById(R.id.doc_info_section_lay);

        ImageView doc_image = (ImageView) convertView.findViewById(R.id.doc_image_view);
        TextView delivery_person = (TextView) convertView.findViewById(R.id.delivery_person);
        TextView item_description = (TextView) convertView.findViewById(R.id.doc_description);


        TextView total = (TextView) convertView.findViewById(R.id.order_total);
        TextView instructions = (TextView) convertView.findViewById(R.id.instructions);
        TextView job_hrs = (TextView) convertView.findViewById(R.id.job_hrs);
        TextView distance = (TextView) convertView.findViewById(R.id.distance_txt);
        TextView job_start = (TextView) convertView.findViewById(R.id.time_job);
        TextView booking_type = (TextView) convertView.findViewById(R.id.job_type);
        TextView order_person_name = (TextView) convertView.findViewById(R.id.customer_name);
        ImageView customer_img = (ImageView) convertView.findViewById(R.id.customer_img);
        LinearLayout meet_datetime_layout = (LinearLayout) convertView.findViewById(R.id.meet_datetime_layout);
        CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.order_person_image);
        LinearLayout first_main = (LinearLayout) convertView.findViewById(R.id.first_main_layout);
        LinearLayout first_middle = (LinearLayout) convertView.findViewById(R.id.first_middle_layout);
        LinearLayout job_hr_lay = (LinearLayout) convertView.findViewById(R.id.hours_lay);
        LinearLayout distance_lay = (LinearLayout) convertView.findViewById(R.id.distance_lay);
        LinearLayout second_main = (LinearLayout) convertView.findViewById(R.id.second_main_layout);
        LinearLayout instruction_layout = (LinearLayout) convertView.findViewById(R.id.instruction_layout);
        ImageView uniform = (ImageView) convertView.findViewById(R.id.uniform);
        LinearLayout uniform_layout = (LinearLayout) convertView.findViewById(R.id.uniform_layout);



        CompletedJobObject object = list.get(position);


        order_id.setText(object.getOrder_id());
        main_service_name.setText(object.getMain_service_name());

        try {
            String[] time_date = object.getCompleted_Time().split(Pattern.quote(" "));
            String adjusted_date = AdjustDateFormat(time_date[0]);
            time_completed.setText(object.getCompleted_Time());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (list.get(position).getOrder_service_infoList().size() > 0){
                if (object.getItem_type()!=null) {
                    if (!object.getItem_type().equalsIgnoreCase("null")) {
                        Delivery_Services_Adapter delivery_services_adapter = new Delivery_Services_Adapter(list.get(position).getOrder_service_infoList(),context);
                        delivery_service_list.setAdapter(delivery_services_adapter);
                        delivery_service_list.setVisibility(View.VISIBLE);
                        UtilsManager.setListViewHeightBasedOnChildren(delivery_service_list);
                    }
                }
            }
            UtilsManager.setListViewHeightBasedOnChildren(delivery_service_list);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (object.getInstructions().equalsIgnoreCase("N/A")) {
            instruction_layout.setVisibility(View.GONE);
            hasInstructions = false;
        }
        else
        {
            instruction_layout.setVisibility(View.VISIBLE);
            hasInstructions = true;
        }

        if (list.get(position).getUniform_id().equalsIgnoreCase("0")) {
            uniform_layout.setVisibility(View.GONE);
            hasUniform = false;
        }
        else
        {

            uniform_layout.setVisibility(View.VISIBLE);
            hasUniform = true;

            if (list.get(position).getUniform_id().equalsIgnoreCase("1"))
            {
                uniform.setImageResource(R.drawable.premium);

            }
            else if (list.get(position).getUniform_id().equalsIgnoreCase("2"))
            {
                uniform.setImageResource(R.drawable.casual);

            }
            else if (list.get(position).getUniform_id().equalsIgnoreCase("3"))
            {
                uniform.setImageResource(R.drawable.intermediate);

            }
            else if (list.get(position).getUniform_id().equalsIgnoreCase("4"))
            {
                uniform.setImageResource(R.drawable.basic);

            }

            uniform.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).getUniform_id().equalsIgnoreCase("1"))
                    {
                        showZoomImage(R.drawable.premium);
                    }
                    else if (list.get(position).getUniform_id().equalsIgnoreCase("2"))
                    {
                        showZoomImage(R.drawable.casual);
                    }
                    else if (list.get(position).getUniform_id().equalsIgnoreCase("3"))
                    {
                        showZoomImage(R.drawable.intermediate);
                    }
                    else if (list.get(position).getUniform_id().equalsIgnoreCase("4"))
                    {
                        showZoomImage(R.drawable.basic);
                    }
                }
            });

        }


        if (object.getItem_type()!=null) {
            if (!object.getItem_type().equalsIgnoreCase("null")) {
                delivery_person.setText(object.getDelivery_person());
                item_description.setText(object.getItem_desc());

                Log.e(TAG, "getView: "+object.getDoc_image());
                Picasso.with(context).load(object.getDoc_image())
                        .placeholder(R.drawable.docx_icon)
                        .into(doc_image);
                doc_item_lay.setVisibility(View.VISIBLE);
            }
        }

        doc_item_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doc_info_lay.getVisibility()==View.VISIBLE){
                    doc_info_lay.setVisibility(View.GONE);
                }else {
                    doc_info_lay.setVisibility(View.VISIBLE);
                }
            }
        });


        if (list.get(position).getUniform_id().equalsIgnoreCase("0")) {
            uniform_layout.setVisibility(View.GONE);
            hasUniform = false;
        }
        else
        {

            uniform_layout.setVisibility(View.VISIBLE);
            hasUniform = true;

            if (list.get(position).getUniform_id().equalsIgnoreCase("1"))
            {
                uniform.setImageResource(R.drawable.premium);

            }
            else if (list.get(position).getUniform_id().equalsIgnoreCase("2"))
            {
                uniform.setImageResource(R.drawable.casual);

            }
            else if (list.get(position).getUniform_id().equalsIgnoreCase("3"))
            {
                uniform.setImageResource(R.drawable.intermediate);

            }
            else if (list.get(position).getUniform_id().equalsIgnoreCase("4"))
            {
                uniform.setImageResource(R.drawable.basic);

            }

            uniform.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).getUniform_id().equalsIgnoreCase("1"))
                    {
                        showZoomImage(R.drawable.premium);
                    }
                    else if (list.get(position).getUniform_id().equalsIgnoreCase("2"))
                    {
                        showZoomImage(R.drawable.casual);
                    }
                    else if (list.get(position).getUniform_id().equalsIgnoreCase("3"))
                    {
                        showZoomImage(R.drawable.intermediate);
                    }
                    else if (list.get(position).getUniform_id().equalsIgnoreCase("4"))
                    {
                        showZoomImage(R.drawable.basic);
                    }
                }
            });

        }

        drop_location.setText(object.getDestination());


        String[] date_time = object.getDatetime_meet().split(Pattern.quote(" "));

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss");
            Date date = dateFormat.parse(object.getDatetime_meet());
            dateFormat = new SimpleDateFormat("dd-MM-yyyy h:mm");
            String formatted_date = dateFormat.format(date);
            object.setDatetime_meet(formatted_date);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        order_datetime.setText(ApplyFormat(object.getDatetime_ordered()));
        accepted_datetime.setText(ApplyFormat(object.getDatetime_accepted()));
        started_datetime.setText(ApplyFormat(object.getDatetime_started()));
        meet_date.setText(ApplyFormat(object.getDatetime_meet()));
        time_completed.setText(ApplyFormat(object.getCompleted_Time()));
       // meet_time.setText(date_time[1]);
        meet_location.setText(object.getMeet_loc());
        instructions.setText(object.getInstructions());
        total.setText(Constants.currency+object.getMember_share());

        if (instructions.getText().toString().equalsIgnoreCase("")){
            instruction_layout.setVisibility(View.GONE);
        }

        if (object.getNo_of_hours().equalsIgnoreCase("0")){
            job_hr_lay.setVisibility(View.GONE);
            distance_lay.setVisibility(View.VISIBLE);
            distance.setText(object.getTotal_distance()+" KM");
        }else {
            job_hr_lay.setVisibility(View.VISIBLE);
            distance_lay.setVisibility(View.GONE);
            job_hrs.setText(object.getNo_of_hours() + " hours");
        }

        booking_type.setText(object.getBooking_type());

        order_person_name.setText(object.getCustomer_name());
        Log.e(TAG, "getView: "+object.getCustomer_image());
        Picasso.with(context).load(object.getCustomer_image()).placeholder(R.drawable.ic_avatar).into(customer_img);

       // job_start.setText(date_time[1]);


        return convertView;
    }

    private String AdjustDateFormat(String s)
    {
        String new_date = s;
        try {
            Date date = current_format.parse(s);
            new_date = desired_format.format(date);
            return new_date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new_date;
    }

    private boolean IsImmediate(String datetime_meet) {

        if (datetime_meet.contains("0000"))
            return true;
        else
            return false;
    }

    private String ApplyFormat(String date_string)
    {
        String am_pm = "pm";
        if (date_string.contains("am") || date_string.contains("AM") || date_string.contains("Am"))
            am_pm = "am";
        try {
            date_string = date_string.replace(" AM","").replace(" PM","");
            Log.e("date",date_string);
            Date current_date = new SimpleDateFormat("dd-MM-yyyy h:mm").parse(date_string);
            // return new SimpleDateFormat("dd-MM-yyyy h:mm").format(current_date)+" "+am_pm+"";
            return new SimpleDateFormat("dd MMM yyyy h:mm").format(current_date)+" "+am_pm+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date_string+" "+am_pm;
    }


    Dialog dialog;

    private void showZoomImage(int resource)
    {
        dialog = new Dialog(context);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.zoom_dialog_layout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
        ImageView bmImage = (ImageView) dialog.findViewById(R.id.img_receipt);
        bmImage.setImageResource(resource);
        dialog.setCancelable(true);
        dialog.show();

    }


}
