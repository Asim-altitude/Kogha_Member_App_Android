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
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.adapters.SignUp_Services_Adapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberService;
import asim.tgs_member_app.models.Order_Service_Info;
import asim.tgs_member_app.models.Service_Slot;
import asim.tgs_member_app.restclient.MyPrefrences;
import asim.tgs_member_app.utils.ImageCompressClass;
import asim.tgs_member_app.utils.ServiceSelectionCallBack;
import cz.msebera.android.httpclient.Header;

public class SignUp_One extends AppCompatActivity {
    private static final String TAG = "SignUp_One";

    LinearLayout cancel,next;
    LinearLayout  personal_body,cum_drive_body,static_body,bumble_body,document_body,delivery_body;
    RelativeLayout personal_header,cum_driver_header,static_header,bumble_header,document_header,delivery_header;

    public static final int PICK_CODE = 0011;
    public static final int REQ_CODE = 1011;

    boolean isSelected = false;
    public void galleryIntent() {

        try{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
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
               // startCamera();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_CODE);
            }
        }
        else
        {
            // Create intent to Open Image applications like Gallery, Google Photos
          //  startCamera();
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, PICK_CODE);
        }

        }catch (Exception e){
            Toast.makeText(SignUp_One.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }


    Uri imageUri = null;
    private void startCamera() {

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
            if (requestCode == PICK_CODE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

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


                sharedPreferences.edit().putString(Constants.CERTIFICATE, file_profile).apply();

                Bitmap _bitmap = BitmapFactory.decodeFile(file_profile);
                imageView.setImageBitmap(_bitmap);
                isSelected = true;


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    LinearLayout upload_lay;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__one);

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        setupback();
        bindViews();

        service_ids = new ArrayList<String>();
        service_type_ids = new ArrayList<String>();
        next = findViewById(R.id.next_btn);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true) {

                    collectServices();

                }
                else {
                    Toast.makeText(SignUp_One.this,"Upload certificate",Toast.LENGTH_SHORT).show();
                }
            }
        });


        getServicesFromServer();

    }

    private void bindViews() {
        personal_body = findViewById(R.id.personal_body);
        delivery_body = findViewById(R.id.delivery_body);
        cum_drive_body = findViewById(R.id.cum_driver_body);
        static_body = findViewById(R.id.static_body);
        personal_header = findViewById(R.id.personal_header_lay);
        cum_driver_header = findViewById(R.id.cum_driver_header_lay);
        static_header = findViewById(R.id.static_header_lay);
        bumble_body = findViewById(R.id.bumble_body);
        bumble_header = findViewById(R.id.bumble_header_lay);

        document_body = findViewById(R.id.document_body);
        document_header = findViewById(R.id.document_header_lay);

        delivery_header = findViewById(R.id.delivery_header_lay);

        document_recycler = findViewById(R.id.document_recycler);
        delivery_recycler = findViewById(R.id.delivery_recycler);

        upload_lay = findViewById(R.id.upload_lay);
        imageView = findViewById(R.id.doc_image_view);

        upload_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
            }
        });

        bodyguard_recycle = findViewById(R.id.bodyguard_recycler);
        bodyguard_cum_driver_recycle = findViewById(R.id.bodyguard_cum_recycler);
        driver_chauffer_recycle = findViewById(R.id.bodyguard_driver_recycler);
        bumble_recycle = findViewById(R.id.bumble_recycler);

       /* personal_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personal_body.getVisibility() == View.VISIBLE) {
                    personal_body.setVisibility(View.GONE);
                    personal_header.getChildAt(1).setRotation(-90);
                } else {
                    personal_body.setVisibility(View.VISIBLE);
                    personal_header.getChildAt(1).setRotation(90);
                }
            }
        });

        static_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (static_body.getVisibility() == View.VISIBLE) {
                    static_body.setVisibility(View.GONE);
                    static_header.getChildAt(1).setRotation(-90);
                } else {
                    static_body.setVisibility(View.VISIBLE);
                    static_header.getChildAt(1).setRotation(90);
                }
            }
        });

        cum_driver_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cum_drive_body.getVisibility() == View.VISIBLE) {
                    cum_drive_body.setVisibility(View.GONE);
                    cum_driver_header.getChildAt(1).setRotation(-90);
                } else {
                    cum_drive_body.setVisibility(View.VISIBLE);
                    cum_driver_header.getChildAt(1).setRotation(90);
                }
            }
        });

        bumble_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bumble_body.getVisibility() == View.VISIBLE) {
                    bumble_body.setVisibility(View.GONE);
                    bumble_header.getChildAt(1).setRotation(-90);
                } else {
                    bumble_body.setVisibility(View.VISIBLE);
                    bumble_header.getChildAt(1).setRotation(90);
                }
            }
        });*/
    }

    private void setupback(){
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private SignUp_Services_Adapter adapter;
    private ListView service_list;
    private ArrayList<MemberService> list;
    private List<ArrayList<Order_Service_Info>> services_list;
    private ImageView back_navigation;
    private ArrayList<String> service_ids,service_type_ids;

    private String member_id = "117";
    private String email = "";
    private SharedPreferences settings;
    private ListView bodyguard_recycle,bodyguard_cum_driver_recycle,driver_chauffer_recycle,bumble_recycle,document_recycler,delivery_recycler;
    private SignUp_Services_Adapter b_services_adapter,b_c_services_adapter,driver_services_adapter,bumble_services_adapter,document_services,delivery_adapter;
    RecyclerView.LayoutManager layoutManager;

    private Order_Service_Info bodyguard_selected=null,bodyguard_cum_selected=null,driver_selected=null,bumble_selected=null,document_selected=null;

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getServicesFromServer()
    {
        asyncHttpClient.setConnectTimeout(30000);

        asyncHttpClient.get(SignUp_One.this, Constants.Host_Address + "services/get_services_list/0/tgs_appkey_amin", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("getting services...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e("SERVICES",response);
                    JSONObject data = new JSONObject(response);
                    JSONObject services = data.getJSONObject("data").getJSONObject("services");
                    JSONArray sub_services = data.getJSONObject("data").getJSONArray("sub_services");

                    if (services.has("1")) {
                        String body = services.getString("1");
                        ((TextView)personal_header.getChildAt(0)).setText(body);
                    }else {
                        personal_header.setVisibility(View.GONE);
                    }

                    if (services.has("2")) {
                        String body_cum = services.getString("2");
                        ((TextView)cum_driver_header.getChildAt(0)).setText(body_cum);
                    }else {
                        cum_driver_header.setVisibility(View.GONE);
                    }

                    if (services.has("3")) {
                        String bumble = services.getString("3");
                        ((TextView)bumble_header.getChildAt(0)).setText(bumble);
                    }else {
                        bumble_header.setVisibility(View.GONE);
                    }

                    if (services.has("4")) {
                        String static_security = services.getString("4");
                        String text = static_security.replace("Static ","").replace("static ","");
                        ((TextView)static_header.getChildAt(0)).setText(text);
                    }else {
                        static_header.setVisibility(View.GONE);
                    }

                   /* if (services.has("6")) {
                        String static_security = services.getString("6");
                       // String text = static_security.replace("Static ","").replace("static ","");
                        ((TextView)document_header.getChildAt(0)).setText(static_security);
                    }else {
                        document_header.setVisibility(View.GONE);
                    }*/


                    list = new ArrayList<>();
                    services_list = new ArrayList<>();
                    Iterator<String> keys = services.keys();
                    while (keys.hasNext())
                    {
                        String key = keys.next();
                        Object value = services.get(key);

                        MemberService memberService = new MemberService();
                        memberService.setName(value.toString());
                        memberService.setId(key);
                        memberService.setSelected(false);

                        if (key.equalsIgnoreCase("1"))
                            memberService.setImageDrawable(R.drawable.ic_bodyguard);
                        else if (key.equalsIgnoreCase("2"))
                            memberService.setImageDrawable(R.drawable.ic_bodyguard_cum_driver);
                        else if (key.equalsIgnoreCase("3"))
                            memberService.setImageDrawable(R.drawable.bumble_ride);
                        else if (key.equalsIgnoreCase("4"))
                            memberService.setImageDrawable(R.drawable.driver_icon);

                        list.add(memberService);
                    }

                    ArrayList<Service_Slot> service_bodyguard = new ArrayList<>();
                    ArrayList<Service_Slot> service_bcd = new ArrayList<>();
                    ArrayList<Service_Slot> service_driver = new ArrayList<>();
                    ArrayList<Service_Slot> service_bumble = new ArrayList<>();
                    ArrayList<Service_Slot> service_document = new ArrayList<>();
                    extra_services = new ArrayList<>();

                    for (int i=0;i<sub_services.length();i++)
                    {
                        JSONObject object_ = sub_services.getJSONObject(i);
                        String sub_service_id = object_.getString("id");
                        String service_id = object_.getString("service_id");
                        String service_name = object_.getString("service_name");

                        Service_Slot slot = new Service_Slot();
                        slot.setSlot_id(sub_service_id);
                        slot.setService_id(service_id);

                    /*    if (service_name.contains("Cum")) {
                            service_name.replace("Bodyguard Cum Driver ", "");

                        }
                        else
                        {
                            service_name.replace("Bodyguard ", "");
                            service_name = "BCD "+service_name;
                        }

                        if (service_name.contains("Bumble"))
                        {
                            service_name = service_name.replace("Bumble Ride Bike","").replace("Bumble Ride Car","");
                        }
*/
                        slot.setSlot_name(service_name);
                        slot.setActive(false);


                        if (service_id.equalsIgnoreCase("1"))
                            service_bodyguard.add(slot);
                        else if (service_id.equalsIgnoreCase("2"))
                            service_bcd.add(slot);
                        else if (service_id.equalsIgnoreCase("3"))
                            service_bumble.add(slot);
                        else if (service_id.equalsIgnoreCase("4"))
                            service_driver.add(slot);
                        /*else if (service_id.equalsIgnoreCase("6"))
                            service_document.add(slot);*/
                        else
                            extra_services.add(slot);

                    }

                    services_list.add(ConvertObjectListBodyGuard(service_bodyguard));
                    services_list.add(ConvertObjectList(service_bcd));
                    services_list.add(ConvertObjectList(service_driver));
                    services_list.add(ConvertObjectList(service_bumble));
                    services_list.add(ConvertObjectList(service_document));

                    ArrayList<Order_Service_Info> list1_ = ConvertObjectList(service_bodyguard);
                    ArrayList<Order_Service_Info> list2_ = ConvertObjectList(service_bcd);
                    ArrayList<Order_Service_Info> list3_ = ConvertObjectList(service_driver);
                    ArrayList<Order_Service_Info> list4_ = ConvertObjectList(service_bumble);
                    ArrayList<Order_Service_Info> list6_ = ConvertObjectList(service_document);


                    b_services_adapter = new SignUp_Services_Adapter(list1_,SignUp_One.this);
                    b_c_services_adapter = new SignUp_Services_Adapter(list2_,SignUp_One.this);
                    driver_services_adapter = new SignUp_Services_Adapter(list3_,SignUp_One.this);
                    bumble_services_adapter = new SignUp_Services_Adapter(list4_,SignUp_One.this);
                    document_services = new SignUp_Services_Adapter(list6_,SignUp_One.this);

                    b_services_adapter.setS_code(1);
                    b_c_services_adapter.setS_code(2);
                    driver_services_adapter.setS_code(4);
                    bumble_services_adapter.setS_code(3);
                    document_services.setS_code(6);

                    b_services_adapter.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {
                            if (code==1)
                            {
                                service_ids.add("1");
                                service_type_ids.add(order_service_info.getService_id());
                                bodyguard_selected = order_service_info;
                                b_services_adapter.setSelected_index(pos);
                                b_services_adapter.notifyDataSetChanged();
                            }
                        }
                    });

                    b_c_services_adapter.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {
                            if (code==2)
                            {
                                service_ids.add("2");
                                service_type_ids.add(order_service_info.getService_id());
                                bodyguard_cum_selected = order_service_info;
                                b_c_services_adapter.setSelected_index(pos);
                                b_c_services_adapter.notifyDataSetChanged();
                            }
                        }
                    });

                    driver_services_adapter.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {
                            if (code==4)
                            {
                                service_ids.add("4");
                                service_type_ids.add(order_service_info.getService_id());
                                driver_selected = order_service_info;
                                driver_services_adapter.setSelected_index(pos);
                                driver_services_adapter.notifyDataSetChanged();
                            }
                        }
                    });

                    bumble_services_adapter.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {
                            if (code==3)
                            {
                                service_ids.add("3");
                                service_type_ids.add(order_service_info.getService_id());
                                bumble_selected = order_service_info;
                                bumble_services_adapter.setSelected_index(pos);
                                bumble_services_adapter.notifyDataSetChanged();
                            }
                        }
                    });

                    document_services.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {
                            if (code==6)
                            {
                                service_ids.add("6");
                                service_type_ids.add(order_service_info.getService_id());
                                document_selected = order_service_info;
                                document_services.setSelected_index(pos);
                                document_services.notifyDataSetChanged();
                            }
                        }
                    });


                    bodyguard_recycle.setAdapter(b_services_adapter);
                    bodyguard_cum_driver_recycle.setAdapter(b_c_services_adapter);
                    driver_chauffer_recycle.setAdapter(driver_services_adapter);
                    bumble_recycle.setAdapter(bumble_services_adapter);
                    document_recycler.setAdapter(document_services);


                    setListViewHeightBasedOnItems(bodyguard_recycle);
                    setListViewHeightBasedOnItems(bodyguard_cum_driver_recycle);
                    setListViewHeightBasedOnItems(driver_chauffer_recycle);
                    setListViewHeightBasedOnItems(bumble_recycle);
                    setListViewHeightBasedOnItems(document_recycler);

                    getGajaServicesFromServer();


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e("response fail",response);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    ArrayList<Service_Slot> extra_services;
    private void getGajaServicesFromServer()
    {

        asyncHttpClient.setConnectTimeout(30000);

        asyncHttpClient.get(SignUp_One.this, Constants.Host_Address + "services/get_services_list/1/tgs_appkey_amin", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("getting services...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e("SERVICES",response);
                    JSONObject data = new JSONObject(response);
                    JSONObject services = data.getJSONObject("data").getJSONObject("services");
                    JSONArray sub_services = data.getJSONObject("data").getJSONArray("sub_services");

                    ((TextView)delivery_header.getChildAt(0)).setText("Delivery Services");

                   /* if (services.has("1")) {
                        String body = services.getString("1");
                        ((TextView)personal_header.getChildAt(0)).setText(body);
                    }else {
                        personal_header.setVisibility(View.GONE);
                    }

                    if (services.has("2")) {
                        String body_cum = services.getString("2");
                        ((TextView)cum_driver_header.getChildAt(0)).setText(body_cum);
                    }else {
                        cum_driver_header.setVisibility(View.GONE);
                    }

                    if (services.has("3")) {
                        String bumble = services.getString("3");
                        ((TextView)bumble_header.getChildAt(0)).setText(bumble);
                    }else {
                        bumble_header.setVisibility(View.GONE);
                    }

                    if (services.has("4")) {
                        String static_security = services.getString("4");
                        String text = static_security.replace("Static ","").replace("static ","");
                        ((TextView)static_header.getChildAt(0)).setText(text);
                    }else {
                        static_header.setVisibility(View.GONE);
                    }*/

                   /* if (services.has("6")) {
                        String static_security = services.getString("6");
                       // String text = static_security.replace("Static ","").replace("static ","");
                        ((TextView)document_header.getChildAt(0)).setText(static_security);
                    }else {
                        document_header.setVisibility(View.GONE);
                    }*/


                    list = new ArrayList<>();
                    ArrayList<Service_Slot> service_delivery = new ArrayList<>();
                    services_list = new ArrayList<>();
                    Iterator<String> keys = services.keys();
                    while (keys.hasNext())
                    {
                        String key = keys.next();
                        Object value = services.get(key);

                       /* MemberService memberService = new MemberService();
                        memberService.setName(value.toString());
                        memberService.setId(key);
                        memberService.setSelected(false);
*/
                        Service_Slot slot = new Service_Slot();
                        slot.setSlot_id(key);
                        slot.setSlot_name(value.toString());
                        slot.setActive(false);

                        service_delivery.add(slot);


                    }



                   /* for (int i=0;i<sub_services.length();i++)
                    {
                        JSONObject object_ = sub_services.getJSONObject(i);
                        String sub_service_id = object_.getString("id");
                        String service_id = object_.getString("service_id");
                        String service_name = object_.getString("service_name");

                        Service_Slot slot = new Service_Slot();
                        slot.setSlot_id(sub_service_id);

                    *//*    if (service_name.contains("Cum")) {
                            service_name.replace("Bodyguard Cum Driver ", "");

                        }
                        else
                        {
                            service_name.replace("Bodyguard ", "");
                            service_name = "BCD "+service_name;
                        }

                        if (service_name.contains("Bumble"))
                        {
                            service_name = service_name.replace("Bumble Ride Bike","").replace("Bumble Ride Car","");
                        }
*//*
                        slot.setSlot_name(service_name);
                        slot.setActive(false);


                        if (service_id.equalsIgnoreCase("1"))
                            service_bodyguard.add(slot);
                        else if (service_id.equalsIgnoreCase("2"))
                            service_bcd.add(slot);
                        else if (service_id.equalsIgnoreCase("3"))
                            service_bumble.add(slot);
                        else if (service_id.equalsIgnoreCase("4"))
                            service_driver.add(slot);
                        else if (service_id.equalsIgnoreCase("6"))
                            service_document.add(slot);

                    }*/

                    services_list.add(ConvertObjectListBodyGuard(service_delivery));
                   /* services_list.add(ConvertObjectList(service_bcd));
                    services_list.add(ConvertObjectList(service_driver));
                    services_list.add(ConvertObjectList(service_bumble));
                    services_list.add(ConvertObjectList(service_document));
*/
                    ArrayList<Order_Service_Info> list1_ = ConvertObjectList(service_delivery);
                   /* ArrayList<Order_Service_Info> list2_ = ConvertObjectList(service_bcd);
                    ArrayList<Order_Service_Info> list3_ = ConvertObjectList(service_driver);
                    ArrayList<Order_Service_Info> list4_ = ConvertObjectList(service_bumble);
                    ArrayList<Order_Service_Info> list6_ = ConvertObjectList(service_document);
*/

                    delivery_adapter = new SignUp_Services_Adapter(list1_,SignUp_One.this);
                   /* b_c_services_adapter = new SignUp_Services_Adapter(list2_,SignUp_One.this);
                    driver_services_adapter = new SignUp_Services_Adapter(list3_,SignUp_One.this);
                    bumble_services_adapter = new SignUp_Services_Adapter(list4_,SignUp_One.this);
                    document_services = new SignUp_Services_Adapter(list6_,SignUp_One.this);

                    b_services_adapter.setS_code(1);
                    b_c_services_adapter.setS_code(2);
                    driver_services_adapter.setS_code(4);
                    bumble_services_adapter.setS_code(3);
                */    delivery_adapter.setS_code(-1);

                    delivery_adapter.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {

                            for (int i=0;i<extra_services.size();i++)
                            {
                                if (extra_services.get(i).getService_id().equalsIgnoreCase(order_service_info.getService_id())){
                                    service_ids.add(order_service_info.getService_id());
                                    service_type_ids.add(extra_services.get(i).getSlot_id());
                                    bodyguard_selected = order_service_info;
                                    b_services_adapter.setSelected_index(pos);
                                    b_services_adapter.notifyDataSetChanged();
                                }
                            }

                        }
                    });

                  /*  b_c_services_adapter.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {
                            if (code==2)
                            {
                                service_ids.add("2");
                                service_type_ids.add(order_service_info.getService_id());
                                bodyguard_cum_selected = order_service_info;
                                b_c_services_adapter.setSelected_index(pos);
                                b_c_services_adapter.notifyDataSetChanged();
                            }
                        }
                    });

                    driver_services_adapter.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {
                            if (code==4)
                            {
                                service_ids.add("4");
                                service_type_ids.add(order_service_info.getService_id());
                                driver_selected = order_service_info;
                                driver_services_adapter.setSelected_index(pos);
                                driver_services_adapter.notifyDataSetChanged();
                            }
                        }
                    });

                    bumble_services_adapter.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {
                            if (code==3)
                            {
                                service_ids.add("3");
                                service_type_ids.add(order_service_info.getService_id());
                                bumble_selected = order_service_info;
                                bumble_services_adapter.setSelected_index(pos);
                                bumble_services_adapter.notifyDataSetChanged();
                            }
                        }
                    });

                    document_services.setServiceSelectionCallBack(new ServiceSelectionCallBack() {
                        @Override
                        public void onServiceSelected(Order_Service_Info order_service_info, int code,int pos) {
                            if (code==6)
                            {
                                service_ids.add("6");
                                service_type_ids.add(order_service_info.getService_id());
                                document_selected = order_service_info;
                                document_services.setSelected_index(pos);
                                document_services.notifyDataSetChanged();
                            }
                        }
                    });


                    bodyguard_recycle.setAdapter(b_services_adapter);
                    bodyguard_cum_driver_recycle.setAdapter(b_c_services_adapter);
                    driver_chauffer_recycle.setAdapter(driver_services_adapter);
                    bumble_recycle.setAdapter(bumble_services_adapter);*/
                    delivery_recycler.setAdapter(delivery_adapter);


                    /*setListViewHeightBasedOnItems(bodyguard_recycle);
                    setListViewHeightBasedOnItems(bodyguard_cum_driver_recycle);
                    setListViewHeightBasedOnItems(driver_chauffer_recycle);
                    setListViewHeightBasedOnItems(bumble_recycle);
                  */  setListViewHeightBasedOnItems(delivery_recycler);


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e("response fail",response);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    private ProgressDialog progress;
    private void showProgressDialog(String message)
    {

        progress = new ProgressDialog(SignUp_One.this);
        progress.setMessage(message);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }

    private ArrayList<Order_Service_Info> ConvertObjectListBodyGuard(ArrayList<Service_Slot> list_)
    {
        ArrayList<Order_Service_Info> services = new ArrayList<>();
        Order_Service_Info order_service_info_basic = new Order_Service_Info();
        Order_Service_Info order_service_info_inter = new Order_Service_Info();
        Order_Service_Info order_service_info_prem = new Order_Service_Info();
        for (int i=0;i<list_.size();i++)
        {
            if (list_.get(i).getSlot_name().toLowerCase().contains("basic")) {
                order_service_info_basic.setService_id(list_.get(i).getSlot_id());
                order_service_info_basic.setService_name(list_.get(i).getSlot_name());
                order_service_info_basic.setSelected(-1);

            }
            if (list_.get(i).getSlot_name().toLowerCase().contains("intermediate")) {
                order_service_info_inter.setService_id(list_.get(i).getSlot_id());
                order_service_info_inter.setService_name(list_.get(i).getSlot_name());
                order_service_info_inter.setSelected(-1);

            }
            if (list_.get(i).getSlot_name().toLowerCase().contains("premium")) {
                order_service_info_prem.setService_id(list_.get(i).getSlot_id());
                order_service_info_prem.setService_name(list_.get(i).getSlot_name());
                order_service_info_prem.setSelected(-1);

            }
        }

        services.add(order_service_info_basic);
        services.add(order_service_info_inter);
        services.add(order_service_info_prem);


        return services;
    }


    private ArrayList<Order_Service_Info> ConvertObjectList(ArrayList<Service_Slot> list_)
    {
        ArrayList<Order_Service_Info> services = new ArrayList<>();
        for (int i=0;i<list_.size();i++)
        {
            Order_Service_Info order_service_info = new Order_Service_Info();
            order_service_info.setService_id(list_.get(i).getSlot_id());
            order_service_info.setService_name(list_.get(i).getSlot_name());
            order_service_info.setSelected(-1);

            services.add(order_service_info);
        }

        return services;
    }


    private void collectServices(){
        int selected_services = 0;

        Object[] s_ids =  service_ids.toArray();
        Object[] s_type_ids =  service_type_ids.toArray();

        String[] s_ids_ = new String[s_ids.length];
        String[] s_type_ids_ = new String[s_type_ids.length];

        for (int i=0;i<s_ids.length;i++){
            s_ids_[i] = s_ids[i].toString();
            s_type_ids_[i] = s_type_ids[i].toString();
        }

        if (s_ids.length >0) {

           /* Intent intent = new Intent(SignUp_One.this,SignUp_Two.class);
            intent.putExtra("ids",s_ids_);
            intent.putExtra("type_ids",s_type_ids_);
            startActivity(intent);
            finish();*/

           String service_ids = "";

           for (int i=0;i<s_type_ids_.length;i++){
               service_ids = service_ids + s_type_ids_[i] + "-";
           }


           sharedPreferences.edit().putString("service_id",service_ids).apply();
           saveBasicInfo(s_ids_,s_type_ids_);


        }else {
            Toast.makeText(SignUp_One.this,"Choose Atleast 1 Service",Toast.LENGTH_SHORT).show();
        }

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


    private void saveBasicInfo(String[] ids,String[] type_ids) {
        asyncHttpClient.setConnectTimeout(20000);
        asyncHttpClient.setTimeout(20*1000);

        RequestParams params = new RequestParams();

        String email = sharedPreferences.getString(Constants.PREFS_USER_EMAIL,"");
        String name = sharedPreferences.getString(Constants.PREFS_USER_NAME,"");
        String fullName = sharedPreferences.getString(Constants.PREFS_USER_FULL_NAME,"");
        String number = sharedPreferences.getString(Constants.PREFS_USER_MOBILE,"");
        String image = sharedPreferences.getString(Constants.PREFS_USER_IMAGE,"");

        try {
            String certificate = sharedPreferences.getString(Constants.CERTIFICATE,"");
            Log.e(TAG, "saveBasicInfo: "+certificate);
            params.put("service_id", ids);
            params.put("service_type_id", type_ids);
            params.put("email", email);
            params.put("preferred_name", name);
            params.put("full_name", fullName);
            params.put("phone", "+60"+number);
            params.put("profile_pic", new File(image));
            // params.put("certificate", new FileInputStream(certificate));

        }
        catch (Exception e){
            e.printStackTrace();
        }

        asyncHttpClient.post(SignUp_One.this, Constants.Host_Address + "member_register_new", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("Creating Account");
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
                        String verification_code = jsonObject.getString("otp_code");
                        String mem_id = jsonObject.getString("member_id");
                        String email = jsonObject.getString("email");

                        String json = jsonObject.getJSONObject("docs_to_upload").toString();

                        sharedPreferences.edit().putString(Constants.DOC_LIST_JSON,json).apply();
                        sharedPreferences.edit().putString(Constants.PREFS_CUSTOMER_ID,mem_id).apply();
                        sharedPreferences.edit().putString(Constants.PREFS_USER_EMAIL,email).apply();
                        sharedPreferences.edit().putString("code",verification_code).apply();

                        Intent intent = new Intent(SignUp_One.this,SignUp_Checklist.class);
                        startActivity(intent);


                    } else {
                        String message = jsonObject.getString("message");
                        Toast.makeText(SignUp_One.this,message,Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(SignUp_One.this,"Server Error "+e.getMessage(),Toast.LENGTH_LONG).show();
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
                    Toast.makeText(SignUp_One.this,message,Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();

                    Toast.makeText(SignUp_One.this,"Server Error "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUp_One.this,SignUp_Two.class).putExtra("back",true));
        finish();
    }
}
