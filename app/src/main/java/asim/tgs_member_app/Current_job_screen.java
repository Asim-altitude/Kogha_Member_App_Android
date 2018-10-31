package asim.tgs_member_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import asim.tgs_member_app.chat.ChatActivity;
import asim.tgs_member_app.models.Constants;
import cz.msebera.android.httpclient.Header;

public class Current_job_screen extends AppCompatActivity {

    private String pickup,detination,mem_share,customer_name,customer_image,cust_mobile,cust_id,order_id
            ,meet_date_,job_hrs_,total_distance_;

    private TextView pickup_text,detination_text,mem_share_text,customer_name_text,meet_date,total_distance;
    private ImageView customer_image_url;
    private LinearLayout chat_btn,call_btn;
    private Button complete_job_btn;
    private String member_id;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_current_job_);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.white_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        pickup = getIntent().getStringExtra("pickup");
        detination = getIntent().getStringExtra("detination");
        mem_share = getIntent().getStringExtra("mem_share");
        customer_name = getIntent().getStringExtra("customer_name");
        customer_image = getIntent().getStringExtra("customer_image");
        cust_mobile = getIntent().getStringExtra("cust_mobile");
        cust_id = getIntent().getStringExtra("cust_id");
        order_id = getIntent().getStringExtra("order_id");
      //  meet_date_ = getIntent().getStringExtra("meet_date");
        total_distance_ = getIntent().getStringExtra("total_distance");

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        member_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"117");

        complete_job_btn = (Button) findViewById(R.id.complete_job_btn);
        complete_job_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeJob(order_id,member_id);
            }
        });

        setTitle("Current Job");

        pickup_text = (TextView) findViewById(R.id.meet_location);
        detination_text = (TextView) findViewById(R.id.destination_location);
        mem_share_text = (TextView) findViewById(R.id.member_share);
        customer_name_text = (TextView) findViewById(R.id.customer_name);
        total_distance = (TextView) findViewById(R.id.total_distance);
        customer_image_url = (ImageView) findViewById(R.id.customer_img);

        chat_btn = (LinearLayout) findViewById(R.id.chat_btn_layout);
        call_btn = (LinearLayout) findViewById(R.id.call_btn_layout);

        call_btn.setVisibility(View.GONE);

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat_activity = new Intent(Current_job_screen.this, ChatActivity.class);
                chat_activity.putExtra("customer_id",cust_id);
                chat_activity.putExtra("order_id",order_id);
                startActivity(chat_activity);
            }
        });

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+cust_mobile+""));
                startActivity(intent);
            }
        });


        pickup_text.setText(pickup);
        detination_text.setText(detination);
        customer_name_text.setText(customer_name);
        total_distance.setText(total_distance_+"KM");

        mem_share_text.setText(mem_share);

        Glide.with(Current_job_screen.this).load(Constants.Customer_Image_BASE_PATH+customer_image).into(customer_image_url);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private ProgressDialog progressDialog;
    private void showDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Applying for job completion...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private void completeJob(String order_id,String member_id)
    {
        asyncHttpClient.setConnectTimeout(20000);

        asyncHttpClient.get(Current_job_screen.this, Constants.Host_Address+"members/cancel_job/" + order_id + "/" + member_id + "/tgs_appkey_amin", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            showDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();


                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    Log.e("response",jsonObject.toString());
                    finish();
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


                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    Log.e("fail response",jsonObject.toString());
                    finish();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

}
