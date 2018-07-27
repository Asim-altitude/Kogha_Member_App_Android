package asim.tgs_member_app;

import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import asim.tgs_member_app.adapters.ServicesAdapter;
import asim.tgs_member_app.models.Constants;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;


public class AllowedServices extends AppCompatActivity {

    private ListView services_list;
    private ServicesAdapter serviceadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allowed_services);

        setupToolbar();
        settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mem_id = settings.getString(Constants.PREFS_CUSTOMER_ID,"");

        services_list = (ListView) findViewById(R.id.services_list);
        services = new ArrayList<>();
        serviceadapter = new ServicesAdapter(AllowedServices.this,services);
        services_list.setAdapter(serviceadapter);

        GetMemberServices();
    }
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.services);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    AsyncHttpClient httpClient = new AsyncHttpClient();
    ProgressDialog progressDialog;
    SharedPreferences settings;
    String key,mem_id;
    private void GetMemberServices()
    {
        httpClient.setConnectTimeout(40000);

        settings = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        String member_id = settings.getString(Constants.PREFS_USER_ID,"0");
        key ="tgs_appkey_amin";// settings.getString(Constants.PREFS_ACCESS_TOKEN,"tgs_appkey_amin");
        Log.e("member id",member_id);
        mem_id = member_id;



        Log.e("member services",  Constants.Host_Address + "members/my_services/"+member_id+"/"+key);
        httpClient.get(AllowedServices.this,  Constants.Host_Address + "members/my_services/"+member_id+"/"+key, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(AllowedServices.this);
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
}
