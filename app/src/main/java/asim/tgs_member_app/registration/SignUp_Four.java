package asim.tgs_member_app.registration;

import android.Manifest;
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
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.restclient.MyPrefrences;
import asim.tgs_member_app.utils.ImageCompressClass;


public class SignUp_Four extends AppCompatActivity {

    public static final int FRONT = 011;
    public static final int SIDE = 001;
    public static final int BACK = 010;

    boolean isfrontSel=false,isBackSel=false,isSideSel=false;

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
                // Create intent to Open Image applications like Gallery, Google Photos
                startCamera(selected_code);
            }
        }
        else
        {
            // Create intent to Open Image applications like Gallery, Google Photos
            startCamera(selected_code);
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
    }
    public String  getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    private MyPrefrences prefs;
    private SharedPreferences sharedPreferences;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == FRONT && resultCode == RESULT_OK
                    ) {
                // Get the Image from data

                String file = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                sharedPreferences.edit().putString(Constants.FRONT,file).apply();

                Bitmap _bitmap = BitmapFactory.decodeFile(file);
                front.setImageBitmap(_bitmap);
                isfrontSel =true;


            } else if (requestCode == BACK && resultCode == RESULT_OK
                   ){
               /* Toast.makeText(RegisterUser.this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();*/

                String file = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                sharedPreferences.edit().putString(Constants.BACK,file).apply();

                Bitmap _bitmap = BitmapFactory.decodeFile(file);
                back.setImageBitmap(_bitmap);
                isBackSel =true;
            }
            else if (requestCode == SIDE && resultCode == RESULT_OK
                   ){
               /* Toast.makeText(RegisterUser.this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();*/
                String file_profile = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                sharedPreferences.edit().putString(Constants.SIDE,file_profile).apply();

                Bitmap _bitmap = BitmapFactory.decodeFile(file_profile);
                side.setImageBitmap(_bitmap);
                isSideSel =true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
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

    private ImageView front,side,back;
    private LinearLayout front_lay,back_lay,side_lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__four);
        setupback();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        prefs = new MyPrefrences(this);
         next = findViewById(R.id.next_btn);

        front = findViewById(R.id.front);
        side = findViewById(R.id.side);
        side_lay = findViewById(R.id.side_lay);
        back = findViewById(R.id.back);

        front_lay = findViewById(R.id.front_lay);

        back_lay = findViewById(R.id.back_lay);

        front_lay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selected_code = FRONT;
                galleryIntent();
            }
        });
        side_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_code = SIDE;
                galleryIntent();
            }
        });
        back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_code = BACK;
                galleryIntent();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isfrontSel && isSideSel) {
                    startActivity(new Intent(SignUp_Four.this, SignUp_Five.class));
                    finish();
                }
                else
                    Toast.makeText(SignUp_Four.this, "Please complete your pictures", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
