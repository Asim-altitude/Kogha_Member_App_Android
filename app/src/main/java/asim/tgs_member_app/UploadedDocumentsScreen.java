package asim.tgs_member_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import asim.tgs_member_app.adapters.DocumentAdapter;
import asim.tgs_member_app.adapters.ServicesAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberDocument;
import asim.tgs_member_app.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;


public class UploadedDocumentsScreen extends AppCompatActivity {


    private Button upload_details;
    Button upload_documnets_btn;
    SharedPreferences settings;
    String mem_id;
    private List<MemberDocument> documents;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploaded_docs);

        setupToolbar();
        settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mem_id = settings.getString(Constants.PREFS_CUSTOMER_ID,"");
        upload_documnets_btn = (Button) findViewById(R.id.upload_documnets_btn);
        listView = (ListView) findViewById(R.id.doc_listview);
        upload_documnets_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (Constants.hasProfessionalImage
                        && Constants.hasDrivingLicense
                        && Constants.hasCarImage)
                {
                    UtilsManager.showAlertMessage(UploadedDocumentsScreen.this,"","Your documents are complete");
                }
                else
                {
                    startActivity(new Intent(UploadedDocumentsScreen.this, Upload_Strength_Picture_Registeration.class));
                }*/
                startActivity(new Intent(UploadedDocumentsScreen.this, Upload_Strength_Picture_Registeration.class));
            }
        });


    }
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.upload_documents);
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

    @Override
    protected void onResume() {
        super.onResume();
        fetchDocuments();
    }

    ProgressDialog progressDialog;
    private void showDialog(){
        progressDialog = new ProgressDialog(UploadedDocumentsScreen.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    AsyncHttpClient httpClient = new AsyncHttpClient();
    private void fetchDocuments()
    {
        httpClient.setConnectTimeout(30000);
        Log.e("url ",Constants.Host_Address + "members/member_documents/"+mem_id+"/tgs_appkey_amin");
        httpClient.get(UploadedDocumentsScreen.this, Constants.Host_Address + "members/member_documents/"+mem_id+"/tgs_appkey_amin",new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    progressDialog.dismiss();
                    String response = new String(responseBody);
                    Log.e("response",response);
                    documents = new ArrayList<MemberDocument>();
                    JSONObject object = new JSONObject(response);
                    JSONArray doc_array = object.getJSONArray("data");
                    if (doc_array==null)
                        return;
                    String doc_id,doc_name,doc_image,uploaded_date;

                    for (int i=0;i<doc_array.length();i++)
                    {
                        JSONObject object1 = doc_array.getJSONObject(i);
                        doc_id = object1.getString("id");
                        doc_name = object1.getString("document");
                        doc_image = object1.getString("document_name");
                        uploaded_date = object1.getString("uploaded_date");

                        if (doc_name.equalsIgnoreCase("car pictures"))
                            Constants.hasCarImage = true;

                        if (doc_name.equalsIgnoreCase("Driving license"))
                            Constants.hasDrivingLicense = true;

                        if (doc_name.equalsIgnoreCase("professional pictures"))
                            Constants.hasProfessionalImage = true;

                        MemberDocument memberDocument = new MemberDocument();
                        memberDocument.setDoc_id(doc_id);
                        memberDocument.setDoc_name(doc_name);
                        memberDocument.setDoc_image(doc_image);
                        memberDocument.setUploaded_date(uploaded_date);


                        documents.add(memberDocument);
                    }

                    DocumentAdapter adapter = new DocumentAdapter(UploadedDocumentsScreen.this, documents);
                    listView.setAdapter(adapter);

                    if (documents.size()==0)
                        ((TextView)findViewById(R.id.document_error_empty)).setVisibility(View.VISIBLE);
                    else
                        ((TextView)findViewById(R.id.document_error_empty)).setVisibility(View.GONE);


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    if (documents.size()==0)
                        ((TextView)findViewById(R.id.document_error_empty)).setVisibility(View.VISIBLE);
                    else
                        ((TextView)findViewById(R.id.document_error_empty)).setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    progressDialog.dismiss();
                    String response = new String(responseBody);
                    Log.e("response",response);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


}
