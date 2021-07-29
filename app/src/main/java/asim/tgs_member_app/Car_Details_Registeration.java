package asim.tgs_member_app;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.restclient.MyPrefrences;
import asim.tgs_member_app.utils.ImageCompressClass;
import asim.tgs_member_app.utils.ImageRotation;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;


public class Car_Details_Registeration extends AppCompatActivity {

    Button confirm_registeration;
    ImageView back_navigation,car_image;
    EditText car_number,car_name_model;

    private String profileImageString;
    private MyPrefrences prefs;
    private static String OLD_IMAGE="old_image";
    private boolean isImageChosen = false;
    LinearLayout camera_btn,gallery_btn;

    private String profile_image,professional_image,driving_licence,car_image_url="";
    private String IC_image,Latest_side_body_image,passport_image,interier_picture,grant_pic,insurance_pic;
    private String mem_id,mem_name;
    private Button skipBtn;
    private SharedPreferences sharedPreferences;

    private void LoadAllDocuments() {
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        professional_image = sharedPreferences.getString(Constants.STRENGTH_DOC,"");
        Latest_side_body_image = sharedPreferences.getString(Constants.SIDEBODY_DOC,"");
        IC_image = sharedPreferences.getString(Constants.IC_CARD_DOC,"");
        passport_image = sharedPreferences.getString(Constants.PASSPORT_DOC,"");
        interier_picture = sharedPreferences.getString(Constants.INTERIOR_DOC,"");
        insurance_pic = sharedPreferences.getString(Constants.INSURANCE_DOC,"");
        grant_pic = sharedPreferences.getString(Constants.GRANT_DOC,"");
        driving_licence = sharedPreferences.getString(Constants.DRIVING_LICENCE_DOC,"");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car__details__registeration);

        LoadAllDocuments();

        prefs = new MyPrefrences(Car_Details_Registeration.this);

        car_image = findViewById(R.id.car_image);

        SharedPreferences settings = this.getSharedPreferences(Constants.PREFS_NAME, 0);
        mem_name = settings.getString(Constants.PREFS_USER_NAME, "");
        mem_id = settings.getString(Constants.PREFS_USER_ID, "");


        confirm_registeration = findViewById(R.id.confirm_registeration);
        back_navigation= findViewById(R.id.back_navigation);
        back_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        skipBtn =  findViewById(R.id.btnSkip);

        String doc_name = sharedPreferences.getString(Constants.CAR_DOC,"no");
        if (doc_name.equalsIgnoreCase("done"))
        {
            try {
                saveDocumentsOnServer();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                car_image_url = "";
                createDataArrays();
                try {
                    saveDocumentsOnServer();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //   startActivity(new Intent(Car_Details_Registeration.this,DrawerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        camera_btn = findViewById(R.id.camera_btn);
        gallery_btn = findViewById(R.id.gallery_btn);

        confirm_registeration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isImageChosen) {
                        createArrays();
                        try {
                            saveDocumentsOnServer();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        UtilsManager.showAlertMessage(Car_Details_Registeration.this,"","Please provide car picture.");


            }
        });

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              TakeFromCamera();
            }
        });
        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                galleryIntent();
            }
        });


    }



    Uri imageUri;
    private static final int CAMERA_REQUEST =0011;
    private void TakeFromCamera()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "pick_image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Picked fom KOGHA member");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private static int RESULT_LOAD_IMG = 1;
    private static final int REQ_CODE = 0011;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void galleryIntent() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},REQ_CODE);
        }
        else
        {
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
              //  prefs.setValue(OLD_IMAGE,profileImageString);
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                profileImageString = cursor.getString(columnIndex);
                Log.e("profile image",profileImageString);
                String file_profile = ImageCompressClass.compressImage(profileImageString);
                Log.e("profile image",file_profile);
                profileImageString = file_profile;
                car_image_url = file_profile;
                cursor.close();
                Bitmap _bitmap = BitmapFactory.decodeFile(file_profile);
                car_image.setImageBitmap(_bitmap);
                isImageChosen =true;


            } else  if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

                //  Bitmap photo = (Bitmap) data.getExtras().get("data");
                //  Drawable d = new BitmapDrawable(getResources(), photo);

                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);

                thumbnail = ImageRotation.getProperBitmap(getRealPathFromURI(imageUri), thumbnail);
                Log.e("captured image ", getRealPathFromURI(imageUri).toString());


                // CompressedBitmap(thumbnail, path);
                //  ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
                //  bmps.add(thumbnail);

                //  ArrayList<String> list = new ArrayList<String>();
                // list.add(getRealPathFromURI(imageUri));

                // new CompressImageTask(list,bmps).execute();
                profileImageString = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                car_image_url = profileImageString;
                Drawable d = BitmapDrawable.createFromPath(profileImageString);
                car_image.setImageDrawable(d);
                isImageChosen =true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private void createDataArrays()
    {
        file_caption = new String[3];
        dcoumnet_id = new String[3];

        file_caption[0] = "professional image";
        file_caption[1] = "driving licence image";
        file_caption[2] = "car image";


        dcoumnet_id[0] = "5";
        dcoumnet_id[1] = "1";
        dcoumnet_id[2] = "2";
    }

    private void createArrays()
    {
        try
        {
            if (professional_image.equalsIgnoreCase("skip")
                    && driving_licence.equalsIgnoreCase("skip")
                    && car_image_url.equalsIgnoreCase("skip"))
            {
             startActivity(new Intent(Car_Details_Registeration.this,DrawerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
            else if (professional_image.equalsIgnoreCase("skip") && driving_licence.equalsIgnoreCase("skip")
                    && !car_image_url.equalsIgnoreCase("skip")
                    )
        {
            file_caption = new String[1];
            dcoumnet_id = new String[1];

            file_caption[0] = "car image";
            dcoumnet_id[0] = "2";
        }
        else if (!professional_image.equalsIgnoreCase("skip") &&  driving_licence.equalsIgnoreCase("skip")
                    && !car_image_url.equalsIgnoreCase("skip")
                    )
        {

            file_caption = new String[2];
            dcoumnet_id = new String[2];

            file_caption[0] = "car image";
            dcoumnet_id[0] = "2";

            file_caption[1] = "driving licence image";
            dcoumnet_id[1] = "1";
        }
        else if (professional_image.equalsIgnoreCase("skip") && !driving_licence.equalsIgnoreCase("skip")
                    && !car_image_url.equalsIgnoreCase("skip")
                    )
        {

            file_caption = new String[2];
            dcoumnet_id = new String[2];

            file_caption[0] = "car image";
            dcoumnet_id[0] = "2";

            file_caption[1] = "professional image";
            dcoumnet_id[1] = "5";
        }
        else
        {
            file_caption = new String[9];
            dcoumnet_id = new String[9];

            file_caption[0] = "professional image";
            file_caption[1] = "driving licence image";
            file_caption[2] = "car image";


            dcoumnet_id[0] = "5";
            dcoumnet_id[1] = "1";
            dcoumnet_id[2] = "2";
        }


          /*  FileInputStream fileInputStream = new FileInputStream(profile_image);*/

          /*  document_name[0] = new FileInputStream(profile_image);
            document_name[1] = new FileInputStream(professional_image);
            document_name[2] = new FileInputStream(driving_licence);
            document_name[3] = new FileInputStream(car_image_url);*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("error","creating arrays "+e.getMessage());
        }
    }

    String[] file_caption,dcoumnet_id;
    FileInputStream[] document_name;

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void saveDocumentsOnServer() throws FileNotFoundException {

        asyncHttpClient.setConnectTimeout(30000);

        RequestParams params = new RequestParams();


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
       int index = 0;
           if (!car_image_url.equalsIgnoreCase("")) {
               params.put("document_name["+index+"]", new FileInputStream(car_image_url));
               params.put("document_id["+index+"]","2");
               params.put("file_caption["+index+"]","car image");
               index++;
           }


           if (!driving_licence.equalsIgnoreCase("")) {
               params.put("document_name["+index+"]", new FileInputStream(driving_licence));
               params.put("document_id["+index+"]","1");
               params.put("file_caption["+index+"]","driving licence image");
               index++;
           }


           if (!professional_image.equalsIgnoreCase("")) {
               params.put("document_name["+index+"]", new FileInputStream(professional_image));
               params.put("document_id["+index+"]","5");
               params.put("file_caption["+index+"]","professional image");
               index++;
           }

        if (!IC_image.equalsIgnoreCase("")) {
            params.put("document_name["+index+"]", new FileInputStream(IC_image));
            params.put("document_id["+index+"]","8");
            params.put("file_caption["+index+"]","ID card");
            index++;
        }



        if (!passport_image.equalsIgnoreCase("")) {
            params.put("document_name["+index+"]", new FileInputStream(passport_image));
            params.put("document_id["+index+"]","6");
            params.put("file_caption["+index+"]","IC_Passport Cop");
            index++;
        }



        if (!insurance_pic.equalsIgnoreCase("")) {
            params.put("document_name["+index+"]", new FileInputStream(insurance_pic));
            params.put("document_id["+index+"]","11");
            params.put("file_caption["+index+"]","Car insurance pic");
            index++;
        }



        if (!interier_picture.equalsIgnoreCase("")) {
            params.put("document_name["+index+"]", new FileInputStream(interier_picture));
            params.put("document_id["+index+"]","9");
            params.put("file_caption["+index+"]","Car interier");
            index++;
        }
     /*   if (!Latest_side_body_image.equalsIgnoreCase("")) {
            params.put("document_name["+index+"]", new FileInputStream(professional_image));
            params.put("document_id["+index+"]","5");
            params.put("file_caption["+index+"]","professional image");
            index++;
        }*/

        if (!grant_pic.equalsIgnoreCase("")) {
            params.put("document_name["+index+"]", new FileInputStream(grant_pic));
            params.put("document_id["+index+"]","10");
            params.put("file_caption["+index+"]","Car grant pic");
            index++;
        }

      /*  List<String> docs_id = Arrays.asList(dcoumnet_id);
        List<String> docs_cap = Arrays.asList(file_caption);

        docs_id.removeAll(Arrays.asList("",null));
        docs_cap.removeAll(Arrays.asList("",null));

        dcoumnet_id = (String[]) docs_id.toArray();
        file_caption = (String[]) docs_cap.toArray();

        params.put("document_id",dcoumnet_id);
        params.put("file_caption",file_caption);
        params.put("member_id",mem_id);*/

       // }


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
                    startActivity(new Intent(Car_Details_Registeration.this,Registeration_completed_Screen.class));
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

        progress = new ProgressDialog(Car_Details_Registeration.this);
        progress.setMessage("Uploading documents");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }


}
