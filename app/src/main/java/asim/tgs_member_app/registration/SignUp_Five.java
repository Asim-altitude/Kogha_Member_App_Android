package asim.tgs_member_app.registration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

import asim.tgs_member_app.R;
import asim.tgs_member_app.Registeration_completed_Screen;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.registration.camera.CaptureFrontBodyScreen;
import asim.tgs_member_app.registration.ui.FaceDetectRGBActivity;
import asim.tgs_member_app.restclient.MyPrefrences;
import asim.tgs_member_app.utils.ImageCompressClass;
import cz.msebera.android.httpclient.Header;

public class SignUp_Five extends AppCompatActivity {
    private static final String TAG = "SignUp_Five";

    public static final int CV = 011;
    public static final int SIDE = 000;
    public static final int PASS = 001;
    public static final int DRIVE = 010;

    boolean isfrontSel=false,isBackSel=false,isSideSel=false,isSideSelected=false;

    private static final int REQ_CODE = 0011;

    private int selected_code = 0;

    public void galleryIntent() {

        if (Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED  ||
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},REQ_CODE);
            }
            else
            {

                if (selected_code==PASS || selected_code==SIDE){
                    startCamera(selected_code);
                }
                else {
                    // Create intent to Open Image applications like Gallery, Google Photos
                    pickDocument();

                }
            }
        }
        else
        {
            // Create intent to Open Image applications like Gallery, Google Photos
            if (selected_code==PASS){
                startCamera(selected_code);
            }
            else {
                // Create intent to Open Image applications like Gallery, Google Photos
                pickDocument();
            }
        }

    }

    int camera_code = 0;
    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.choose_camera_gallery, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);


        final LinearLayout camera = view.findViewById(R.id.camera);
        LinearLayout galllery = view.findViewById(R.id.files);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                camera_code = 1;
                startCamera(selected_code);
            }
        });

        galllery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                camera_code = 0;
                pickDocument();
            }
        });

        dialog.show();
    }

    private void pickDocument(){
        Intent intent = new Intent(SignUp_Five.this,DocumentDirectoryScreen.class);
        startActivityForResult(intent,selected_code);
    }
    private void browseDocuments(){

        String[] mimeTypes =
                {
                        "application/docx",
                        "application/pdf",
                };

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"Choose CV "), selected_code);

    }


    public void capturePassportPic(){

        Intent intent = new Intent(SignUp_Five.this,FaceDetectRGBActivity.class);
        startActivityForResult(intent,selected_code);

    }
    public void captureSideBodyPic(){

        Intent intent = new Intent(SignUp_Five.this,CaptureFrontBodyScreen.class);
        startActivityForResult(intent,selected_code);

    }
    Uri imageUri = null;
    private void startCamera(int PICK_CODE) {

      /*Intent intent = new Intent(SignUp_Step_One.this,CaptureFaceScreen.class);
      startActivityForResult(intent,PICK_CODE);*/
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "pick_image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Picked from member app");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PICK_CODE);

       /* Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_CODE);*/
    }

    public String  getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
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

    private MyPrefrences prefs;
    private SharedPreferences sharedPreferences;
    String[] items = null;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            if (requestCode == CV && resultCode == RESULT_OK
                    && (null != data || imageUri!=null)) {
                // Get the Image from data

                /*Uri selectedDoc = data.getData();
                Log.e("selected_doc_path",selectedDoc.toString());
*/
                if (camera_code==1){
                    String file_profile = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                    sharedPreferences.edit().putString(Constants.CV, file_profile).apply();

                    items = new String[1];
                    items[0] = file_profile;
                    ((TextView) findViewById(R.id.cv_name)).setText("(1) Files ");
                    Picasso.with(SignUp_Five.this).load(new File(items[0])).into(cv);
                }else {
                    String file_profile = null;
                    file_profile = data.getData().toString();/*selectedDoc.toString();//*///UtilsManager.getPathFromUri(SignUp_Five.this, selectedDoc);

                     items = file_profile.split(Pattern.quote(","));
                    // Toast.makeText(SignUp_Five.this,"Items "+items.length,Toast.LENGTH_LONG).show();
               /* if (selectedDoc.toString().startsWith("raw:")){
                    file_profile = selectedDoc.toString().replace(Pattern.quote("raw:"),"");
                }
                else {
                    file_profile = *//*selectedDoc.toString();//*//*UtilsManager.getPathFromUri(SignUp_Five.this, selectedDoc);
                    if (file_profile.startsWith("raw:")){
                        file_profile = file_profile.replace(Pattern.quote("raw:"),"");
                    }
                }*/

                    sharedPreferences.edit().putString(Constants.CV, file_profile).apply();

                    ((TextView) findViewById(R.id.cv_name)).setText("(" + items.length + ") Files ");
                    Picasso.with(SignUp_Five.this).load(new File(items[0])).into(cv);

                }
                /*String fileName = new File(file_profile).getName();
                ((TextView)findViewById(R.id.cv_name)).setText(fileName);
                if (fileName.endsWith(".docx"))
                    cv.setImageResource(R.drawable.docx_icon);
                else  if (fileName.endsWith(".pdf"))
                    cv.setImageResource(R.drawable.pdf_icon);
                else {
                    ((TextView)findViewById(R.id.cv_name)).setText("Invalid format");
                    Toast.makeText(SignUp_Five.this,"Please upload pdf or docx file",Toast.LENGTH_LONG).show();

                }*/
             /*   Bitmap _bitmap = BitmapFactory.decodeFile(file_profile);
                cv.setImageBitmap(_bitmap);*/
                isfrontSel = true;


            } else if (requestCode == PASS && resultCode == RESULT_OK) {
               /* Toast.makeText(RegisterUser.this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();*/

                //String file_profile = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                String file = data.getData().toString();
                sharedPreferences.edit().putString(Constants.PASS, file).apply();

                Bitmap _bitmap = BitmapFactory.decodeFile(file);
                passport.setImageBitmap(_bitmap);
                isBackSel = true;

            }
            else if (requestCode == SIDE && resultCode == RESULT_OK) {
               /* Toast.makeText(RegisterUser.this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();*/
                String file = data.getData().toString();//ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                // String file = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
              //  String file_profile = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));

                sharedPreferences.edit().putString(Constants.SIDE, file).apply();

                Bitmap _bitmap = BitmapFactory.decodeFile(file);
                side.setImageBitmap(_bitmap);
                isSideSelected = true;

            }
            else if (requestCode == DRIVE && resultCode == RESULT_OK
                    && null != data) {
               /* Toast.makeText(RegisterUser.this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();*/
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String profileImageString = cursor.getString(columnIndex);
                Log.e("profile image", profileImageString);
                String file_profile = ImageCompressClass.compressImage(profileImageString);
                Log.e("profile image", file_profile);
                profileImageString = file_profile;
                sharedPreferences.edit().putString(Constants.DRIVE, file_profile).apply();
                cursor.close();
                Bitmap _bitmap = BitmapFactory.decodeFile(file_profile);
                driveing.setImageBitmap(_bitmap);
                isSideSel = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SignUp_Five.this,"Error "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
        LinearLayout cancel,next;
    private void setupback(){
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private ImageView cv,passport,driveing,side;
    private LinearLayout cv_lay,pass_lay,driving_lay,side_lay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__five);
        setupback();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        sharedPreferences.edit().putInt(Constants.REGISTRATION_STEP,0).apply();
        sharedPreferences.edit().putBoolean(Constants.IS_IN_REGISTRATION,true).apply();
        //mem_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");
        prefs = new MyPrefrences(this);
         next = findViewById(R.id.next_btn);

        cv = findViewById(R.id.cv);
        passport = findViewById(R.id.pass);
        driveing = findViewById(R.id.driving);
        side = findViewById(R.id.side);
        side_lay = findViewById(R.id.side_lay);

        cv_lay = findViewById(R.id.cv_lay);
        pass_lay = findViewById(R.id.pass_lay);
        driving_lay = findViewById(R.id.driving_lay);

        cv_lay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selected_code = CV;
               // galleryIntent();
                showBottomSheetDialog();
            }
        });
        side_lay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selected_code = SIDE;
                captureSideBodyPic();
               // galleryIntent();
            }
        });
        pass_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_code = PASS;
                capturePassportPic();
                //galleryIntent();
            }
        });
        driving_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_code = DRIVE;
                galleryIntent();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (isBackSel && isSideSelected)

                {
                    //Intent intent = new Intent(SignUp_Five.this,)
                   // saveDocumentsOnServer();
                   saveDocs();
                   /* Intent intent = new Intent(SignUp_Five.this,ServiceDocumentScreen.class);
                    startActivity(intent);
                    finish();*/
                }
                else
                    Toast.makeText(SignUp_Five.this, "Please complete your documents", Toast.LENGTH_SHORT).show();        }
        });


        boolean isBack = getIntent().getBooleanExtra("isBack",false);
        if (true){
            String cv_ = sharedPreferences.getString(Constants.CV,"");
            String pass = sharedPreferences.getString(Constants.PASS,"");
            String side_ = sharedPreferences.getString(Constants.SIDE,"");

            if (!pass.equalsIgnoreCase("")){

                Bitmap _bitmap = BitmapFactory.decodeFile(side_);
                side.setImageBitmap(_bitmap);
                isSideSelected = true;

                Bitmap _bitmap1 = BitmapFactory.decodeFile(pass);
                passport.setImageBitmap(_bitmap1);
                isBackSel = true;

                if (!cv_.equalsIgnoreCase("")) {
                    items = cv_.split(Pattern.quote(","));
                    ((TextView) findViewById(R.id.cv_name)).setText("(" + items.length + ") Files");

                    Glide.with(this).load(items[0]).into(cv);
             /*   Bitmap _bitmap = BitmapFactory.decodeFile(file_profile);
                cv.setImageBitmap(_bitmap);*/
                    isfrontSel = true;
                }

            }
        }
    }


    String mem_id;

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void saveDocumentsOnServer() {

        asyncHttpClient.setConnectTimeout(30000);
        asyncHttpClient.setTimeout(20*1000);
        RequestParams params = new RequestParams();

        try {


           /* for (int i=0;i<serviceDocumentList.size();i++){
                params.put("document_name[" + i + "]",new FileInputStream(serviceDocumentList.get(i).getDocument_path()));
                params.put("document_id[" + i + "]", serviceDocumentList.get(i).getDoc_id());
                params.put("file_caption[" + i + "]", serviceDocumentList.get(i).getDocument_name());
            }

            int index = serviceDocumentList.size();*/
           int index = 0;
            params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.SIDE, "")));
            params.put("document_id[" + index + "]", "2");
            params.put("file_caption[" + index + "]", "side image");
            index++;


            params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.FRONT, "")));
            params.put("document_id[" + index + "]", "1");
            params.put("file_caption[" + index + "]", "front image");
            index++;

            params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.CV, "")));
            params.put("document_id[" + index + "]", "8");
            params.put("file_caption[" + index + "]", "CV ");
            index++;


            params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.PASS, "")));
            params.put("document_id[" + index + "]", "6");
            params.put("file_caption[" + index + "]", "IC_Passport ");
            index++;


       /* if (professional_image.equalsIgnoreCase("skip") && driving_licence.equalsIgnoreCase("skip")) {
            params.put("document_name[0]", new FileInputStream(car_image_url));
        }
        else if (!professional_image.equalsIgnoreCase("skip") && driving_licence.equalsIgnoreCase("skip")) {
            params.put("document_name[0]", new FileInputStream(car_image_url));
            params.put("document_name[1]", new FileInputStream(professional_image));
        }
        else if (professional_image.equalsIgnoreCase("skip") && !driving_licence.equalsIgnoreCase("skip")) {
            params.put("document_name[0]", new FileInputStream(car_image_url));
            params.put("document_name[1]", new FileInputStream(driving_licence));
        }
        else
        {*/
           /* int index = 0;

            params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.SIDE, "")));
            params.put("document_id[" + index + "]", "2");
            params.put("file_caption[" + index + "]", "side image");
            index++;


            params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.FRONT, "")));
            params.put("document_id[" + index + "]", "1");
            params.put("file_caption[" + index + "]", "front image");
            index++;


          *//*  params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.BACK, "")));
            params.put("document_id[" + index + "]", "5");
            params.put("file_caption[" + index + "]", "back image");
            index++;
*//*

            params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.CV, "")));
            params.put("document_id[" + index + "]", "8");
            params.put("file_caption[" + index + "]", "CV ");
            index++;


            params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.PASS, "")));
            params.put("document_id[" + index + "]", "6");
            params.put("file_caption[" + index + "]", "IC_Passport ");
            index++;


            params.put("document_name[" + index + "]", new FileInputStream(sharedPreferences.getString(Constants.DRIVE, "")));
            params.put("document_id[" + index + "]", "11");
            params.put("file_caption[" + index + "]", "Driving Licence");*/


        }
        catch (Exception e){
            e.printStackTrace();
        }



        params.put("member_id",mem_id);
        params.put("key","tgs_appkey_amin");

        asyncHttpClient.post(getApplicationContext(), Constants.Host_Address+"members/register_documents", params, new AsyncHttpResponseHandler() {

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
                    Log.e("respose",response);
                    Toast.makeText(getApplicationContext(),"Documents uploaded successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUp_Five.this, Registeration_completed_Screen.class));
                    finish();
                }
                catch (Exception e)
                {
                    Log.e("exception ",e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e("respose failed",response);
                    Toast.makeText(getApplicationContext(),"Could not upload documents ",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Log.e("exception ",e.getMessage());
                }
            }
        });

    }

    private ProgressDialog progress;
    private void showProgressDialog()
    {

        progress = new ProgressDialog(SignUp_Five.this);
        progress.setMessage("Uploading documents");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }



    FileInputStream[] cv_items;
    private void saveDocs() {

        asyncHttpClient.setTimeout(10*1000);

        RequestParams params = new RequestParams();
        try {

            if (isfrontSel) {
                cv_items = new FileInputStream[items.length];
                for (int i = 0; i < items.length; i++) {
                    //cv_items[0] = new FileInputStream(items[i]);
                    params.put("cv[" + i + "]", new File(items[i]));
                }
            }else {
                params.put("cv[0]", "");
            }

            params.put("member_id", sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,""));
            params.put("malaysian_id",new File(sharedPreferences.getString(Constants.PASS,"")));
           // params.put("cv ", cv_items);
            // params.put("certificate", new FileInputStream(certificate));

        }
        catch (Exception e){
            e.printStackTrace();
        }

        asyncHttpClient.post(SignUp_Five.this, Constants.Host_Address + "memebr_upload_cv", params, new AsyncHttpResponseHandler() {

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
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);

                    int errorcode = jsonObject.getInt("errorCode");
                    if (errorcode==00){


                        Intent intent = new Intent(SignUp_Five.this,ServiceDocumentScreen.class);
                        startActivity(intent);
                        finish();

                    }else {
                        String message = jsonObject.getString("message");
                        Toast.makeText(SignUp_Five.this,message,Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(SignUp_Five.this,"Server Error",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(SignUp_Five.this,message,Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();

                    Toast.makeText(SignUp_Five.this,"Server Error",Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: ");
    }

}
