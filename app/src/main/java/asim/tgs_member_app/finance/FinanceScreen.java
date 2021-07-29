package asim.tgs_member_app.finance;

import android.app.Dialog;
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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.LedgerItem;
import cz.msebera.android.httpclient.Header;


public class FinanceScreen extends AppCompatActivity {
    private static final String TAG = "FinanceScreen";

    Button pay_btn;
    TextView user_credit;
    String user_id,group_id,credit="00";
    LinearLayout refund_btn;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    CreditHistoryAdapter creditHistoryAdapter;
    List<LedgerItem> ledgerItemList;
    ListView creditListview;
    int preLast = 0;

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");

        ((TextView)findViewById(R.id.pageTitle)).setText("Finance");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_screen);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        user_id = sharedPreferences.getString(Constants.PREFS_USER_ID, "0");
        group_id = sharedPreferences.getString(Constants.PREFS_USER_GROUP, "0");

        setupToolbar();
        pay_btn = findViewById(R.id.pay_btn);
        user_credit = findViewById(R.id.user_credit);
        refund_btn = findViewById(R.id.refund_btn);
        creditListview = findViewById(R.id.credit_list);
        ledgerItemList = new ArrayList<>();
        creditHistoryAdapter = new CreditHistoryAdapter(this,ledgerItemList);
        creditListview.setAdapter(creditHistoryAdapter);
        refund_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenRefundDialog();
            }
        });

        pay_btn.setVisibility(View.GONE);
        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinanceScreen.this, TopUpScreen.class);
                startActivity(intent);
            }
        });

        creditListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if(lastItem == totalItemCount)
                {
                    if(preLast!=lastItem)
                    {
                        //to avoid multiple calls for last item
                        Log.d("Last", "bottom reached");
                        preLast = lastItem;
                        index++;
                        getCreditHistory();
                    }
                }
            }
        });


        getCreditHistory();
    }



    @Override
    protected void onResume() {
        super.onResume();
        getDriverCreditInfoAPI();
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private int index = 0;
    private void getCreditHistory() {

        String userType = "2";
        if (group_id.equalsIgnoreCase("1"))
            userType = "5";

        asyncHttpClient.setConnectTimeout(20000);
        asyncHttpClient.get(FinanceScreen.this, Constants.Host_Address+"customers/history/"+user_id+"/"+userType+"/tgs_appkey_amin/"+index+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i=0;i<jsonArray.length();i++){
                        String pay_id  = jsonArray.getJSONObject(i).getString("order_id_or_payment_id");
                        String id  = jsonArray.getJSONObject(i).getString("id");
                        String created_date  = jsonArray.getJSONObject(i).getString("date_time");
                        String type  = jsonArray.getJSONObject(i).getString("type");
                        String amount  = jsonArray.getJSONObject(i).getString("amount");
                        String desc  = jsonArray.getJSONObject(i).getString("description");

                        LedgerItem ledgerItem = new LedgerItem();
                        ledgerItem.setId(id);
                        ledgerItem.setAmount(amount);
                        ledgerItem.setDate(created_date);
                        ledgerItem.setPay_id(pay_id);
                        ledgerItem.setIn_out(type);
                        ledgerItem.setDesc(desc);

                        ledgerItemList.add(ledgerItem);

                    }


                    creditHistoryAdapter.notifyDataSetChanged();



                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    private void getDriverCreditInfoAPI() {
        String userType = "2";
        if (group_id.equalsIgnoreCase("1"))
            userType = "5";

        asyncHttpClient.setConnectTimeout(20000);
        asyncHttpClient.get(FinanceScreen.this, Constants.Host_Address + "customers/calculate_balance/" + user_id + "/"+userType+"/tgs_appkey_amin", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("data");
                    credit = data;

                    user_credit.setText("RM" + data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: " + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



    }

    Dialog refund_dialog;
    private void OpenRefundDialog()
    {
        refund_dialog = new Dialog(FinanceScreen.this, R.style.Theme_Dialog);
        refund_dialog.setContentView(R.layout.refund_layout);

        final EditText amount_input = refund_dialog.findViewById(R.id.amount_input);
        final TextView tot_credit = refund_dialog.findViewById(R.id.refund_total_credit);
        final TextView driver_share_ = refund_dialog.findViewById(R.id.refund_driver_credit);

        tot_credit.setText(credit);
        driver_share_.setText(credit);
        TextView send_request = refund_dialog.findViewById(R.id.request_btn);


        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!amount_input.getText().toString().equalsIgnoreCase(""))
                    SendRefundRequest(amount_input.getText().toString());
            }
        });

        refund_dialog.show();
    }

    private void SendRefundRequest(String s) {
        refund_dialog.dismiss();

        asyncHttpClient.setTimeout(20*1000);

        RequestParams params = new RequestParams();

        String userType = "2";
        if (group_id.equalsIgnoreCase("1"))
            userType = "5";

        params.put("user_id",user_id);
        params.put("user_type",userType);
        params.put("key","tgs_appkey_amin");
        params.put("amount",s);

        Log.e(TAG, "SendRefundRequest: USER_ID "+user_id);
        Log.e(TAG, "SendRefundRequest: USER_AMOUNT "+s);
        Log.e(TAG, "SendRefundRequest: URL "+Constants.Host_Address + "customers/customer_withdraw_request" );

        asyncHttpClient.post(Constants.Host_Address + "customers/customer_withdraw_request", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Requesting withdrawl");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialog();
                    String res = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(res);
                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onSuccess: "+res);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialog();
                    String res = new String(responseBody);
                    Log.e(TAG, "onFailed: "+res);
                    Toast.makeText(getApplicationContext(),"Internet/Server Error",Toast.LENGTH_LONG).show();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    private ProgressDialog progressDialog = null;

    private void showDialog(String message){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideDialog(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

}

