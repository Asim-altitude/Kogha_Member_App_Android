package asim.tgs_member_app.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

import asim.tgs_member_app.R;
import asim.tgs_member_app.adapters.ServicesAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.FragmentSettings;
import asim.tgs_member_app.models.MOCustomerProfile;
import asim.tgs_member_app.restclient.MyPrefrences;
import asim.tgs_member_app.restclient.RestServiceClient;
import asim.tgs_member_app.utils.ImageCompressClass;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sohaib on 07/09/2017.
 */


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    public View rootView;

    private TextView txtName;
    private EditText txtFullName;
    private EditText txtEmailAddress;
    private EditText txtPhone;
    @SuppressWarnings("FieldCanBeLocal")
    private MOCustomerProfile jsonResponse;
    @SuppressWarnings("FieldCanBeLocal")
    private Button btnUpdate;
    private ImageView image_userProfilePic;

    private String accessToken;
    private String customerId;
    private boolean enable = false;



    private String userPassword="";

    SharedPreferences.Editor editor;
    private static int RESULT_LOAD_IMG = 1;

    RestServiceClient restServiceClient;
    private String profileImageString;
    private MyPrefrences prefs;
    private static String OLD_IMAGE="old_image";
    private boolean isImageChosen = false;

    private ListView services_list;
    private ServicesAdapter serviceadapter;
    private String mem_id,passport,city,address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        services_list = (ListView) rootView.findViewById(R.id.services_list);
        services = new ArrayList<>();
        serviceadapter = new ServicesAdapter(getContext(),services);
        services_list.setAdapter(serviceadapter);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        prefs = new MyPrefrences(getContext());

        settings = getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();

        try
        {
            userPassword =  new FragmentSettings(getContext()).getUserPassword();
        }
        catch (Exception e)
        {
            Log.e("pass save "," error "+e.getMessage());
        }

        txtName = (TextView) rootView.findViewById(R.id.txtName);
        txtFullName = (EditText) rootView.findViewById(R.id.txtFullName);
        txtEmailAddress = (EditText) rootView.findViewById(R.id.txtEmailAddress);
        txtPhone = (EditText) rootView.findViewById(R.id.txtPhone);
        btnUpdate = (Button) rootView.findViewById(R.id.btnUpdate);
        image_userProfilePic = (ImageView) rootView.findViewById(R.id.userProfilePic);
        rootView.findViewById(R.id.btnPlus).setOnClickListener(this);
        image_userProfilePic.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        accessToken = settings.getString(Constants.PREFS_ACCESS_TOKEN, "");
        customerId = settings.getString(Constants.PREFS_USER_ID, "");
        profileImageString = settings.getString(Constants.PREFS_USER_IMAGE, null);


        if (!(profileImageString.isEmpty() && profileImageString.equalsIgnoreCase(null))) {

            Picasso.with(getActivity())
                    .load(profileImageString)
                    .into(image_userProfilePic);

            Log.e("pictureUrl", profileImageString);
        }

        mem_id = settings.getString(Constants.PREFS_USER_ID,"");
        passport = settings.getString(Constants.PREFS_USER_PASSWORD,"");
        address = settings.getString(Constants.PREFS_USER_ADDRESS,"");
        city = "not available";//settings.getString(Constants.PREFS_CUSTOMER_ID,"");

        txtName.setText(settings.getString(Constants.PREFS_USER_NAME, ""));
        txtFullName.setText(settings.getString(Constants.PREFS_USER_NAME, ""));
        txtEmailAddress.setText(settings.getString(Constants.PREFS_USER_EMAIL, ""));
        txtPhone.setText(settings.getString(Constants.PREFS_USER_MOBILE, ""));


      /*  txtFullName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (txtFullName.getRight() - txtFullName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                       // txtFullName.setFocusableInTouchMode(true);
                       // enable = true;
                      //  showKeyBoard();
                        txtFullName.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        txtEmailAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (txtEmailAddress.getRight() - txtEmailAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        txtEmailAddress.setFocusableInTouchMode(true);
                        enable = true;
                        showKeyBoard();
                        return true;
                    }
                }
                return false;
            }
        });

        txtPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (txtPhone.getRight() - txtPhone.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        txtPhone.setFocusableInTouchMode(true);
                        enable = true;
                        showKeyBoard();
                        return true;
                    }
                }
                return false;
            }
        });*/



        return rootView;
    }

    private static final int REQ_CODE = 0011;
    public void galleryIntent() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_CODE);
        }
        else
        {
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try
        {
            if (requestCode == REQ_CODE)
            {
                if (grantResults[0]>0)
                {
                    galleryIntent();
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = null;
                if (resultCode == RESULT_OK)
                    resultUri = result.getUri();

                Bitmap bitmap = ImageCompressClass.getThumbnail(getContext(),resultUri);
                String destinationUri = Environment.getExternalStorageDirectory() + File.separator + "kogha_profile.jpeg";
                ImageCompressClass.SaveBitmap(bitmap,destinationUri);
                profileImageString = destinationUri;

                image_userProfilePic.setImageBitmap(bitmap);
                isImageChosen =true;
                prefs.setValue(OLD_IMAGE,profileImageString);
            }
            else if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                prefs.setValue(OLD_IMAGE,profileImageString);
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                profileImageString = cursor.getString(columnIndex);
                Uri res_url=Uri.fromFile(new File((profileImageString)));
                /*UCrop.of(res_url,Uri.fromFile(new File(destinationUri)))
                        .withAspectRatio(16, 9)
                        .withMaxResultSize(200, 200)
                        .start(getActivity());*/
                CropImage.activity(res_url)
                        .start(getContext(), this);

              /*  Log.e("profile image",profileImageString);
                String file_profile = ImageCompressClass.compressImage(profileImageString);
                Log.e("profile image",file_profile);
                profileImageString = file_profile;
                cursor.close();
                Bitmap _bitmap = BitmapFactory.decodeFile(file_profile);
                image_userProfilePic.setImageBitmap(_bitmap);*/
                isImageChosen =true;


            } else {
               /* Toast.makeText(RegisterUser.this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();*/
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    private Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    AsyncHttpClient httpClient = new AsyncHttpClient();
    ProgressDialog progressDialog;
    SharedPreferences settings;
    String key;
    private void GetMemberServices()
    {
        httpClient.setConnectTimeout(40000);

        settings = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        String member_id = settings.getString(Constants.PREFS_USER_ID,"0");
        key ="tgs_appkey_amin";// settings.getString(Constants.PREFS_ACCESS_TOKEN,"tgs_appkey_amin");
        Log.e("member id",member_id);
        mem_id = member_id;



        Log.e("member services",  Constants.Host_Address + "members/my_services/"+member_id+"/"+key);
        httpClient.get(getContext(),  Constants.Host_Address + "members/my_services/"+member_id+"/"+key, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (progressDialog != null)
                        progressDialog.dismiss();

                    String responseData = new String(responseBody);
                    JSONObject response = new JSONObject(responseData);
                    Log.e("response ",responseData);

                    if (response.getJSONArray("data")!=null)
                        parseServicesResponse(response);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();

                    String responseData = new String(responseBody,"UTF-8");
                    Log.e("response failure",responseData);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    List<String> services;
    private void parseServicesResponse(JSONObject response) {

        try {
            JSONArray servicesList = response.getJSONArray("data");

            String service_name;
            if (services==null)
                services = new ArrayList<>();

            for (int i=0;i<servicesList.length();i++)
            {
                JSONObject object = servicesList.getJSONObject(i);
                service_name = object.getString("service_name");
                services.add(service_name);
            }
            serviceadapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(services_list);

        }
        catch (Exception e)
        {
            Log.e("exception ",e.getMessage());
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null)
        {
            int totalHeight = 0;
            int size = listAdapter.getCount();
            for (int i = 0; i < size; i++)
            {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            totalHeight = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight;
            listView.setLayoutParams(params);

        }

    }




   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get path of the selected bg image
        if (requestCode == 120 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            CropImage.activity(selectedImage)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri filePath = result.getUri();
                try {
                    //getting image from gallery
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    Bitmap bitmap2 = scaleDown(bitmap, 2000, true);
                    Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap2, 190);
                    byte[] b = stream.toByteArray();

                    profileImageString =  new String(b, "UTF-8");
                    Log.e("path",profileImageString);
                  //  profileImageString = Base64.encodeToString(b, Base64.DEFAULT);
                   // profileImageString =b.toString();
                    //Log.d("profileImageString",profileImageString);
                    enable = true;
                    //Setting image to ImageView
                    image_userProfilePic.setImageBitmap(circularBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
                Log.e("OnActivity", "Crop Error");
            }
        }
    }
*/
   /* public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());
        return Bitmap.createScaledBitmap(realImage, width, height, filter);
    }*/

    private void showDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void updateInformation() {

        Log.e("pic", profileImageString);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("customer_name", txtFullName.getText().toString());
        map.add("customer_email", txtEmailAddress.getText().toString());
        map.add("customer_mobile", txtPhone.getText().toString());
        map.add("profile_img", profileImageString);
        map.add("id", customerId);
        map.add("key", accessToken);
      //  restServiceClient.callService(this, Constants.Host_Address + "customers/update_profile", MOCustomerProfile.class, "POST", map, true);

    }

    public JSONObject getData(final String customer_name, final String customer_email, final String customer_mobile, final String profile_img,
                       final String id, final String key) {

        String url_val = Constants.Host_Address + "members/update_profile";


        int serverResponseCode = 0;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead_, bytesAvailable_, bufferSize_;

        byte[] buffer_;
        //FileInputStream fileInputStream  ;
        int maxBufferSize_ = 1 * 1024 * 1024;
        String photo_file = profile_img;

        File sourceFile_photo = new File(photo_file);
      /*  if (!sourceFile_photo.isFile()) {
            Log.e("uploadFile", "Source File Does not exist");
            return 0;
        }*/

        try {

            if (!isImageChosen)
            {
                SharedPreferences settings = getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                sourceFile_photo = new File(settings.getString(Constants.PREFS_PROFILE_IMG,""));

            }
            isImageChosen = false;
            if (sourceFile_photo!=null) {


                try {

                    URL url_ = new URL(url_val);
                    //InputStream url = new URL(upLoadServerUri).openStream();
                    conn = (HttpURLConnection) url_.openConnection(); // Open a HTTP
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", photo_file);
                    dos = new DataOutputStream(conn.getOutputStream());

                    FileInputStream fileInputStream_photo = new FileInputStream(sourceFile_photo);


                    //photo
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"profile_img\";filename=\""
                            + photo_file + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    bytesAvailable_ = fileInputStream_photo.available(); // create a buffer of
                    // maximum size

                    bufferSize_ = Math.min(bytesAvailable_, maxBufferSize_);
                    buffer_ = new byte[bufferSize_];

                    // read file and write it into form...
                    bytesRead_ = fileInputStream_photo.read(buffer_, 0, bufferSize_);

                    while (bytesRead_ > 0) {
                        dos.write(buffer_, 0, bufferSize_);
                        bytesAvailable_ = fileInputStream_photo.available();
                        bufferSize_ = Math.min(bytesAvailable_, maxBufferSize_);
                        bytesRead_ = fileInputStream_photo.read(buffer_, 0, bufferSize_);
                    }
                    fileInputStream_photo.close();
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    Log.e("reg_photo_file", photo_file);

                }
                catch (Exception e)
                {

                }

            }

            //name
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"member_id\"" + lineEnd);
            dos.writeBytes(lineEnd);
            Log.e("memberId", mem_id);

            // assign value
            dos.writeBytes(mem_id);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            //name
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"full_name\"" + lineEnd);
            dos.writeBytes(lineEnd);
            Log.e("member_name", customer_name);

            // assign value
            dos.writeBytes(customer_name);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //display name
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"display_name\"" + lineEnd);
            dos.writeBytes(lineEnd);
            Log.e("customer_name", customer_name);

            // assign value
            dos.writeBytes(customer_name);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);



          /*  //email
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"customer_email\"" + lineEnd);
            dos.writeBytes(lineEnd);
            Log.e("customer_email", customer_email);
            // assign value
            dos.writeBytes(customer_email);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

*/
            //mobile number
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"mobile_number\"" + lineEnd);
            dos.writeBytes(lineEnd);
            Log.e("member_mobile", customer_mobile);
            // assign value
            dos.writeBytes(customer_mobile);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

             SharedPreferences settings = getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
             String  old_photo = settings.getString(Constants.PREFS_PROFILE_IMG,"");

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"profile_img_old\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // assign value
            dos.writeBytes(old_photo);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);



            //passportt

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"ic_passport\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // assign value
            dos.writeBytes(passport);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //cityyyyy


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"city\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // assign value
            dos.writeBytes(city);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //aadresss

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"address\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // assign value
            dos.writeBytes(address);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            //cityyyyy
            //postcodeeee
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"postcode\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // assign value
            dos.writeBytes("00");
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //service_type
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"key\"" + lineEnd);
            dos.writeBytes(lineEnd);
            Log.e("key", key);
            // assign value
            dos.writeBytes("tgs_appkey_amin");
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
            Log.e("response", serverResponseMessage);
            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage
                    + ": " + serverResponseCode);
            String res = receiveResponse(conn);
            Log.e("login_response", res);
            if (res != null) {
                try {



                    JSONObject responseObject = new JSONObject(res);
                    String status = responseObject.getString("status");
                    String errorCode = responseObject.getString("errorCode");
                    String message = responseObject.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        JSONArray data = responseObject.getJSONArray("data");
                        JSONObject arrayObj = data.getJSONObject(0);

                        dos.flush();
                        dos.close();

                      return arrayObj;

                     //   UtilsManager.showAlertMessage(getContext(),"","Data Updated");

                    }
                    else{
                        progressDialog.dismiss();
                        UtilsManager.showAlertMessage(getActivity(), "", message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                Log.e("JSON Data", "JSON data error!");
            }


            // close the streams //
            // fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {

            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();

            Log.e("Upload file  Exception",
                    "Exception : " + e.getMessage(), e);
        }

        return null;


    }

    public String receiveResponse(HttpURLConnection conn)
            throws IOException {
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        // retrieve the response from server
        InputStream is = null;
        try {
            is = conn.getInputStream();
            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = is.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString();

        } catch (IOException e) {

            if (e.getMessage().indexOf("Connection reset by peer") > 0) {

            }

            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnPlus:
                galleryIntent();
                break;

            case R.id.btnUpdate:
                if (txtFullName.getText().toString().trim().isEmpty()) {


                    UtilsManager.showAlertMessage(getContext(), "", "Please Enter Your Name");


                } else if (txtEmailAddress.getText().toString().trim().isEmpty()) {
                    UtilsManager.showAlertMessage(getContext(), "", "Please Enter Your Email");

                } else if (txtPhone.getText().toString().trim().isEmpty()) {

                    UtilsManager.showAlertMessage(getContext(), "", "Please Enter Your Phone");
                } else {
                   // updateInformation();

                    // showDialog();
                    // getData(txtFullName.getText().toString(), txtEmailAddress.getText().toString(), txtPhone.getText().toString(), profileImageString, customerId, accessToken);
                    UpdateProfile updateTask = new UpdateProfile(txtFullName.getText().toString(), txtEmailAddress.getText().toString(), txtPhone.getText().toString(), profileImageString, customerId, accessToken,v);
                    updateTask.execute();

                    v.setEnabled(false);
                }
                break;

            default:
                break;
        }
    }


    private class UpdateProfile extends AsyncTask<JSONObject,Void,JSONObject>
    {
        String name;
        String email;
        String mobile;
        String selected_image;

        String customerId,accessToken;

        View update_btn;

        public UpdateProfile(String name,String email,String mobile,String selected_image,String cust_id,String accessToken,View v)
        {
            this.name =name;
            this.email= email;
            this.mobile = mobile;
            this.selected_image = selected_image;

            this.customerId = cust_id;
            this.accessToken = accessToken;

            this.update_btn=v;
        }


        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            try
            {
                return  getData(name,email,mobile,selected_image,this.customerId,this.accessToken);


            }
            catch (Exception e)
            {
                Log.e("error updating",e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            progressDialog.dismiss();
            update_btn.setEnabled(true);
            if (json!=null) {

                try {

                    String customer_name_value = json.getString("member_name");
                    String customer_mobile_value = json.getString("member_mobile");
                    String customer_email_value = json.getString("member_email");
                    String customer_full_img_value = json.getString("member_full_img");
                    String profile_img = json.getString("profile_img");

                    txtName.setText(customer_name_value);
                    txtFullName.setText(customer_name_value);
                    txtPhone.setText(customer_mobile_value);
                    txtEmailAddress.setText(customer_email_value);

                    profileImageString = customer_full_img_value;

                    Picasso.with(getActivity())
                            .load(profileImageString)
                            .into(image_userProfilePic);

                    SharedPreferences.Editor editor1 = settings.edit();
                    editor1.putString(Constants.PREFS_USER_MOBILE, customer_mobile_value);
                    editor1.putString(Constants.PREFS_USER_IMAGE, customer_full_img_value);
                    editor1.putString(Constants.PREFS_USER_NAME, customer_name_value);
                    editor1.putString(Constants.PREFS_USER_EMAIL, customer_email_value);
                    editor1.putString(Constants.PREFS_PROFILE_IMG, profile_img);
                    editor1.apply();

                    UtilsManager.showAlertMessage(getContext(), "", "Profile Updated Successfully");

                }
                catch (Exception e) {
                    Log.e("error occured",e.getMessage());
                  //  UtilsManager.showAlertMessage(getContext(), "", "Profile Updated Successfully");
                }
            }
            else {
                UtilsManager.showAlertMessage(getContext(), "", "Profile could not be updated");
            }


        }

    }

    private void showKeyBoard()
    {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }




}
