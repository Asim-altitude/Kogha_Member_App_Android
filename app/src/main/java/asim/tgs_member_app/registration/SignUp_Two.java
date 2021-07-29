package asim.tgs_member_app.registration;

import android.Manifest;
import android.app.ProgressDialog;
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
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.registration.camera.CaptureFaceScreen;

public class SignUp_Two extends AppCompatActivity {
    private static final String TAG = "SignUp_Two";

    public static final int PICK_CODE = 0011;
    public static final int REQ_CODE = 1011;

    boolean isSelected = false;

    public void galleryIntent() {

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE);
                } else {
                    // Create intent to Open Image applications like Gallery, Google Photos
              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    dispatchTakePictureIntent();
                }
                else {
                    startCamera(PICK_CODE);
                }*/
                    startCamera(PICK_CODE);
                }
            } else {
                // Create intent to Open Image applications like Gallery, Google Photos
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                dispatchTakePictureIntent();
            }
            else {
                startCamera(PICK_CODE);
            }*/
                startCamera(PICK_CODE);
            }

        }catch (Exception e){
            Toast.makeText(SignUp_Two.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }


    /*String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  *//* prefix *//*
                ".jpg",         *//* suffix *//*
                storageDir      *//* directory *//*
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        ""+getPackageName()+".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

*/
    Uri imageUri = null;
    private void startCamera(int PICK_CODE) {
       /* Intent intent = new Intent(SignUp_Two.this,FaceDetectRGBActivity.class);
        startActivityForResult(intent,PICK_CODE);*/
      Intent intent = new Intent(SignUp_Two.this,CaptureFaceScreen.class);
      startActivityForResult(intent,PICK_CODE);
        /*ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "pick_image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Picked from member app");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        } else {
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }
        startActivityForResult(intent, PICK_CODE);*/
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

    private String proile_path;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_CODE && resultCode == RESULT_OK) {
                // Get the Image from data

               // String file = data.getData().toString();//ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                String file = data.getData().toString();//ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
               // String file = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                proile_path = file;

                Log.e(TAG, "onActivityResult: "+proile_path );
                Bitmap _bitmap = BitmapFactory.decodeFile(file);
                imageView.setImageBitmap(_bitmap);
                isSelected = true;


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    String[] ids;
    String[] type_ids;
    LinearLayout upload_lay;
    ImageView imageView;
    LinearLayout cancel,next;
    private void setupback(){
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    EditText full_name_txt,name_txt,number_txt,email_txt;
    TextView fnam_message,name_message,contact_message;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__two);
        setupback();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);

        next = findViewById(R.id.next_btn);
        bindViews();


       /* ids = getIntent().getStringArrayExtra("ids");
        type_ids = getIntent().getStringArrayExtra("type_ids");
*/
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid()){

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.PREFS_USER_NAME,name_txt.getText().toString());
                    editor.putString(Constants.PREFS_USER_FULL_NAME,full_name_txt.getText().toString());
                    editor.putString(Constants.PREFS_USER_MOBILE,number_txt.getText().toString());
                    editor.putString(Constants.PREFS_USER_EMAIL,email_txt.getText().toString());
                    editor.putString(Constants.PREFS_USER_IMAGE,proile_path);
                    editor.apply();
                    editor.commit();

                    startActivity(new Intent(SignUp_Two.this,SignUp_One.class));
                    finish();
                }
               // saveBasicInfo();
            }
        });


        try {

            boolean isback = getIntent().getBooleanExtra("back",false);

            if (isback) {

                String name = sharedPreferences.getString(Constants.PREFS_USER_NAME, "");
                String fname = sharedPreferences.getString(Constants.PREFS_USER_FULL_NAME, "");
                String contact = sharedPreferences.getString(Constants.PREFS_USER_MOBILE, "");
                String email = sharedPreferences.getString(Constants.PREFS_USER_EMAIL, "");
                String file = sharedPreferences.getString(Constants.PREFS_USER_IMAGE, "");
                proile_path = file;

                name_txt.setText(name);
                full_name_txt.setText(fname);
                email_txt.setText(email);
                number_txt.setText(contact);

                Bitmap _bitmap = BitmapFactory.decodeFile(file);
                imageView.setImageBitmap(_bitmap);
                isSelected = true;

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



    private boolean valid() {
        boolean valid = true;
        if (proile_path==null){
            valid = false;
            Toast.makeText(SignUp_Two.this,"Upload profile picture",Toast.LENGTH_SHORT).show();

        }
        if (name_txt.getText().toString().trim().length() ==0
                || email_txt.getText().toString().trim().length()==0
                || number_txt.getText().toString().trim().length()==0){
            valid = false;
            Toast.makeText(SignUp_Two.this,"Please provide basic information",Toast.LENGTH_SHORT).show();


        }

        //Your nickname should not be less than 3 characters

        String fname_text = full_name_txt.getText().toString().trim().replaceAll("\\s+", "");
        if (fname_text.length() < 8){
            valid = false;
            fnam_message.setVisibility(View.VISIBLE);

        }else {
            fnam_message.setVisibility(View.GONE);
        }
         fname_text = name_txt.getText().toString().trim().replaceAll("\\s+", "");
        if (fname_text.length() < 3){
            valid = false;
            name_message.setVisibility(View.VISIBLE);

        }else {
            name_message.setVisibility(View.GONE);
        }

        if (number_txt.getText().toString().trim().length() < 9 ){
            valid = false;
            contact_message.setVisibility(View.VISIBLE);

        }else {
            contact_message.setVisibility(View.GONE);
        }


        return valid;
    }

    private void bindViews() {
        upload_lay = findViewById(R.id.profile_layout_top);
        imageView = findViewById(R.id.userProfilePic);
        name_txt = findViewById(R.id.name_txt);
        number_txt = findViewById(R.id.number_txt);
        email_txt = findViewById(R.id.email_txt);
        full_name_txt = findViewById(R.id.full_name_txt);

        fnam_message = findViewById(R.id.fname_message);
        name_message = findViewById(R.id.name_message);
        contact_message = findViewById(R.id.contact_message);

        upload_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
            }
        });
    }

    private ProgressDialog progress;
    private void showProgressDialog()
    {

        progress = new ProgressDialog(SignUp_Two.this);
        progress.setMessage("Saving info");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }
}

