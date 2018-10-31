package asim.tgs_member_app.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.Upload_Strength_Picture_Registeration;
import asim.tgs_member_app.adapters.DocumentAdapter;
import asim.tgs_member_app.adapters.MissingDocumentAdapter;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberDocument;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PC-GetRanked on 10/2/2018.
 */

public class MissingDocuments extends Fragment
{
    private String[] all_docs_name = {"Car grant pic","Car interier","Car insurance Pic","IC_Passport Copy","ID Card"
            ,"Professional Pictures","Driving license","Car Pictures"};
    private int[] found_indexes = {-1,-1,-1,-1,-1,-1,-1,-1};
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
                dos.add(all_docs_name[i]);
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
    SharedPreferences settings;
    String mem_id;
    Button upload_document_btn;
    List<String> dos;
    RecyclerView missing_recycler;
    LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.missing_docs_frame,container,false);

        missing_recycler = rootView.findViewById(R.id.missing_docs_list);
        settings = getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mem_id = settings.getString(Constants.PREFS_USER_ID,"");
        editor = settings.edit();
        missing_docs_names = rootView.findViewById(R.id.missing_docs_names);
        upload_document_btn = rootView.findViewById(R.id.upload_documnets_btn);

        upload_document_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Upload_Strength_Picture_Registeration.class));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        missing_docs = "";
        if (dos!=null)
            dos.clear();
        fetchDocuments();
    }



    AsyncHttpClient httpClient = new AsyncHttpClient();
    private void fetchDocuments()
    {
        httpClient.setConnectTimeout(30000);
        Log.e("url ", Constants.Host_Address + "members/member_documents/"+mem_id+"/tgs_appkey_amin");
        httpClient.get(getContext(), Constants.Host_Address + "members/member_documents/"+mem_id+"/tgs_appkey_amin",new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    dos = new ArrayList<String>();
                    String response = new String(responseBody);
                    Log.e("response",response);
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

                    }

                    String mis_doc = getMissingDocs();
                    if (!IsSomethingMissing())
                    {
                        missing_docs_names.setText("Your documents are complete");
                        missing_docs_names.setVisibility(View.GONE);
                        upload_document_btn.setVisibility(View.GONE);

                        layoutManager = new LinearLayoutManager(getContext());
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        missing_recycler.setLayoutManager(layoutManager);
                        MissingDocumentAdapter documentAdapter = new MissingDocumentAdapter(getContext(),dos);
                        missing_recycler.setAdapter(documentAdapter);

                    }
                    else {

                        if (!mis_doc.isEmpty()) {
                            missing_docs_names.setText(mis_doc + " are missing");
                            missing_docs_names.setVisibility(View.VISIBLE);
                        } else {
                            missing_docs_names.setVisibility(View.GONE);
                        }

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
