package asim.tgs_member_app;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.restclient.MyPrefrences;
import asim.tgs_member_app.utils.ImageCompressClass;
import asim.tgs_member_app.utils.UtilsManager;


public class Upload_Driving_License_Registeration extends AppCompatActivity {

    ImageView back_navigation,licence_image;
    Button btn_next;
    LinearLayout gallery_btn;

    private String profileImageString;
    private MyPrefrences prefs;
    private static String OLD_IMAGE="old_image";
    private boolean isImageChosen = false;
    private String profile_image,professional_image,driving_licence;
    private SharedPreferences sharedPreferences;
    private Button btnSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__driving__license__registeration);


        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        prefs = new MyPrefrences(Upload_Driving_License_Registeration.this);

        back_navigation=(ImageView) findViewById(R.id.back_navigation);
        back_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


       /* if (Constants.hasDrivingLicense) {
            startActivity(new Intent(Upload_Driving_License_Registeration.this, Upload_Passport_Picture_Registeration.class));

            finish();
        }*/


        gallery_btn = (LinearLayout) findViewById(R.id.upload_driving_license);

        licence_image = (ImageView) findViewById(R.id.licence_image);

        btn_next = (Button) findViewById(R.id.btnNext_upload_profile);
        btnSkip = (Button) findViewById(R.id.btnSkip);


        String doc_name = sharedPreferences.getString(Constants.DRIVING_LICENCE_DOC,"no");
        if (doc_name.equalsIgnoreCase("done"))
        {
            btnSkip.performClick();
        }


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Upload_Passport_Picture_Registeration.class));
                sharedPreferences.edit().putString(Constants.DRIVING_LICENCE_DOC,"").apply();
                finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageChosen) {
                    startActivity(new Intent(getApplicationContext(), Upload_Passport_Picture_Registeration.class));
                    sharedPreferences.edit().putString(Constants.DRIVING_LICENCE_DOC,driving_licence).apply();
                    finish();
                }
                 else
                    UtilsManager.showAlertMessage(Upload_Driving_License_Registeration.this,"","Please choose a driving licence.");


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
                prefs.setValue(OLD_IMAGE,profileImageString);
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                profileImageString = cursor.getString(columnIndex);
                Log.e("profile image",profileImageString);
                String file_profile = ImageCompressClass.compressImage(profileImageString);
                Log.e("profile image",file_profile);
                profileImageString = file_profile;
                driving_licence = file_profile;
                cursor.close();
                Bitmap _bitmap = BitmapFactory.decodeFile(file_profile);
                licence_image.setImageBitmap(_bitmap);
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
}
