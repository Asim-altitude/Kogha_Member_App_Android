package asim.tgs_member_app.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Observable;
import java.util.Observer;

import androidx.fragment.app.Fragment;
import asim.tgs_member_app.ChangePasswordActivity;
import asim.tgs_member_app.ForgotPasswordActivity;
import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.FragmentSettings;
import asim.tgs_member_app.models.JsonResponse;
import asim.tgs_member_app.restclient.RestServiceClient;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.CONSUMER_IR_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sohaib on 07/09/2017.
 */


public class ChangePasswordFragment extends Fragment {

   public View rootView;

   private EditText txtPassword;
   private EditText txtCPassword;
   private EditText txtOldpassword;
   private Button btnChangePassword;

   private String accessToken;
   private String customerId;
   private String userPassword;
   private String member_id;


   AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      rootView = inflater.inflate(R.layout.fragment_change_password, container, false);

      SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
      accessToken = settings.getString(Constants.PREFS_ACCESS_TOKEN, "");
      customerId = settings.getString(Constants.PREFS_CUSTOMER_ID, "");
      userPassword = settings.getString(Constants.PREFS_USER_PASSWORD, "");
      member_id = settings.getString(Constants.PREFS_USER_ID, "");

      Log.e("password", userPassword);

      try {
         userPassword = new FragmentSettings(getContext()).getUserPassword();
      } catch (Exception e) {
         Log.e("pass save ", " error " + e.getMessage());
      }
      txtPassword = (EditText) rootView.findViewById(R.id.txtPassword);
      txtCPassword = (EditText) rootView.findViewById(R.id.txtCPassword);
      txtOldpassword = (EditText) rootView.findViewById(R.id.txt_oldPass);

      btnChangePassword = (Button) rootView.findViewById(R.id.btnChangePassword);
      btnChangePassword.setOnClickListener(updatepasswordListner);

      return rootView;
   }


   private View.OnClickListener updatepasswordListner = new View.OnClickListener() {
      @Override
      public void onClick(View v) {

         if (!txtOldpassword.getText().toString().equals("") &&
                 !txtCPassword.getText().toString().equals("") &&
                 !txtPassword.getText().toString().equals("")) {

            if (txtOldpassword.getText().toString().trim().equals(userPassword))
            {
               if (txtPassword.getText().toString().equals(txtCPassword.getText().toString()))
               {
                  String old_pass = txtOldpassword.getText().toString();
                  String new_pass = txtPassword.getText().toString();
                  changePasswordApi(old_pass,new_pass);
               }
               else
               {
                  txtPassword.setError("password do not match");
                  txtCPassword.setError("password do not match");
               }
            }
            else
            {
               txtOldpassword.setError("Incorrect password");
            }
         }
         else
         {
            if (txtOldpassword.getText().toString().equals(""))
                 txtOldpassword.setError("Field required");

            if (txtPassword.getText().toString().equals(""))
               txtPassword.setError("Field required");

            if (txtCPassword.getText().toString().equals(""))
               txtCPassword.setError("Field required");


         }
      }
   };

   private void showDialog() {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setMessage("Please wait...");
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(true);
      progressDialog.show();
   }

   private void hideDialog() {
      if (progressDialog != null)
         progressDialog.dismiss();
   }


   AsyncHttpClient httpClient = new AsyncHttpClient();
   ProgressDialog progressDialog;
   private void changePasswordApi(String old,String new_pass) {

      RequestParams params = new RequestParams();
      params.put("password_1", new_pass);
      params.put("password_2", new_pass);
      params.put("member_id", member_id);
      params.put("key", accessToken);

      httpClient.setConnectTimeout(40000);
      httpClient.post(getContext(), Constants.Host_Address + "members/change", params, new AsyncHttpResponseHandler() {

         @Override
         public void onStart() {
            super.onStart();
            progressDialog = new ProgressDialog(getContext());
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
                  UtilsManager.showAlertMessage(getContext(), "", "Password Updated Successfully");
                 // Constants.logOutUser(getContext());
               }
               else
                  UtilsManager.showAlertMessage(getContext(), "", object.getString("message"));

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

}
