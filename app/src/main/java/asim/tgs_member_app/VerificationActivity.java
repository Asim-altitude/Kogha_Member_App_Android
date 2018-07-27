package asim.tgs_member_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Observable;
import java.util.Observer;

import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.ErrorCodes;
import asim.tgs_member_app.models.JsonResponse;
import asim.tgs_member_app.restclient.BaseModel;
import asim.tgs_member_app.restclient.ErrorModel;
import asim.tgs_member_app.restclient.RestServiceClient;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by sohaib on 7/24/17.
 *
 */


public class VerificationActivity extends AppCompatActivity implements Observer, View.OnClickListener {

   private static final String TAG = "VerificationActivity";

   // Views
   private EditText editTextCode;
   @SuppressWarnings("FieldCanBeLocal")
   private Button btnValidate;
   @SuppressWarnings("FieldCanBeLocal")
   private TextView btnResendCode;
   private ProgressDialog progressDialog;
   @SuppressWarnings("FieldCanBeLocal")
   private JsonResponse jsonResponse;
   private int actionChange;
   private String email;

   private ImageView back_navigation;


   RestServiceClient restServiceClient;
   private String member_id;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_verification);

      editTextCode = (EditText) findViewById(R.id.editConfirmCode);
      btnValidate = (Button) findViewById(R.id.btnValidate);
      btnResendCode = (TextView) findViewById(R.id.btnResendCode);
      back_navigation=(ImageView) findViewById(R.id.back_navigation);
      back_navigation.setOnClickListener(this);
      btnValidate.setOnClickListener(this);
      btnResendCode.setOnClickListener(this);
      email = getIntent().getStringExtra("email");
      member_id = getIntent().getStringExtra("member_id");
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
      progressDialog = new ProgressDialog(VerificationActivity.this);
      progressDialog.setMessage("Please wait...");
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(true);
      progressDialog.show();
   }

   @Override
   public void onClick(View view) {

      switch (view.getId()){

         case R.id.btnValidate:

            editTextCode.setError(null);
            boolean cancel = false;
            View focusView = null;

            if (editTextCode.getText().length() <= 0) {
               editTextCode.setError("Please enter authentication code.");
               focusView = editTextCode;
               cancel = true;
            }

            if(cancel) {
               focusView.requestFocus();
            } else {
               showDialog();
               verifyCode();
            }

            break;

         case R.id.btnResendCode:

            showDialog();
            resendValidationCode(email);

            break;

         case R.id.back_navigation:

          finish();

            break;

      }
   }

   /**
    * API call to validate user
    * @param email Received email
    */

   AsyncHttpClient httpClient = new AsyncHttpClient();

   private void verifyCode()
   {
      httpClient.setConnectTimeout(30000);
      RequestParams params = new RequestParams();
      params.put("email", email);
      params.put("code", editTextCode.getText().toString());

      httpClient.post(VerificationActivity.this, Constants.Host_Address + "verify_registration_code", params, new AsyncHttpResponseHandler() {

         @Override
         public void onStart() {
            super.onStart();
            showDialog();
         }

         @Override
         public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            try {
               progressDialog.dismiss();
               String response = new String(responseBody);
               JSONObject responseObj = new JSONObject(response);
               Log.e("response",response);
                  if (responseObj.getString("message").equalsIgnoreCase("Registration completed"))
                  {
                     startActivity(new Intent(VerificationActivity.this,Select_Services_Activity.class).putExtra("member_id",member_id));
                     finish();
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
               progressDialog.dismiss();
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

   private void validateUser(String email) {

      actionChange = 2;

      MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add("email", email);
      map.add("code", editTextCode.getText().toString());
      restServiceClient.callService(this, Constants.Host_Address + "verify_registration_code", JsonResponse.class, "POST", map, true);
   }
   /**
    * API call to resend validation code
    * @param email Received email
    */
   private void resendValidationCode(String email){

      actionChange = 1;

      MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add("email", email);
      restServiceClient.callService(this, Constants.Host_Address + "resend", JsonResponse.class, "POST", map, true);
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

               if (actionChange == 1) {

                  UtilsManager.showAlertMessage(this, "", jsonResponse.getMessage());

               } else if (actionChange == 2) {

                  Toast.makeText(this, jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                  ActivityCompat.finishAffinity(this);
                  Intent intent = new Intent(this, Select_Services_Activity.class);
                  startActivity(intent);
               }
                  break;

               default:

                  UtilsManager.showAlertMessage(this, "", jsonResponse.getMessage());
                  break;
            }
         }
      }
   }
}
