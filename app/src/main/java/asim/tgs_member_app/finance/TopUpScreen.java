package asim.tgs_member_app.finance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import asim.tgs_member_app.R;
import asim.tgs_member_app.finance.payment.MOLPaymentPage;
import asim.tgs_member_app.models.Constants;
import cz.msebera.android.httpclient.Header;

public class TopUpScreen extends AppCompatActivity {
    private static final String TAG = "TopUpScreen";

    String[] amount_values = {"50","100","200","300","500","1000","5000"};
    int selected_amount = 50;
    double total = 0.0;
    double pending=0.0;

    GridView amountListGrid;
    CreditPickAdapter creditPickAdapter;
    TextView pay_btn;

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");

        ((TextView)findViewById(R.id.pageTitle)).setText("Top Up");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_screen);
        setupToolbar();
        amountListGrid = findViewById(R.id.topupList);
        pay_btn = findViewById(R.id.pay_btn);
        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                savePaymentInfo();
            }
        });

        creditPickAdapter = new CreditPickAdapter(amount_values,this);
        amountListGrid.setAdapter(creditPickAdapter);
        amountListGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_amount = Integer.parseInt(amount_values[position]);
                Log.e(TAG, "onItemClick: "+selected_amount);
                creditPickAdapter.setSelected(position);
                creditPickAdapter.notifyDataSetChanged();
            }
        });


    }




    AsyncHttpClient async = new AsyncHttpClient();
    private void savePaymentInfo() {

        final SharedPreferences sha_prefs = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        String driverId = sha_prefs.getString(Constants.PREFS_USER_ID,"12");
        final String driverName = sha_prefs.getString(Constants.PREFS_USER_NAME,"Customer");
        final String driveremail = sha_prefs.getString(Constants.PREFS_USER_EMAIL,"abc@ridingpink.com");
        // final String driverPhone = sha_prefs.getString(PrefConstantsClass.PREF,"Driver");
//        String amount_ = amount_text.getText().toString().trim().replace("RM","");

        async.setConnectTimeout(20000);

        RequestParams params = new RequestParams();
        params.put("amount",selected_amount+"");
        params.put("user_id",driverId);
        params.put("user_type","2");
        params.put("key","tgs_appkey_amin");


        async.post(TopUpScreen.this, Constants.Host_Address+"customers/add_topup" ,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Please wait...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject json = new JSONObject(response);
                    JSONObject data = json.getJSONObject("data");//.getJSONObject(0);
                    String order_id = data.getString("order_id");
                    String payment_id = data.getString("payment_id");
                    String driver_id = data.getString("user_id");
                    String transaction_id = data.getString("transaction_id");

                    String phone = sha_prefs.getString(Constants.PREFS_USER_MOBILE,"010023");


                    Intent intent = new Intent(TopUpScreen.this, MOLPaymentPage.class)
                            .putExtra("name",driverName)
                            .putExtra("phone",phone)
                            .putExtra("email",driveremail)
                            .putExtra("transaction_id",transaction_id)
                            .putExtra("payment_id",payment_id)
                            .putExtra("order_id",order_id)
                            .putExtra("amount_to_pay",selected_amount+"")
                            .putExtra("driver_id",driver_id);

                    startActivity(intent);
                    finish();


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    hideDialoge();
                    String response = new String();
                    Log.e(TAG, "onSuccess: "+response);
                    Log.e("response",response);
                    Toast.makeText(TopUpScreen.this,"Could not redirect to MOLPay "+response,Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    ProgressDialog progressDialog;
    private void showDialog(String message){
        progressDialog = new ProgressDialog(TopUpScreen.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideDialoge(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }
}
