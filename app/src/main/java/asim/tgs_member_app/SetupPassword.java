package asim.tgs_member_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import asim.tgs_member_app.models.Constants;
import cz.msebera.android.httpclient.Header;


public class SetupPassword extends AppCompatActivity {

    EditText password,confirm_password;
    Button save_pass;
    String member_id="117";
    ImageView back_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_password);

        member_id = getIntent().getStringExtra(Constants.PREFS_USER_ID);

        back_navigation = findViewById(R.id.back_navigation);

        back_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.e("response",response);
                    Toast.makeText(SetupPassword.this,"Password created.",Toast.LENGTH_SHORT).show();
                    Toast.makeText(SetupPassword.this,"Your can login now.",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SetupPassword.this, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
                    String response = new String(responseBody);
                    Log.e("response",response);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }
}
