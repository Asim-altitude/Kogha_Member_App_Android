package asim.tgs_member_app.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkb.vcedittext.VerificationAction;
import com.jkb.vcedittext.VerificationCodeEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import cz.msebera.android.httpclient.Header;

public class SignUp_Three extends AppCompatActivity {
    private static final String TAG = "SignUp_Three";

    VerificationCodeEditText verificationCodeEditText;
    LinearLayout next_btn;
    ImageView imageView;
    private void setupback(){
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    String code = "0000";
    TextView resend_otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__three);
        setupback();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);

        code = sharedPreferences.getString("code","-1-1-2-2");
        Log.e(TAG, "CODE "+code);
        next_btn = findViewById(R.id.next_btn);
        verificationCodeEditText = findViewById(R.id.verification_code_input);
        verificationCodeEditText.setFigures(code.length());
        resend_otp = findViewById(R.id.resend_btn);
        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationCodeEditText.setText("");
                resendOTP();
            }
        });
        imageView = findViewById(R.id.done_image);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificationCodeEditText.getText().toString().length() >= 4 && verificationCodeEditText.getText().toString().equalsIgnoreCase(code)) {
                    startActivity(new Intent(SignUp_Three.this, SignUp_Four.class));
                    finish();
                }
                else
                    findViewById(R.id.contact_message).setVisibility(View.VISIBLE);

            }
        });

        verificationCodeEditText.setFigures(4);
        verificationCodeEditText.setOnVerificationCodeChangedListener(new VerificationAction.OnVerificationCodeChangedListener() {
            @Override
            public void onVerCodeChanged(CharSequence s, int start, int before, int count) {
                next_btn.setBackgroundResource(R.drawable.disabled_btn);
                if (s.toString().length()<4)
                   findViewById(R.id.code_message).setVisibility(View.GONE);

            }

            @Override
            public void onInputCompleted(CharSequence s) {
               if (true)
                   verifyOTP();
               else {
                   findViewById(R.id.code_message).setVisibility(View.VISIBLE);
               }
            }
        });


        sharedPreferences.edit().putBoolean(Constants.IS_IN_REGISTRATION,true).apply();
        sharedPreferences.edit().putInt(Constants.REGISTRATION_STEP,-1).apply();

    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    SharedPreferences sharedPreferences;
    private void verifyOTP() {
        asyncHttpClient.setConnectTimeout(20000);
        asyncHttpClient.setTimeout(20*1000);

        RequestParams params = new RequestParams();
        try {
            params.put("otp", verificationCodeEditText.getText().toString());

        }
        catch (Exception e){
            e.printStackTrace();
        }

        asyncHttpClient.post(SignUp_Three.this, Constants.Host_Address + "check_otp_member", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("Verifying...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);

                    int errorcode = jsonObject.getInt("errorCode");
                    if (errorcode==00){
                        sharedPreferences.edit().putBoolean(Constants.IS_IN_REGISTRATION,true).apply();
                        String message = jsonObject.getString("message");
                        Toast.makeText(SignUp_Three.this,message,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp_Three.this,SignUp_Five.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        finishAffinity();
                    }else {
                        String message = jsonObject.getString("message");
                        Toast.makeText(SignUp_Three.this,message,Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "onFailure: "+jsonObject);
                    String message = jsonObject.getString("message");
                    Toast.makeText(SignUp_Three.this,message,Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();

                    Toast.makeText(SignUp_Three.this,"Server Error",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void resendOTP() {
        asyncHttpClient.setConnectTimeout(20000);
        asyncHttpClient.setTimeout(20*1000);
        RequestParams params = new RequestParams();
        try {
            params.put("email", sharedPreferences.getString(Constants.PREFS_USER_EMAIL,""));

        }
        catch (Exception e){
            e.printStackTrace();
        }

        asyncHttpClient.post(SignUp_Three.this, Constants.Host_Address + "resend_otp_member", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("Sending OTP");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);

                    int errorcode = jsonObject.getInt("errorCode");
                    if (errorcode==00){
                        String message = jsonObject.getString("message");
                        Toast.makeText(SignUp_Three.this,message,Toast.LENGTH_SHORT).show();
                        code = jsonObject.getString("otp_code");

                    }else {
                        String message = jsonObject.getString("message");
                        Toast.makeText(SignUp_Three.this,message,Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "onFailure: "+jsonObject);
                    String message = jsonObject.getString("message");
                    Toast.makeText(SignUp_Three.this,message,Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();

                    Toast.makeText(SignUp_Three.this,"Server Error",Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        sharedPreferences.edit().putBoolean(Constants.IS_IN_REGISTRATION,false).apply();

        startActivity(new Intent(SignUp_Three.this,SignUp_One.class));
        finish();
    }

    private ProgressDialog progress;
    private void showProgressDialog(String message)
    {

        progress = new ProgressDialog(SignUp_Three.this);
        progress.setMessage(message);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }
}
