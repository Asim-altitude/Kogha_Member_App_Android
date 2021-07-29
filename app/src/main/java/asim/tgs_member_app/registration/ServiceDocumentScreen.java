package asim.tgs_member_app.registration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import asim.tgs_member_app.AddBankInfoScreen;
import asim.tgs_member_app.BankScreen;
import asim.tgs_member_app.R;
import asim.tgs_member_app.Registeration_completed_Screen;
import asim.tgs_member_app.adapters.ServiceDocumentAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.ServiceDocument;
import asim.tgs_member_app.registration.ui.FaceDetectRGBActivity;
import asim.tgs_member_app.restclient.MyPrefrences;
import asim.tgs_member_app.utils.DocumentSelectorCallBack;
import asim.tgs_member_app.utils.ImageCompressClass;
import cz.msebera.android.httpclient.Header;

public class ServiceDocumentScreen extends AppCompatActivity implements DocumentSelectorCallBack {
    private static final String TAG = "ServiceDocumentScreen";

    ListView documentList;
    ServiceDocumentAdapter serviceDocumentAdapter;
    List<ServiceDocument> serviceDocumentList;
    SharedPreferences sharedPreferences;
    LinearLayout doneBtn;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_document_screen);

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        mem_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"0");
        documentList = findViewById(R.id.doc_list);
        doneBtn = findViewById(R.id.next_btn);
        back = findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allow = true;
                for (int i=0;i<serviceDocumentList.size();i++){
                    if (!serviceDocumentList.get(i).isUploaded()){
                        allow = false;
                        break;
                    }
                }

                allow = true;
                if (allow){
                    saveDocumentsOnServer();
                }else {
                    Toast.makeText(ServiceDocumentScreen.this,"Please Complete Your Documents",Toast.LENGTH_LONG).show();
                }

                Log.e(TAG, "onClick: ALLOWED ");
            }
        });

        resolveDocuments();

    }


    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            //setDynamicHeight(listView);
            return true;

        } else {
            return false;
        }

    }

    private void resolveDocuments() {

        String json = sharedPreferences.getString(Constants.DOC_LIST_JSON,"");
        if (!json.equalsIgnoreCase("")){
            serviceDocumentList = new ArrayList<>();
            Log.e(TAG, "resolveDocuments: "+json);
            try {

                JSONObject jsonObject = new JSONObject(json);
                JSONArray idArray = jsonObject.getJSONArray("id");
                JSONArray nameArray = jsonObject.getJSONArray("document_name");


                for (int i=0;i<idArray.length();i++){
                    String id = idArray.get(i).toString();
                    String doc_name = nameArray.get(i).toString();

                    ServiceDocument serviceDocument = new ServiceDocument();
                    serviceDocument.setDoc_id(id);
                    serviceDocument.setDocument_name(doc_name);
                    serviceDocument.setDocument_path("");

                    String file = sharedPreferences.getString("doc_"+id,"");
                    if (file.equalsIgnoreCase("")) {
                        serviceDocument.setUploaded(false);
                        serviceDocument.setDocArray(null);
                    }else {

                        serviceDocument.setUploaded(true);
                        serviceDocument.setDocArray(file.split(Pattern.quote(",")));
                    }



                    serviceDocumentList.add(serviceDocument);
                }

                serviceDocumentAdapter = new ServiceDocumentAdapter((ArrayList<ServiceDocument>) serviceDocumentList,ServiceDocumentScreen.this);
                serviceDocumentAdapter.setDocumentSelectorCallBack(ServiceDocumentScreen.this);
                documentList.setAdapter(serviceDocumentAdapter);
                setListViewHeightBasedOnItems(documentList);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    int selected_postion = 0;
    @Override
    public void onDocumentSelection(int position) {
        selected_postion = position;
        if (serviceDocumentList.get(position).getDoc_id().equalsIgnoreCase("3")){
            captureDrivingLicensePic();
        }else {
            galleryIntent();
        }
    }

    private static final int REQ_CODE = 0011;
    private static final int CHOOSE = 1011;

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

                selected_code = CHOOSE;
               // browseDocuments();
                showBottomSheetDialog();
            }
        }
        else
        {
           // browseDocuments();
            // Create intent to Open Image applications like Gallery, Google Photos
           showBottomSheetDialog();
        }


    }

    public void captureDrivingLicensePic(){

        selected_code = CHOOSE;
        Intent intent = new Intent(ServiceDocumentScreen.this,FaceDetectRGBActivity.class);
        startActivityForResult(intent,selected_code);

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
        Intent intent = new Intent(ServiceDocumentScreen.this,DocumentDirectoryScreen.class);
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
        startActivityForResult(intent, CHOOSE);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CHOOSE && resultCode == RESULT_OK
                    && (null != data || imageUri!=null)) {

                if (serviceDocumentList.get(selected_postion).getDoc_id().equalsIgnoreCase("3")){
                    String file = data.getData().toString();
                    String[] items = new String[1];
                    items[0] = file;

                    sharedPreferences.edit().putString("doc_"+serviceDocumentList.get(selected_postion).getDoc_id(),file).apply();
                    serviceDocumentList.get(selected_postion).setUploaded(true);
                    serviceDocumentList.get(selected_postion).setDocArray(items);

                    serviceDocumentAdapter.notifyDataSetChanged();

                    return;
                }

                if (camera_code==1){
                    String file_profile = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));

                    String[] items = new String[1];
                    items[0] = file_profile;

                    sharedPreferences.edit().putString("doc_"+serviceDocumentList.get(selected_postion).getDoc_id(),file_profile).apply();
                    serviceDocumentList.get(selected_postion).setUploaded(true);
                    serviceDocumentList.get(selected_postion).setDocArray(items);

                    serviceDocumentAdapter.notifyDataSetChanged();
                }else {
                    String file_profile = null;
                    file_profile = data.getData().toString();/*selectedDoc.toString();//*///UtilsManager.getPathFromUri(SignUp_Five.this, selectedDoc);

                    String[] items = file_profile.split(Pattern.quote(","));

                    sharedPreferences.edit().putString("doc_"+serviceDocumentList.get(selected_postion).getDoc_id(),file_profile).apply();
                    serviceDocumentList.get(selected_postion).setUploaded(true);
                    serviceDocumentList.get(selected_postion).setDocArray(items);

                    serviceDocumentAdapter.notifyDataSetChanged();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    String mem_id;

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void saveDocumentsOnServer() {

        asyncHttpClient.setConnectTimeout(30000);
        asyncHttpClient.setTimeout(20*1000);
        RequestParams params = new RequestParams();

        try {

           List<ServiceDocument> filteredList =  filterList();

            ServiceDocument serviceDocument = new ServiceDocument();
            serviceDocument.setDoc_id("0");
            serviceDocument.setDocument_name("side image");
            serviceDocument.setDocument_path("");

            String file = sharedPreferences.getString(Constants.SIDE,"");
            if (file.equalsIgnoreCase("")) {
                serviceDocument.setUploaded(false);
                serviceDocument.setDocArray(null);
            }else {

                serviceDocument.setUploaded(true);
                serviceDocument.setDocArray(file.split(Pattern.quote(",")));
            }

            filteredList.add(serviceDocument);


            for (int i=0;i<filteredList.size();i++){

                params.put("document_id[" + i + "]", filteredList.get(i).getDoc_id());
                params.put("file_caption[" + i + "]", filteredList.get(i).getDocument_name());

                for (int j = 0; j < filteredList.get(i).getDocArray().length; j++) {
                    // items[j] = new FileInputStream(serviceDocumentList.get(i).getDocArray()[j]);
                    params.put("document_name[" + i + "][" + j + "]", new File(filteredList.get(i).getDocArray()[j]));
                }
            }

          /*  params.put("document_name["+index+"][0]", new File(sharedPreferences.getString(Constants.SIDE,"")));
            params.put("document_id[" + index + "]", "0");
            params.put("file_caption[" + index + "]", "side image");*/





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
                    //sharedPreferences.edit().putInt(Constants.REGISTRATION_STEP,1).apply();

                    startActivity(new Intent(ServiceDocumentScreen.this,AddBankInfoScreen.class)
                            .putExtra("is_reg",true));

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

    private List<ServiceDocument> filterList() {
        List<ServiceDocument> tempSeerviceDocuments = new ArrayList<>();
        for (ServiceDocument obj: serviceDocumentList) {
            if (obj.isUploaded()){
                tempSeerviceDocuments.add(obj);
            }
        }

        return tempSeerviceDocuments;
    }

    private ProgressDialog progress;
    private void showProgressDialog()
    {

        progress = new ProgressDialog(ServiceDocumentScreen.this);
        progress.setMessage("Uploading documents");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: ");
        startActivity(new Intent(ServiceDocumentScreen.this,SignUp_Five.class).putExtra("isBack",true));
        finish();
    }
}
