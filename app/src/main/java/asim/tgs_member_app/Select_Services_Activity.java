package asim.tgs_member_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import asim.tgs_member_app.adapters.SelectServicesAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberService;
import asim.tgs_member_app.models.Service_Slot;
import asim.tgs_member_app.utils.UpdateHeight;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;


public class Select_Services_Activity extends AppCompatActivity  {

    private SelectServicesAdapter adapter;
    private ListView service_list;
    private ArrayList<MemberService> list;
    private List<ArrayList<Service_Slot>> services_list;
    private Button next;
    private ImageView back_navigation;
    private ArrayList<String> service_ids,service_type_ids;

    private String member_id = "117";
    private SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__services_);

       // loadServices();
        list = new ArrayList<>();
        services_list = new ArrayList<>();

        settings = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        member_id = getIntent().getStringExtra("member_id");
        getServicesFromServer();
        service_list = (ListView) findViewById(R.id.services_list);
        next = (Button) findViewById(R.id.btnNext_services);

        back_navigation=(ImageView) findViewById(R.id.back_navigation);
        back_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        service_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    if (list.get(position).isSelected())
                        list.get(position).setSelected(false);
                    else {
                        if (CanSelect())
                            list.get(position).setSelected(true);
                    }


                adapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(service_list);
            }
        });


        setListViewHeightBasedOnChildren(service_list);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selected_services = 0;
                service_ids = new ArrayList<String>();
                service_type_ids = new ArrayList<String>();

                for (int i=0;i<list.size();i++)
                {
                    if (list.get(i).isSelected())
                    {
                        for (int j=0;j<services_list.get(i).size();j++)
                        {
                            if (services_list.get(i).get(j).isActive()) {
                                selected_services++;
                                service_ids.add(services_list.get(i).get(j).getSlot_id());
                                service_type_ids.add(list.get(i).getId());
                            }
                        }
                    }
                }

                Object[] s_ids = service_ids.toArray();
                Object[] s_type_ids = service_type_ids.toArray();


                Register_services(s_ids,s_type_ids);
/*

               if (CanProceed()) {
                   startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                   finish();
               }
                else
                    UtilsManager.showAlertMessage(Select_Services_Activity.this,"","Please choose at least one service.");
*/


            }
        });

      //  getServicesFromServer();

    }

    private boolean CanSelect() {
        int selected_num = 0;

        for (int i=0;i<list.size();i++)
        {
            if (list.get(i).isSelected())
                selected_num++;
        }

        if (selected_num==3)
            return false;
        else
            return true;
    }

    private boolean CanProceed() {
        int selected_num = 0;

        for (int i=0;i<list.size();i++)
        {
            if (list.get(i).isSelected()) {
                selected_num++;
            }
        }

        if (selected_num>0)
            return true;
        else
            return false;
    }

    private void loadServices()
    {
        list = new ArrayList<>();
        services_list = new ArrayList<>();

        MemberService memberService = new MemberService();
        memberService.setName("Bodyguard");
        memberService.setImageDrawable(R.drawable.ic_bodyguard);

        ArrayList<Service_Slot> service_bodyguard = new ArrayList<>();

        Service_Slot slot = new Service_Slot();
        slot.setSlot_name("Premium");
        slot.setActive(false);
        slot.setSlot_id("1");

        Service_Slot slot2 = new Service_Slot();
        slot2.setSlot_name("Basic");
        slot2.setActive(false);
        slot2.setSlot_id("2");

        Service_Slot slot3 = new Service_Slot();
        slot3.setSlot_name("Intermediate");
        slot3.setActive(false);
        slot3.setSlot_id("3");

        service_bodyguard.add(slot);
        service_bodyguard.add(slot2);
        service_bodyguard.add(slot3);

        services_list.add(service_bodyguard);

        list.add(memberService);

        MemberService memberService1 = new MemberService();
        memberService1.setName("Bodyguard cum driver");
        memberService1.setImageDrawable(R.drawable.bodyguard_cum_driver);


        ArrayList<Service_Slot> service_bcd = new ArrayList<>();

        Service_Slot slot_1 = new Service_Slot();
        slot_1.setSlot_name("Premium");
        slot_1.setActive(false);
        slot_1.setSlot_id("1");

        Service_Slot slot_2 = new Service_Slot();
        slot_2.setSlot_name("Basic");
        slot_2.setActive(false);
        slot_2.setSlot_id("2");

        Service_Slot slot_3 = new Service_Slot();
        slot_3.setSlot_name("Intermediate");
        slot_3.setActive(false);
        slot_3.setSlot_id("3");

        service_bcd.add(slot_1);
        service_bcd.add(slot_2);
        service_bcd.add(slot_3);

        services_list.add(service_bcd);

        list.add(memberService1);

        MemberService memberService2 = new MemberService();
        memberService2.setName("Driver");
        memberService2.setImageDrawable(R.drawable.driver_icon);

        ArrayList<Service_Slot> service_driver = new ArrayList<>();

        Service_Slot slot_dr_1 = new Service_Slot();
        slot_dr_1.setSlot_name("Driver");
        slot_dr_1.setActive(false);
        slot_dr_1.setSlot_id("1");


        service_driver.add(slot_dr_1);

        services_list.add(service_driver);

        list.add(memberService2);

        MemberService memberService3 = new MemberService();
        memberService3.setName("Bumble Ride");
        memberService3.setImageDrawable(R.drawable.bumble_ride);

        list.add(memberService3);

        ArrayList<Service_Slot> service_bumble = new ArrayList<>();

        Service_Slot slot_bm_1 = new Service_Slot();
        slot_bm_1.setSlot_name("BumbleRide");
        slot_bm_1.setActive(false);
        slot_bm_1.setSlot_id("1");


        service_bumble.add(slot_bm_1);

        services_list.add(service_bumble);
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

    private ProgressDialog progressDialog;
    private void showProgress(String message)
    {
        progressDialog = new ProgressDialog(Select_Services_Activity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgress()
    {
        if (progressDialog!=null)
            progressDialog.dismiss();
    }


    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getServicesFromServer()
    {
        asyncHttpClient.setConnectTimeout(30000);

        asyncHttpClient.get(Select_Services_Activity.this, Constants.Host_Address + "services/get_services_list/tgs_appkey_amin", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgress("Loading services");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideProgress();
                    String response = new String(responseBody);
                    Log.e("response success",response);
                    JSONObject data = new JSONObject(response);
                    JSONObject services = data.getJSONObject("data").getJSONObject("services");
                    JSONArray sub_services = data.getJSONObject("data").getJSONArray("sub_services");
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
                    for (int i=0;i<sub_services.length();i++)
                    {
                        JSONObject object_ = sub_services.getJSONObject(i);
                        String sub_service_id = object_.getString("id");
                        String service_id = object_.getString("service_id");
                        String service_name = object_.getString("service_name");

                        Service_Slot slot = new Service_Slot();
                        slot.setSlot_id(sub_service_id);

                        if (service_name.contains("Driver cum Bodyguard")) {
                            service_name.replace("Driver cum Bodyguard", "");
                            service_name = "BCD "+service_name;
                        }

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

                    }

                    services_list.add(service_bodyguard);
                    services_list.add(service_bcd);
                    services_list.add(service_driver);
                    services_list.add(service_bumble);


                    adapter = new SelectServicesAdapter(getApplicationContext(),list,services_list);
                    adapter.setListener(new UpdateHeight() {
                        @Override
                        public void updateheight() {
                            setListViewHeightBasedOnChildren(service_list);
                        }
                    });

                    service_list.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(service_list);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideProgress();
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




    private void Register_services(Object[] service_id,Object[] service_type_id)
    {
        asyncHttpClient.setConnectTimeout(30000);

        RequestParams params = new RequestParams();
        params.put("service_id",service_id);
        params.put("service_type_id",service_type_id);
        params.put("key","tgs_appkey_amin");
        params.put("member_id",member_id);

        asyncHttpClient.post(Select_Services_Activity.this, Constants.Host_Address + "members/register_services",params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgress("Registering services");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideProgress();
                    String response = new String(responseBody);
                    Log.e("response ",response);
                    JSONObject data = new JSONObject(response);
                    if (data.getString("status").equalsIgnoreCase("success"))
                    {
                        startActivity(new Intent(Select_Services_Activity.this,PreferredLanguage.class));
                        Toast.makeText(Select_Services_Activity.this,"Services registered successfully",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(Select_Services_Activity.this,"No member found.",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Select_Services_Activity.this,PreferredLanguage.class));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideProgress();
                    String response = new String(responseBody);
                    Log.e("response ",response);

                    UtilsManager.showAlertMessage(Select_Services_Activity.this,"","Could not register services");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }


}
