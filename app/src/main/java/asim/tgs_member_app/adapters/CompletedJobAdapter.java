package asim.tgs_member_app.adapters;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
        TextView time_completed = (TextView) convertView.findViewById(R.id.time_job);

        TextView total = (TextView) convertView.findViewById(R.id.order_total);
        TextView instructions = (TextView) convertView.findViewById(R.id.instructions);
        TextView job_hrs = (TextView) convertView.findViewById(R.id.job_hours);
        TextView job_start = (TextView) convertView.findViewById(R.id.time_job);
        TextView order_person_name = (TextView) convertView.findViewById(R.id.order_person_name);
        LinearLayout meet_datetime_layout = (LinearLayout) convertView.findViewById(R.id.meet_datetime_layout);
        CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.order_person_image);
        LinearLayout first_main = (LinearLayout) convertView.findViewById(R.id.first_main_layout);
        LinearLayout first_middle = (LinearLayout) convertView.findViewById(R.id.first_middle_layout);
        LinearLayout second_main = (LinearLayout) convertView.findViewById(R.id.second_main_layout);
        LinearLayout instruction_layout = (LinearLayout) convertView.findViewById(R.id.instruction_layout);
        ImageView uniform = (ImageView) convertView.findViewById(R.id.uniform);
        LinearLayout uniform_layout = (LinearLayout) convertView.findViewById(R.id.uniform_layout);



        CompletedJobObject object = list.get(position);

        String[] time_date = object.getCompleted_Time().split(Pattern.quote(" "));
        String adjusted_date = AdjustDateFormat(time_date[0]);
        time_completed.setText(adjusted_date+"\n"+time_date[1]+" "+time_date[2]);

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

        if (list.get(position).getBooking_type().equalsIgnoreCase("immediate")) {

            if (!hasInstructions && !hasUniform)
            {
                meet_datetime_layout.setVisibility(View.GONE);
                int pixels_height = UtilsManager.convertDipToPixels(260.0f,context);

                ViewGroup.LayoutParams params = first_main.getLayoutParams();
                params.height = pixels_height;
                first_main.setLayoutParams(params);

                params = second_main.getLayoutParams();
                params.height = pixels_height;
                second_main.setLayoutParams(params);

                pixels_height = UtilsManager.convertDipToPixels(70.0f,context);

                params = first_middle.getLayoutParams();
                params.height = pixels_height;
                first_middle.setLayoutParams(params);
            }
            else if ((hasInstructions && !hasUniform) || (!hasInstructions && hasUniform))
            {
                meet_datetime_layout.setVisibility(View.GONE);
                int pixels_height = UtilsManager.convertDipToPixels(320.0f,context);

                ViewGroup.LayoutParams params = first_main.getLayoutParams();
                params.height = pixels_height;
                first_main.setLayoutParams(params);

                params = second_main.getLayoutParams();
                params.height = pixels_height;
                second_main.setLayoutParams(params);

                pixels_height = UtilsManager.convertDipToPixels(110.0f,context);

                params = first_middle.getLayoutParams();
                params.height = pixels_height;
                first_middle.setLayoutParams(params);
            }
            else
            {
                meet_datetime_layout.setVisibility(View.GONE);
                int pixels_height = UtilsManager.convertDipToPixels(380.0f,context);

                ViewGroup.LayoutParams params = first_main.getLayoutParams();
                params.height = pixels_height;
                first_main.setLayoutParams(params);

                params = second_main.getLayoutParams();
                params.height = pixels_height;
                second_main.setLayoutParams(params);

                pixels_height = UtilsManager.convertDipToPixels(150.0f,context);

                params = first_middle.getLayoutParams();
                params.height = pixels_height;
                first_middle.setLayoutParams(params);
            }

        }
        else {
            if (!hasInstructions && !hasUniform)
            {
                meet_datetime_layout.setVisibility(View.GONE);
                int pixels_height = UtilsManager.convertDipToPixels(320.0f,context);

                ViewGroup.LayoutParams params = first_main.getLayoutParams();
                params.height = pixels_height;
                first_main.setLayoutParams(params);

                params = second_main.getLayoutParams();
                params.height = pixels_height;
                second_main.setLayoutParams(params);

                pixels_height = UtilsManager.convertDipToPixels(110.0f,context);

                params = first_middle.getLayoutParams();
                params.height = pixels_height;
                first_middle.setLayoutParams(params);
            }
            else if ((hasInstructions && !hasUniform) || (!hasInstructions && hasUniform))
            {
                meet_datetime_layout.setVisibility(View.GONE);
                int pixels_height = UtilsManager.convertDipToPixels(380.0f,context);

                ViewGroup.LayoutParams params = first_main.getLayoutParams();
                params.height = pixels_height;
                first_main.setLayoutParams(params);

                params = second_main.getLayoutParams();
                params.height = pixels_height;
                second_main.setLayoutParams(params);

                pixels_height = UtilsManager.convertDipToPixels(150.0f,context);

                params = first_middle.getLayoutParams();
                params.height = pixels_height;
                first_middle.setLayoutParams(params);
            }
            else
            {
                meet_datetime_layout.setVisibility(View.GONE);
                int pixels_height = UtilsManager.convertDipToPixels(440.0f,context);

                ViewGroup.LayoutParams params = first_main.getLayoutParams();
                params.height = pixels_height;
                first_main.setLayoutParams(params);

                params = second_main.getLayoutParams();
                params.height = pixels_height;
                second_main.setLayoutParams(params);

                pixels_height = UtilsManager.convertDipToPixels(190.0f,context);

                params = first_middle.getLayoutParams();
                params.height = pixels_height;
                first_middle.setLayoutParams(params);
            }
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

        meet_date.setText(ApplyFormat(object.getDatetime_meet()));
       // meet_time.setText(date_time[1]);
        meet_location.setText(object.getMeet_loc());
        instructions.setText(object.getInstructions());
        total.setText(Constants.currency+object.getOrder_total());
       // job_hrs.setText(object.getDatetime_ordered());

        order_person_name.setText(object.getCustomer_name());
        Glide.with(context).load(object.getCustomer_image()).placeholder(R.drawable.ic_avatar).into(imageView);

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
