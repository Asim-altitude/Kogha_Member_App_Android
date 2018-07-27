package asim.tgs_member_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.ErrorCodes;
import asim.tgs_member_app.models.JsonResponse;
import asim.tgs_member_app.restclient.BaseModel;
import asim.tgs_member_app.restclient.ErrorModel;
import asim.tgs_member_app.restclient.RestServiceClient;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by sohaib on 7/31/17.
 *
 */

public class ForgotPasswordActivity extends AppCompatActivity implements Observer, View.OnClickListener {

   private static final String TAG = "ForgotPasswordActivity";

   // Views
   private EditText editTextEmail;
   @SuppressWarnings("FieldCanBeLocal")
   private Button btnSubmit;
   @SuppressWarnings("FieldCanBeLocal")
   private JsonResponse jsonResponse;

   private ImageView back_navigation;

   RestServiceClient restServiceClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_forgot_password);

      editTextEmail = (EditText) findViewById(R.id.et_email);
      btnSubmit = (Button) findViewById(R.id.btnSubmit);
      back_navigation=(ImageView) findViewById(R.id.back_navigation);
      back_navigation.setOnClickListener(this);
      btnSubmit.setOnClickListener(this);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            finish();
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   private void showDialog(){
      progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
      progressDialog.setMessage("Please wait...");
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(true);
      progressDialog.show();
   }

   private boolean isEmailValid(String email) {
      Pattern pattern = Patterns.EMAIL_ADDRESS;
      return pattern.matcher(email).matches();
   }

   @Override
   public void onClick(View view) {

      switch (view.getId()){

         case R.id.btnSubmit:

            editTextEmail.setError(null);
            boolean cancel = false;
            View focusView = null;

            // Check for a valid email address.
            if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
               editTextEmail.setError(getString(R.string.error_field_required));
               focusView = editTextEmail;
               cancel = true;
            }

            if (!isEmailValid(editTextEmail.getText().toString())) {
               editTextEmail.setError(getString(R.string.error_invalid_email));
               focusView = editTextEmail;
               cancel = true;
            }

            if(cancel) {
               focusView.requestFocus();
            } else {
               showDialog();
               sendCode();
            }

            break;

         case R.id.back_navigation:

            finish();

            break;
      }
   }

   AsyncHttpClient httpClient = new AsyncHttpClient();
    ProgressDialog progressDialog;
   private void sendCode() {

        RequestParams params = new RequestParams();
        params.put("email", editTextEmail.getText().toString());

       httpClient.setConnectTimeout(40000);
       httpClient.post(ForgotPasswordActivity.this, Constants.Host_Address + "members/forgot", params, new AsyncHttpResponseHandler() {

           @Override
           public void onStart() {
               super.onStart();
               progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
               progressDialog.setCanceledOnTouchOutside(false);
               progressDialog.setMessage("Please Wait...");
               progressDialog.show();
           }

           @Override
           public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               try
               {
                   if (progressDialog!=null)
                       progressDialog.dismiss();

                   String s = new String(responseBody);
                   Log.e("response",s);
                   JSONObject object = new JSONObject(s);
                   if (object.getString("status").equals("success")) {
                       UtilsManager.showAlertMessage(ForgotPasswordActivity.this,"",object.getString("message"));
                       startActivity(new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class).putExtra("email",editTextEmail.getText().toString()));
                       finish();
                   }
                   else
                   {
                       UtilsManager.showAlertMessage(ForgotPasswordActivity.this,"",object.getString("message"));
                   }

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
                   if (progressDialog!=null)
                       progressDialog.dismiss();

                   String s = new String(responseBody);
                   Log.e("response failed ",s);


               }
               catch (Exception e)
               {
                   e.printStackTrace();
               }
           }
       });

   }

   /**
    * Update UI
    * @param data
    * Receive Response from server and cast into actual model.
    */
   @Override
   public void update(Observable observable, Object data) {

      BaseModel baseModel = ((BaseModel) data);
      if(baseModel instanceof ErrorModel){
         progressDialog.dismiss();
         UtilsManager.showAlertMessage(this, "", ((ErrorModel) baseModel).getException());
         Log.d(TAG, ((ErrorModel) baseModel).getException());
      }else {

         if(baseModel instanceof JsonResponse){

            progressDialog.dismiss();

            jsonResponse = ((JsonResponse) baseModel);

            switch (jsonResponse.getErrorCode()){

               case ErrorCodes.NO_ERROR:

                  Toast.makeText(this, jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(this, ChangePasswordActivity.class).putExtra("email",editTextEmail.getText().toString());
                  startActivity(intent);
                  finish();
                  break;

               default:

                  UtilsManager.showAlertMessage(this, "", jsonResponse.getMessage());
                  break;
            }
         }
      }
   }
}
