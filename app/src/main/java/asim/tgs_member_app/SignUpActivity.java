package asim.tgs_member_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class SignUpActivity extends AppCompatActivity implements Observer, View.OnClickListener {

   public static final String TAG = "SignUpActivity";

   // Views
   @SuppressWarnings("FieldCanBeLocal")
   private TextView btnAlreadyAccount;
   private EditText txtName;
   private EditText txtEmail;
   private EditText txtPhone;
   private EditText txtPassword;
   private EditText txtCPassword;
   @SuppressWarnings("FieldCanBeLocal")
   private Button btnNext;
   private ProgressDialog progressDialog;

   @SuppressWarnings("FieldCanBeLocal")
   private JsonResponse registerResponseModel;
   private ImageView back_navigation;

   RestServiceClient restServiceClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_sign_up);

      //setupToolbar();

      btnAlreadyAccount = (TextView) findViewById(R.id.txtAlreadyAccount);
      txtName = (EditText) findViewById(R.id.txtFullName);
      txtEmail = (EditText) findViewById(R.id.txtEmailAddress);
      txtPhone = (EditText) findViewById(R.id.txtPhone);
      txtPassword = (EditText) findViewById(R.id.txtPassword);
      txtCPassword = (EditText) findViewById(R.id.txtCPassword);
      btnNext =(Button) findViewById(R.id.btnNext);
      back_navigation=(ImageView) findViewById(R.id.back_navigation);
      back_navigation.setOnClickListener(this);

      btnAlreadyAccount.setOnClickListener(this);
      btnNext.setOnClickListener(this);
   }

   /**
    * Render the toolbar
    */
   private void setupToolbar(){
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      // Show menu icon
      final ActionBar ab = getSupportActionBar();
      assert ab != null;
      ab.setDisplayHomeAsUpEnabled(true);
      ab.setTitle("Sign Up");
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

   void showDialog(String title, String message){

      progressDialog = new ProgressDialog(SignUpActivity.this);
      progressDialog.setTitle(title);
      progressDialog.setMessage(message);
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(true);
      progressDialog.show();
   }

   /**
    * Email Validation
    * @param email Received email
    * @return false or true
    */
   private boolean isValidEmail(String email) {
      Pattern pattern = Patterns.EMAIL_ADDRESS;
      return pattern.matcher(email).matches();
   }

   // Click listeners
   @Override
   public void onClick(View v) {

      switch (v.getId()) {

         case  R.id.txtAlreadyAccount:
            finish();
            break;

         case  R.id.btnNext:

            String Email = txtEmail.getText().toString().replace(" ","").replace("\n","").replace("\t","").toLowerCase();

            // Reset errors.
            txtName.setError(null);
            txtEmail.setError(null);
            txtPhone.setError(null);
            txtPassword.setError(null);
            txtCPassword.setError(null);

            boolean cancel = false;
            View focusView = null;

            if (txtName.getText().length() <= 0) {
               txtName.setError("Please Enter Your Name");
               focusView = txtName;
               cancel = true;
               //UtilsManager.showAlertMessage(this, "Warning", "Please Enter a Name");
            } else if(!isValidEmail(Email)) {
               txtEmail.setError("Email incorrect, Please enter valid email address.");
               focusView = txtEmail;
               cancel = true;
               //UtilsManager.showAlertMessage(this, "Warning!", "Email incorrect, Please enter valid email address.");
            } else if (txtPhone.getText().length() <= 0) {
               txtPhone.setError("Please Enter Your Mobile Number");
               focusView = txtPhone;
               cancel = true;
               //UtilsManager.showAlertMessage(this, "Warning", "Please Enter a Name");
            } else if (txtPassword.getText().length() <= 0) {
               txtPassword.setError("Please Choose Your Password");
               focusView = txtPassword;
               cancel = true;
               //UtilsManager.showAlertMessage(this, "Warning", "Please Choose a Password");
            } else if (txtCPassword.getText().length() <= 0) {
               txtCPassword.setError("Please Confirm Your Password");
               focusView = txtCPassword;
               cancel = true;
               //UtilsManager.showAlertMessage(this, "Warning", "Please Confirm Your Password");
            } else if (!txtPassword.getText().toString().equals(txtCPassword.getText().toString())) {
               txtCPassword.setError("Passwords do not match");
               focusView = txtCPassword;
               cancel = true;
               //UtilsManager.showAlertMessage(this, "Warning", "Passwords do not match");
            }

            if(cancel) {
               focusView.requestFocus();
            } else {
              // showDialog("", "Sending verification code");
               Intent intent = new Intent(getApplicationContext(),Select_Services_Activity.class);
               startActivity(intent);
               finish();
             //  RequestConfirmationCode();
              // signUpUser();
            }
            break;

         case R.id.back_navigation:

            finish();

            break;
      }
   }

   /**
    * API Call to request confirmation code
    */
   public  void RequestConfirmationCode() {

      MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add("email", txtEmail.getText().toString());
      map.add("name", txtName.getText().toString());
      map.add("phone", txtPhone.getText().toString());
      map.add("password", txtPassword.getText().toString());

      restServiceClient.callService(this, Constants.Host_Address + "register", JsonResponse.class, "POST", map, true);
   }



   AsyncHttpClient httpClient = new AsyncHttpClient();

   private void signUpUser()
   {
      httpClient.setConnectTimeout(30000);
      RequestParams params = new RequestParams();
      params.put("email", txtEmail.getText().toString());
      params.put("name", txtName.getText().toString());
      params.put("phone", txtPhone.getText().toString());
      params.put("password", txtPassword.getText().toString());

      httpClient.post(SignUpActivity.this, Constants.Host_Address + "register", params, new AsyncHttpResponseHandler() {

         @Override
         public void onStart() {
            super.onStart();
            showDialog("Register","please wait...");
         }

         @Override
         public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            try {
               progressDialog.dismiss();
               String response = new String(responseBody);
               Log.e("response",response);
               JSONObject object = new JSONObject(response);
               SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
               SharedPreferences.Editor editor = settings.edit();
               String member_id = object.getString("member_id");
               editor.putString(Constants.PREFS_CUSTOMER_ID,member_id);
               editor.apply();

               startActivity(new Intent(SignUpActivity.this,VerificationActivity.class)
                       .putExtra("email", txtEmail.getText().toString()).putExtra("member_id",member_id));
               finish();

            }
            catch (Exception e)
            {
               e.printStackTrace();
               UtilsManager.showAlertMessage(SignUpActivity.this,"","Email is already in use.");
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

   /**
    * Update UI
    * @param data
    * Receive Response from server and cast into actual model.
    */
   @Override
   public void update(Observable observable, Object data) {

      BaseModel baseModel = ((BaseModel) data);
      if (baseModel instanceof ErrorModel){
         progressDialog.dismiss();
         UtilsManager.showAlertMessage(this, "Sorry", "Something went wrong! please try again");
         Log.d(TAG, ((ErrorModel) baseModel).getException());
      }else {

         if(baseModel instanceof JsonResponse) {
            progressDialog.dismiss();
            registerResponseModel = ((JsonResponse) baseModel);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            switch (registerResponseModel.getErrorCode()){

               case ErrorCodes.NO_ERROR:

                  Intent verificationActivity = new Intent(this, VerificationActivity.class);
                  verificationActivity.putExtra("email", txtEmail.getText().toString());
                  startActivity(verificationActivity);

                break;

               default:

                  builder.setMessage(registerResponseModel.getMessage());
                  builder.setNegativeButton("OK", null);
                  builder.show();

                  break;
            }
         }
      }
   }
}
