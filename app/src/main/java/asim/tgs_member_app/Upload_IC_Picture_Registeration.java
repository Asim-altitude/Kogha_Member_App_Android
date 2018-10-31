package asim.tgs_member_app;

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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.restclient.MyPrefrences;
import asim.tgs_member_app.utils.ImageCompressClass;
import asim.tgs_member_app.utils.ImageRotation;
import asim.tgs_member_app.utils.UtilsManager;


public class Upload_IC_Picture_Registeration extends AppCompatActivity {

    ImageView back_navigation,full_image;
    Button btn_next;
    LinearLayout camera_btn,gallery_btn;

    private String profileImageString;
    private MyPrefrences prefs;
    private static String OLD_IMAGE="old_image";
    private boolean isImageChosen = false;
    private String profile_image,professional_image;
    private Button btnSkip_upload_profile;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__ic_image);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
     /*   if (Constants.hasProfessionalImage) {
            startActivity(new Intent(Upload_IC_Picture_Registeration.this, Upload_Driving_License_Registeration.class).putExtra("professional", ""));
            finish();
        }*/

        back_navigation=(ImageView) findViewById(R.id.back_navigation);
        back_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        full_image = (ImageView) findViewById(R.id.full_image);

        camera_btn = (LinearLayout) findViewById(R.id.camera_btn);
        gallery_btn = (LinearLayout) findViewById(R.id.gallery_btn);

        btn_next = (Button) findViewById(R.id.btnNext_upload_profile);
        btnSkip_upload_profile = (Button) findViewById(R.id.btnSkip_upload_profile);

        String doc_name = sharedPreferences.getString(Constants.IC_CARD_DOC,"no");
        if (doc_name.equalsIgnoreCase("done"))
        {
            btnSkip_upload_profile.performClick();
        }

        btnSkip_upload_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Upload_Driving_License_Registeration.class));
                sharedPreferences.edit().putString(Constants.IC_CARD_DOC,"").apply();
                finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageChosen) {
                    startActivity(new Intent(getApplicationContext(), Upload_Driving_License_Registeration.class));
                    sharedPreferences.edit().putString(Constants.IC_CARD_DOC,professional_image).apply();
                    finish();
                }
                 else
                    UtilsManager.showAlertMessage(Upload_IC_Picture_Registeration.this,"","Please provide a document");

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

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                profileImageString = cursor.getString(columnIndex);
                Log.e("profile image",profileImageString);
                String file_profile = ImageCompressClass.compressImage(profileImageString);
                Log.e("profile image",file_profile);
                profileImageString = file_profile;
                professional_image = file_profile;
                cursor.close();
                Bitmap _bitmap = BitmapFactory.decodeFile(file_profile);
                full_image.setImageBitmap(_bitmap);
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
                professional_image = profileImageString;
                Drawable d = BitmapDrawable.createFromPath(profileImageString);
                full_image.setImageDrawable(d);
                isImageChosen =true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
