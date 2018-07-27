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
 * Created by sohaib on 7/31/17.
 *
 */


public class ChangePasswordActivity extends AppCompatActivity implements Observer, View.OnClickListener {

   private static final String TAG = "ChangePasswordActivity";

   // Views
   private EditText editTextCode;


   private EditText txtPassword;
   private EditText txtCPassword;
   @SuppressWarnings("FieldCanBeLocal")
   private Button btnValidate;
   @SuppressWarnings("FieldCanBeLocal")
   private TextView btnResendCode;
   private ProgressDialog progressDialog;
   @SuppressWarnings("FieldCanBeLocal")
   private JsonResponse jsonResponse;
   private int actionChange;
   private String email;


   RestServiceClient restServiceClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_change_password);

      editTextCode = (EditText) findViewById(R.id.editConfirmCode);
      txtPassword = (EditText) findViewById(R.id.txtPassword);
      txtCPassword = (EditText) findViewById(R.id.txtCPassword);
      btnValidate = (Button) findViewById(R.id.btnValidate);
      btnResendCode = (TextView) findViewById(R.id.btnResendCode);
      btnValidate.setOnClickListener(this);
      btnResendCode.setOnClickListener(this);
      email = getIntent().getStringExtra("email");
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
      progressDialog = new ProgressDialog(ChangePasswordActivity.this);
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
            txtPassword.setError(null);
            txtCPassword.setError(null);

            boolean cancel = false;
            View focusView = null;

            if (editTextCode.getText().length() <= 0) {
               editTextCode.setError("Please Enter Authentication Code");
               focusView = editTextCode;
               cancel = true;
            } else if (txtPassword.getText().length() <= 0) {
               txtPassword.setError("Please Choose Your Password");
               focusView = txtPassword;
               cancel = true;
            } else if (txtCPassword.getText().length() <= 0) {
               txtCPassword.setError("Please Re-Enter Your Password");
               focusView = txtCPassword;
               cancel = true;
            } else if (!txtPassword.getText().toString().equals(txtCPassword.getText().toString())) {
               txtCPassword.setError("Passwords do not match");
               focusView = txtCPassword;
               cancel = true;
            }

            if(cancel) {
               focusView.requestFocus();
            } else {
               showDialog();
               validateUser(email);
            }

            break;

         case R.id.btnResendCode:

            showDialog();
            resendValidationCode(email);

            break;
      }
   }

   /**
    * API call to validate user
    * @param email Received email
    */
   AsyncHttpClient httpClient = new AsyncHttpClient();
   private void validateUser(String email) {


      RequestParams map = new RequestParams();
      map.put("email", email);
      map.put("code", editTextCode.getText().toString());
      map.put("new_password", txtPassword.getText().toString());
     // restServiceClient.callService(this, Constants.Host_Address + "update_password", JsonResponse.class, "POST", map, true);

      httpClient.setConnectTimeout(40000);
      httpClient.post(ChangePasswordActivity.this, Constants.Host_Address + "members/update_password", map, new AsyncHttpResponseHandler() {

         @Override
         public void onStart() {
            super.onStart();
            progressDialog = new ProgressDialog(ChangePasswordActivity.this);
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
                  UtilsManager.showAlertMessage(ChangePasswordActivity.this,"",object.getString("message"));
                  finish();
               }
               else
               {
                  UtilsManager.showAlertMessage(ChangePasswordActivity.this,"",object.getString("message"));
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
                     Intent intent = new Intent(this, LoginActivity.class);
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
