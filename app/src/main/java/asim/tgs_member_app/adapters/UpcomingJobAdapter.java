package asim.tgs_member_app.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.chat.ChatActivity;
import asim.tgs_member_app.chat.Customer;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.OTWStateObject;
import asim.tgs_member_app.models.SuggestedJobObject;
import asim.tgs_member_app.registration.ui.utils.Util;
import asim.tgs_member_app.service.BackgroundLocationService;
import asim.tgs_member_app.utils.OTWRidePrefs;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Asim Shahzad on 2/19/2018.
 */
public class UpcomingJobAdapter extends BaseAdapter
{
    private static final String TAG = "UpcomingJobAdapter";

    private List<SuggestedJobObject> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private OTWStateObject otwStateObject;
    private OTWRidePrefs otwRidePrefs = null;

    public UpcomingJobAdapter(List<SuggestedJobObject> list, Context context) {
        this.list = list;
        this.context = context;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        otwRidePrefs = new OTWRidePrefs(context);
        otwStateObject = otwRidePrefs.getOTWState();
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            convertView = layoutInflater.inflate(R.layout.accepted_jobs,null);
        }


        TextView meet_location = (TextView) convertView.findViewById(R.id.meet_location);
        TextView destination = (TextView) convertView.findViewById(R.id.destination);
        TextView meet_date = (TextView) convertView.findViewById(R.id.meet_datetime);
        TextView accepted_date_time = (TextView) convertView.findViewById(R.id.accepted_datetime);
        TextView order_datetime = (TextView) convertView.findViewById(R.id.order_datetime);
        TextView total = (TextView) convertView.findViewById(R.id.order_total);
        Button otw_button = (Button) convertView.findViewById(R.id.otw_button);
        TextView type = (TextView) convertView.findViewById(R.id.job_type);
        TextView service_name = (TextView) convertView.findViewById(R.id.service_name);
        TextView instructions = (TextView) convertView.findViewById(R.id.instructions);
        TextView job_hrs = (TextView) convertView.findViewById(R.id.job_hours);
        TextView job_starts_in_heading_text = (TextView) convertView.findViewById(R.id.job_starts_in_heading_text);
        //job_starts_in_heading_text
        TextView timer_text = (TextView) convertView.findViewById(R.id.time_job);
        final ImageView chat = (ImageView) convertView.findViewById(R.id.chat_btn);
        final CircleImageView customer_img = (CircleImageView) convertView.findViewById(R.id.customer_img);
        TextView customer_name = (TextView) convertView.findViewById(R.id.customer_name);
        TextView job_status = (TextView) convertView.findViewById(R.id.job_status);


        LinearLayout doc_item_lay = (LinearLayout) convertView.findViewById(R.id.document_item_layout);
        final LinearLayout doc_info_lay = (LinearLayout) convertView.findViewById(R.id.doc_info_section_lay);
        final LinearLayout doc_extra_info_lay = (LinearLayout) convertView.findViewById(R.id.document_extra_info);
        ListView delivery_service_list = convertView.findViewById(R.id.services_list);


        ImageView doc_image = (ImageView) convertView.findViewById(R.id.doc_image_view);
        TextView delivery_person = (TextView) convertView.findViewById(R.id.delivery_person);
        TextView item_description = (TextView) convertView.findViewById(R.id.doc_description);

        TextView pick_contact = (TextView) convertView.findViewById(R.id.pickup_contact_number);
        TextView pick_house_no = (TextView) convertView.findViewById(R.id.pickup_hous_no);

        TextView dest_contact = (TextView) convertView.findViewById(R.id.destination_contact_number);
        TextView dest_house_no = (TextView) convertView.findViewById(R.id.destination_hous_no);


        Button accept_job = (Button) convertView.findViewById(R.id.accept_job);
        Button cancel_job = (Button) convertView.findViewById(R.id.reject_job);
        final LinearLayout option_layout = (LinearLayout) convertView.findViewById(R.id.option_layout);
        final LinearLayout job_starts_in_layout = (LinearLayout) convertView.findViewById(R.id.jobs_starts_in_btn);
        final LinearLayout meet_date_layout = (LinearLayout) convertView.findViewById(R.id.meet_date_layout);
        final RelativeLayout chat_layout = (RelativeLayout) convertView.findViewById(R.id.chat_layout);
        LinearLayout instruction_layout = (LinearLayout) convertView.findViewById(R.id.instruction_layout);
        LinearLayout job_hrs_layout = (LinearLayout) convertView.findViewById(R.id.job_hrs_layout);

        chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat.performClick();
            }
        });

        final SuggestedJobObject object = list.get(position);
        cancel_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelJob(position);

            }
        });

        if (object.getOtw_state().equalsIgnoreCase("1") &&
                (object.getStatus_id().equalsIgnoreCase("2")
                || object.getStatus_id().equalsIgnoreCase("5") ||
                        object.getStatus_id().equalsIgnoreCase("6"))){
            if (otwStateObject==null){
                otwStateObject = new OTWStateObject();
                otwStateObject.setOrder_id(object.getOrder_id());
                otwStateObject.setOtw_state(1);
                otwStateObject.setPickup(object.getMeet_loc());
                otwStateObject.setDestination(object.getDestination());
                otwStateObject.setCustomer_id(object.getCustomer_id());
                otwRidePrefs.saveOTWState(otwStateObject);
            }else {
                otw_button.setVisibility(View.GONE);
                if (otwStateObject.getOtw_state()==0 && otwStateObject.isFinished()){
                    otw_button.setVisibility(View.GONE);
                }
                else if (otwStateObject.getOtw_state()==1){
                    otw_button.setText("On The Way");
                }else if (otwStateObject.getOtw_state()==2){
                    otw_button.setText("I have Reached");
                }
            }


        }else {
            otw_button.setVisibility(View.GONE);
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
                doc_info_lay.setVisibility(View.GONE);

                pick_contact.setText(object.getPick_contact_obj().getContact());
                pick_house_no.setText(object.getPick_contact_obj().getAddress());

                dest_contact.setText(object.getDestination_contact_obj().getContact());
                dest_house_no.setText(object.getDestination_contact_obj().getAddress());


            }
        }

        doc_item_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doc_info_lay.getVisibility()==View.VISIBLE){
                    doc_info_lay.setVisibility(View.GONE);
                    doc_extra_info_lay.setVisibility(View.GONE);
                }else {
                    doc_info_lay.setVisibility(View.VISIBLE);
                    doc_extra_info_lay.setVisibility(View.VISIBLE);
                }
            }
        });

        otw_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String member_id = context.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE).getString(Constants.PREFS_USER_ID,"0");

                if (otwStateObject.getOtw_state()==1){
                   /* otwStateObject.setOtw_state(2);
                    otwRidePrefs.saveOTWState(otwStateObject);
                    notifyDataSetChanged();*/
                    callOTWServer(object.getMain_id(),object.getCustomer_id(),object.getOrder_id(),"5",member_id,"Member is on the way.");

                }else if (otwStateObject.getOtw_state()==2){
                    callOTWServer(object.getMain_id(),object.getCustomer_id(),object.getOrder_id(),"6",member_id,"Your member has reached at pickup.");

                   /* otwStateObject.setOtw_state(0);
                    otwStateObject.setFinished(true);
                    otwRidePrefs.saveOTWState(otwStateObject);
                    notifyDataSetChanged();*/
                }

            }
        });

        accept_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AcceptJob(position);

            }
        });

        if (list.get(position).getJob_status().equalsIgnoreCase("Paid"))
        {
            job_status.setText("Job Confirmed");
            chat_layout.setVisibility(View.VISIBLE);
        }
        else
        {
            if (list.get(position).getStatus_id().equalsIgnoreCase("1")) {
                job_status.setText("Job Applied");
                chat_layout.setVisibility(View.INVISIBLE);
            }
            else if (list.get(position).getStatus_id().equalsIgnoreCase("2")) {
                job_status.setText("Job Accepted");
                chat_layout.setVisibility(View.VISIBLE);
            }
            else {
                job_status.setText("Job Rejected");
                chat_layout.setVisibility(View.INVISIBLE);
            }



        }

        destination.setText(list.get(position).getDestination());

        customer_name.setText(object.getCustomer_name());
        String full_image_path = "http://kogha.my/system/uploads/customer_profile_images/"+object.getCustomer_image();
        Log.e(TAG, "getView: "+full_image_path );
        Picasso.with(context).load(full_image_path)
                .placeholder(R.drawable.ic_avatar)
        .into(customer_img);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat_activity = new Intent(context, ChatActivity.class);
                Customer customer = new Customer();
                customer.setC_id(list.get(position).getCustomer_id());
                customer.setC_image(list.get(position).getCustomer_image());
                customer.setC_name(list.get(position).getCustomer_name());
                customer.setC_lat("2.33");
                customer.setC_lng("56.44");
                chat_activity.putExtra("customer",customer);
                chat_activity.putExtra("customer_id",list.get(position).getCustomer_id());
                chat_activity.putExtra("order_id",list.get(position).getOrder_id());
                context.startActivity(chat_activity);
            }
        });


        if (object.getInstructions()!=null) {
            if (object.getInstructions().equals("N/A") || object.getInstructions().equalsIgnoreCase("")) {
                object.setInstructions("no instructions ");
                instruction_layout.setVisibility(View.GONE);
            }else {
                instruction_layout.setVisibility(View.VISIBLE);
                instructions.setText(object.getInstructions());
            }

        }else {
            instruction_layout.setVisibility(View.GONE);
        }


        service_name.setText(object.getService_type_name());

        try {
            if (list.get(position).getBooking_type()!=null)
                if (list.get(position).getBooking_type().equalsIgnoreCase("Immediate")
                        ||
                        list.get(position).getBooking_type().equalsIgnoreCase("")
                        ) {
                    job_starts_in_layout.setVisibility(View.GONE);
                    list.get(position).setChatEnabled(true);
                    meet_date_layout.setVisibility(View.VISIBLE);
                }
            else
                {
                    job_starts_in_layout.setVisibility(View.VISIBLE);
                    meet_date_layout.setVisibility(View.VISIBLE);
                }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


     /*   try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss");
            Date date = new Date();
            date = simpleDateFormat.parse(list.get(position).getDatetime_meet());
            String formmated_date = new SimpleDateFormat("yyyy/MM/dd - h:mm:ss").format(date);
           // meet_date.setText(new SimpleDateFormat("yyyy/MM/dd-h:mm:ss").parse(list.get(position).getDatetime_meet()).toString());
           meet_date.setText(formmated_date);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        order_datetime.setText(ApplySlashFormat(list.get(position).getDatetime_ordered()));
        meet_date.setText(ApplySlashFormat(list.get(position).getDatetime_meet()));
        accepted_date_time.setText(list.get(position).getDatetime_accepted());

        if (object.getJob_starts_in().equalsIgnoreCase("started")) {
            job_starts_in_heading_text.setText("Job started");
            timer_text.setVisibility(View.GONE);
        }

        type.setText(object.getBooking_type());
        meet_location.setText(object.getMeet_loc());
        String comma_seperated_price = object.getMember_share();
        try {
            DecimalFormat formatter = new DecimalFormat("##,###.00");

            if (!object.getMember_share().contains("."))
                comma_seperated_price = formatter.format(Integer.parseInt(object.getMember_share()));
            else
                comma_seperated_price = formatter.format(Double.parseDouble(object.getMember_share()));


            if (comma_seperated_price.equalsIgnoreCase(".00"))
                comma_seperated_price = "0.00";

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }




        total.setText(Constants.currency+comma_seperated_price);
        job_hrs.setText(object.getNo_of_hours()+" "+context.getResources().getString(R.string.hours)+"");
        timer_text.setText(object.getJob_starts_in());

       /* if (list.get(position).isShow_options()) {
            option_layout.setVisibility(View.VISIBLE);
        }
        else
        {
            option_layout.setVisibility(View.GONE);

        }*/
        if (!list.get(position).getBooking_type().equalsIgnoreCase("Immediate") &&
                list.get(position).getJob_starts_in().equalsIgnoreCase("not started yet"))
            showCountDown(timer_text,list.get(position),position);

       /* if (list.get(position).isChatEnabled())
            chat_layout.setVisibility(View.VISIBLE);
        else
            chat_layout.setVisibility(View.GONE);
*/

        if (list.get(position).getNo_of_hours().equalsIgnoreCase("0")) {
            job_hrs_layout.setVisibility(View.GONE);
            instruction_layout.setVisibility(View.GONE);
        }



        if (list.get(position).getService_list().size() > 0){
            if (object.getItem_type()!=null) {
                if (!object.getItem_type().equalsIgnoreCase("null")) {
                    Delivery_Services_Adapter delivery_services_adapter = new Delivery_Services_Adapter(list.get(position).getService_list(),context);
                    delivery_service_list.setAdapter(delivery_services_adapter);
                    delivery_service_list.setVisibility(View.VISIBLE);
                    UtilsManager.setListViewHeightBasedOnChildren(delivery_service_list);
                }
            }
        }

        return convertView;
    }

    private ProgressDialog progressDialog;
    private void showDialog(String message){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void hideDialoge(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void callOTWServer(final String main_id,final String customer_id,final String order_id,final String otw_state,final String member_id,final String message) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("main_id",main_id);
        requestParams.put("order_id",order_id);
        requestParams.put("status",otw_state);
        requestParams.put("customer_id",customer_id);
        requestParams.put("member_id",member_id);
        requestParams.put("key","tgs_appkey_amin");
        requestParams.put("message",message);

        asyncHttpClient.post(context, Constants.Host_Address + "members/change_status", requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Please wait");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equalsIgnoreCase("success")){
                        if (otw_state.equalsIgnoreCase("5")){
                            otwStateObject.setOtw_state(2);
                            otwRidePrefs.saveOTWState(otwStateObject);
                            notifyDataSetChanged();

                            if (!UtilsManager.isMyServiceRunning(context,BackgroundLocationService.class)){
                                UtilsManager.startLocationService(context,0);
                            }

                        }else if (otw_state.equalsIgnoreCase("6")){
                            otwStateObject.setOtw_state(0);
                            otwStateObject.setFinished(true);
                            otwRidePrefs.saveOTWState(otwStateObject);
                            notifyDataSetChanged();

                            if (UtilsManager.isMyServiceRunning(context,BackgroundLocationService.class)){
                                UtilsManager.startLocationService(context,1);
                            }

                        }

                        Toast.makeText(context,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(context,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    private String ApplyFormat(String date_string)
    {
        String am_pm = "pm";
        if (date_string.contains("am") || date_string.contains("AM") || date_string.contains("Am"))
            am_pm = "am";
        try {
            date_string = date_string.replace(" am","").replace(" pm","");
            Log.e("date",date_string);
            Date current_date = new SimpleDateFormat("dd-MM-yyyy h:mm").parse(date_string);
            // return new SimpleDateFormat("dd-MM-yyyy h:mm").format(current_date)+" "+am_pm+"";
            return new SimpleDateFormat("dd MMM yyyy h:mm").format(current_date)+" "+am_pm+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date_string.toLowerCase().contains("am") || date_string.toLowerCase().contains("pm"))
            return date_string;

        return date_string+" "+am_pm;
    }

    private String ApplySlashFormat(String date_string)
    {
        String am_pm = "pm";
        if (date_string.contains("am") || date_string.contains("AM") || date_string.contains("Am"))
            am_pm = "am";
        try {
            date_string = date_string.replace(" am","").replace(" pm","");
            Log.e("date",date_string);
            Date current_date = new SimpleDateFormat("dd/MM/yyyy h:mm").parse(date_string);
            // return new SimpleDateFormat("dd-MM-yyyy h:mm").format(current_date)+" "+am_pm+"";
            return new SimpleDateFormat("dd MMM yyyy h:mm").format(current_date)+" "+am_pm+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date_string.toLowerCase().contains("am") || date_string.toLowerCase().contains("pm"))
            return date_string;

        return date_string+" "+am_pm;
    }


    private CountDownTimer jobstartsin_timer;
    private void showCountDown(final TextView timer_text, final SuggestedJobObject suggestedJobObject,final int position)
    {
        String start_date = suggestedJobObject.getDatetime_meet().replace("AM","").replace("PM","");
        String ordered_date = suggestedJobObject.getDatetime_meet().replace(" AM","").replace(" PM","");
        String server_time = suggestedJobObject.getServer_time().replace(" AM","").replace(" PM","");

        try {

            String dateString = start_date;
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateString);
            final Date convertedDate = new SimpleDateFormat("dd-MM-yyyy h:mm").parse(dateString);
           // convertedDate = dateFormat.parse(dateString);
            Calendar old_cal = Calendar.getInstance();
            old_cal.setTime(convertedDate);
            int hrs = old_cal.get(Calendar.HOUR_OF_DAY);
            int min = old_cal.get(Calendar.MINUTE);
            int sec = old_cal.get(Calendar.SECOND);

            int month = old_cal.get(Calendar.MONTH);
            month++;
            int day = old_cal.get(Calendar.DAY_OF_MONTH);

            SimpleDateFormat dateFormat_server = new SimpleDateFormat(server_time);
            final Date convertedDate_server = new SimpleDateFormat("yyyy-MM-dd h:mm:ss").parse(server_time);

            Calendar calender = Calendar.getInstance();
            calender.setTime(convertedDate_server);

            int current_month = calender.get(Calendar.MONTH);
            int current_day = calender.get(Calendar.DAY_OF_MONTH);

            current_month++;
            if (month==current_month)
            {
                if (current_day==day)
                {
                    int current_hrs = calender.get(Calendar.HOUR_OF_DAY);
                    int current_min = calender.get(Calendar.MINUTE);
                    int current_sec = calender.get(Calendar.MINUTE);

                    if (current_hrs>=12)
                        current_hrs = current_hrs - 12;

                    if (hrs>=12)
                        hrs = hrs - 12;

                    if (hrs==0)
                        hrs = 12;


                    int rem_hrs = hrs - current_hrs;
                    int rem_minutes = min - current_min;
                    int rem_seconds = sec - current_sec;

                    if (rem_minutes<0)
                    {
                       int tem_min = rem_hrs * 60;
                        tem_min = tem_min - (rem_minutes*-1);
                        rem_hrs = tem_min/60;
                        rem_minutes = tem_min%60;
                    }
                    if (rem_seconds<0)
                    {
                        int tem_sec = rem_minutes * 60;
                        rem_minutes = tem_sec /60;
                        rem_seconds = tem_sec%60;
                    }

                    if (rem_hrs<2)
                        list.get(position).setChatEnabled(true);
                    else
                        list.get(position).setChatEnabled(false);

                    long milliseconds = (rem_hrs * 60 * 60 * 1000)+(rem_minutes * 60 * 1000)+ (rem_seconds*1000);

                    jobstartsin_timer = new CountDownTimer(milliseconds,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long seconds = millisUntilFinished /1000;
                            long mins = seconds/60;
                            seconds = seconds%60;

                            long hrs = mins/60;
                            mins = mins%60;

                            String calcuated_time = "";

                            if (hrs<10 && mins<10 && seconds>=10)
                                calcuated_time= "0"+hrs+":"+"0"+mins+":"+seconds;
                           else if (hrs<10 && mins>=10 && seconds>=10)
                                calcuated_time= "0"+hrs+":"+mins+":"+seconds;
                            else if (hrs>=10 && mins<10 && seconds>=10)
                                calcuated_time = hrs+":0"+mins+":"+seconds;
                            else if (hrs>=10 && mins>=10 && seconds<10)
                                calcuated_time = hrs+":"+mins+":0"+seconds;

                            list.get(position).setJob_starts_in(calcuated_time);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFinish() {
                           // Toast.makeText(context,"Job Started",Toast.LENGTH_LONG).show();
                            list.get(position).setJob_starts_in("started");
                            notifyDataSetChanged();
                        }
                    };
                    jobstartsin_timer.start();

                }
                else if (day!=0 && day > current_day)
                {
                    //timer_text.setText(day-current_day+" days");
                    list.get(position).setJob_starts_in(day-current_day+" days");
                    notifyDataSetChanged();
                }
                else if (day<current_day)
                {
                    list.remove(position);
                    notifyDataSetChanged();
                }
                else if (day==0)
                {
                    jobstartsin_timer = new CountDownTimer(12000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long seconds = millisUntilFinished /1000;
                            long mins = seconds/60;
                            seconds = seconds%60;

                            long hrs = mins/60;
                            mins = mins%60;

                            String calcuated_time = "";

                            if (hrs<10 && mins<10 && seconds>=10)
                                calcuated_time= "0"+hrs+":"+"0"+mins+":"+seconds;
                            else if (hrs<10 && mins>=10 && seconds>=10)
                                calcuated_time= "0"+hrs+":"+mins+":"+seconds;
                            else if (hrs>=10 && mins<10 && seconds>=10)
                                calcuated_time = hrs+":0"+mins+":"+seconds;
                            else if (hrs>=10 && mins>=10 && seconds<10)
                                calcuated_time = hrs+":"+mins+":0"+seconds;

                            list.get(position).setJob_starts_in(calcuated_time);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFinish() {
                            //Toast.makeText(context,"Job Started",Toast.LENGTH_LONG).show();
                            list.get(position).setJob_starts_in("started");
                            notifyDataSetChanged();

                        }
                    };
                    jobstartsin_timer.start();
                }
            }
            else
            {
                list.get(position).setJob_starts_in("next month");
                notifyDataSetChanged();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private void CancelJob(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to cancel?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                list.get(position).setShow_options(false);
                confirmation.dismiss();
                notifyDataSetChanged();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmation.dismiss();
            }
        });



        confirmation= builder.create();
        confirmation.show();
    }

    Dialog confirmation;
    private void AcceptJob(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to accept?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AcceptJobApi(position);
                list.get(position).setShow_options(false);
                confirmation.dismiss();
                notifyDataSetChanged();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               confirmation.dismiss();
            }
        });



        confirmation= builder.create();
        confirmation.show();
    }

    AsyncHttpClient httpClient = new AsyncHttpClient();
    SharedPreferences settings;
    String key;
    private void AcceptJobApi(final int position)
    {
         httpClient.setConnectTimeout(40000);

         settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
         String member_id = settings.getString(Constants.PREFS_USER_ID,"0");
         key ="tgs_appkey_amin";// settings.getString(Constants.PREFS_ACCESS_TOKEN,"tgs_appkey_amin");


        Log.e("suggested jobs",  Constants.Host_Address + "members/accept_order/"+member_id+"/"+list.get(position).getOrder_id()+"/"+list.get(position).getOrder_item_id()+"/"+list.get(position).getMember_share()+"/"+key);
        httpClient.get(context, Constants.Host_Address + "members/accept_order/"+member_id+"/"+list.get(position).getOrder_id()+"/"+list.get(position).getOrder_item_id()+"/"+list.get(position).getMember_share()+"/"+key, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Accepting job...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String responseData = new String(responseBody,"UTF-8");
                    Log.e("response success",responseData);
                    JSONObject object = new JSONObject(responseData);
                    JSONObject data = object.getJSONObject("data");
                    String datetime_accepted = data.getString("datetime_accepted");
                    String datetime_reached = data.getString("datetime_reached");
                    String order_item_id = data.getString("order_item_id");

                    list.get(position).setStatus("In progress");
                    notifyDataSetChanged();

                    Toast.makeText(context,"Job accepted",Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String responseData = new String(responseBody,"UTF-8");
                    Log.e("response failure",responseData);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
