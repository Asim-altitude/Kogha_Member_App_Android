package asim.tgs_member_app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.Upload_Strength_Picture_Registeration;
import asim.tgs_member_app.UploadedDocumentsScreen;
import asim.tgs_member_app.adapters.DocumentAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberDocument;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PC-GetRanked on 10/2/2018.
 */

public class DocumnetFrame extends Fragment
{
    private Button upload_details;
    Button upload_documnets_btn;
    SharedPreferences settings;
    String mem_id;
    private List<MemberDocument> documents;
    private ListView listView;
    private String[] all_docs_name = {"Car grant pic","Car interier","Car insurance Pic","IC_Passport Copy","ID Card"
            ,"Professional Pictures","Driving license","Car Pictures"};
    private int[] found_indexes = {-1,-1,-1,-1,-1,-1,-1,-1};

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private void matchDocName(String name)
    {
        for (int i=0;i<all_docs_name.length;i++)
        {
            if (name.equalsIgnoreCase(all_docs_name[i]))
            {
                found_indexes[i] = i;

                switch (i)
                {
                    case 0:
                        editor.putString(Constants.GRANT_DOC,"done");
                        break;
                    case 1:
                        editor.putString(Constants.INTERIOR_DOC,"done");
                        break;
                    case 2:
                        editor.putString(Constants.INSURANCE_DOC,"done");
                        break;
                    case 3:
                        editor.putString(Constants.PASSPORT_DOC,"done");
                        break;
                    case 4:
                        editor.putString(Constants.IC_CARD_DOC,"done");
                        break;
                    case 5:
                        editor.putString(Constants.STRENGTH_DOC,"done");
                        break;
                    case 6:
                        editor.putString(Constants.DRIVING_LICENCE_DOC,"done");
                        break;
                    case 7:
                        editor.putString(Constants.CAR_DOC,"done");
                        break;
                }
            }
        }

        editor.apply();
    }
    String missing_docs = "";
    private String getMissingDocs()
    {

        for (int i=0;i<found_indexes.length;i++)
        {
            if (found_indexes[i]==-1)
            {
                missing_docs = missing_docs + all_docs_name[i] + ",";
            }
        }

        return missing_docs;
    }

    private boolean IsSomethingMissing()
    {
        boolean missing = false;

        for (int i=0;i<found_indexes.length;i++)
        {
            if (found_indexes[i]==-1)
                missing = true;
        }

        return  missing;
    }


    TextView missing_docs_names;
    TextView empty_message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.uploaded_docs,container,false);
        sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        empty_message = rootView.findViewById(R.id.document_error_empty);

        settings = getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mem_id = settings.getString(Constants.PREFS_USER_ID,"");
        upload_documnets_btn = (Button) rootView.findViewById(R.id.upload_documnets_btn);
        listView = (ListView) rootView.findViewById(R.id.doc_listview);
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
                startActivity(new Intent(getContext(), Upload_Strength_Picture_Registeration.class));
            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchDocuments();
    }


    ProgressDialog progressDialog;
    private void showDialog(){
        progressDialog = new ProgressDialog(getContext());
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
        httpClient.get(getContext(), Constants.Host_Address + "members/member_documents/"+mem_id+"/tgs_appkey_amin",new AsyncHttpResponseHandler() {

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
                    documents = new ArrayList<>();
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

                        matchDocName(doc_name);

                        documents.add(memberDocument);
                    }

                    DocumentAdapter adapter = new DocumentAdapter(getContext(), documents);
                    listView.setAdapter(adapter);

                  /*  String mis_doc = getMissingDocs();
                    if (!IsSomethingMissing())
                    {
                        missing_docs_names.setText("Your documents are complete");
                        missing_docs_names.setVisibility(View.VISIBLE);
                        upload_documnets_btn.setVisibility(View.GONE);

                    }
                    else {

                        if (!mis_doc.isEmpty()) {
                            missing_docs_names.setText(mis_doc + " are missing");
                            missing_docs_names.setVisibility(View.VISIBLE);
                        } else {
                            missing_docs_names.setVisibility(View.GONE);
                        }

                    }*/

                    if (documents.size()==0)
                        empty_message.setVisibility(View.VISIBLE);
                    else
                        empty_message.setVisibility(View.GONE);


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    if (documents.size()==0)
                        empty_message.setVisibility(View.VISIBLE);
                    else
                        empty_message.setVisibility(View.GONE);

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
