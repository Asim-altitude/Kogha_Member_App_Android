package asim.tgs_member_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import asim.tgs_member_app.models.Constants;
import cz.msebera.android.httpclient.Header;


public class SetupPassword extends AppCompatActivity {
    private static final String TAG = "SetupPassword";

    EditText password,confirm_password;
    LinearLayout save_pass;
    String member_id="117";
    ImageView back_navigation;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_password);

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        member_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");
        Log.e(TAG, "onCreate: "+member_id);

        back_navigation = findViewById(R.id.back_btn);

        back_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_pass);

        save_pass = findViewById(R.id.save_pass);
        save_pass.setOnClickListener(save_password_listner);

    }

    View.OnClickListener save_password_listner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String pass = password.getText().toString().trim();
            String c_pass = confirm_password.getText().toString().trim();

            boolean confirmed = false;
            if (pass.isEmpty()) {
                password.setError("enter password");
            }


            if (c_pass.isEmpty())
            {
                confirm_password.setError("enter confirm password");
            }


            if (!pass.isEmpty() && !c_pass.isEmpty())
            {
                setupPassword(pass);
            }

        }
    };




    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void setupPassword(String password_)
    {
        asyncHttpClient.setConnectTimeout(20000);

        RequestParams params = new RequestParams();
        params.put("member_id",member_id);
        params.put("password",password_);
        params.put("key","tgs_appkey_amin");

        asyncHttpClient.post(SetupPassword.this, Constants.Host_Address + "members/set_member_account_password", params, new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e("response",response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                        Toast.makeText(SetupPassword.this, jsonObject.getString("message"),
                                Toast.LENGTH_SHORT).show();
                        //Toast.makeText(SetupPassword.this,"Your can login now.",Toast.LENGTH_LONG).show();
                        sharedPreferences.edit().putBoolean(Constants.IS_IN_REGISTRATION,false).apply();
                        sharedPreferences.edit().putInt(Constants.REGISTRATION_STEP,0).apply();
                        sharedPreferences.edit().clear().apply();
                        startActivity(new Intent(SetupPassword.this, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                    else {
                        Toast.makeText(SetupPassword.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e("response",response);
                    Toast.makeText(SetupPassword.this, "Server Error ", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }


    private ProgressDialog progress;
    private void showProgressDialog()
    {

        progress = new ProgressDialog(SetupPassword.this);
        progress.setMessage("Creating password");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: ");
    }
}
