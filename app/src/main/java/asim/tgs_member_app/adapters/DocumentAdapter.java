package asim.tgs_member_app.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import asim.tgs_member_app.LoginActivity;
import asim.tgs_member_app.R;
import asim.tgs_member_app.VerificationActivity;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.MemberDocument;
import asim.tgs_member_app.utils.TouchImageView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by PC-GetRanked on 4/5/2018.
 */

public class DocumentAdapter extends BaseAdapter {

    private Context context;
    private List<MemberDocument> documents;
    private LayoutInflater inflater;

    public DocumentAdapter(Context context, List<MemberDocument> documents)
    {
        this.documents = documents;
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return documents.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView==null)
            convertView = inflater.inflate(R.layout.my_document_item_layout,null);

        TextView doc_name = (TextView) convertView.findViewById(R.id.document_name);
        TextView delete_doc = convertView.findViewById(R.id.delete_doc);
        ImageView doc_image = (ImageView) convertView.findViewById(R.id.document_image);
        TextView document_uploaded_date = (TextView) convertView.findViewById(R.id.document_uploaded_date);
        doc_name.setText(documents.get(position).getDoc_name());
        Glide.with(context).load(documents.get(position).getDoc_image()).placeholder(R.drawable.loading_).into(doc_image);

        doc_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openImageDialog(position);
            }
        });
        document_uploaded_date.setText(documents.get(position).getUploaded_date());

        delete_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDoc(position);
            }
        });

        return convertView;
    }

    Dialog dialog;
    TouchImageView imageView;
    private void openImageDialog(int position) {
        dialog = new Dialog(context, R.style.DialogTheme);
        imageView = new TouchImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(500,500));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setPadding(8, 8, 8, 8);
        imageView.setImageResource(R.drawable.loading);
        imageView.setBackgroundColor(Color.BLACK);

        Glide.with(context).load(documents.get(position).getDoc_image()).into(imageView);

        dialog.setContentView(imageView);

        dialog.show();
    }

    AsyncHttpClient httpClient = new AsyncHttpClient();
    ProgressDialog progressDialog;
    private void showDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void hideLoader()
    {
        progressDialog.dismiss();
    }

    private void DeleteDoc(final int pos)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);
        String mem_id = prefs.getString(Constants.PREFS_USER_ID,"20");

        String[] name_ =documents.get(pos).getDoc_image().split(Pattern.quote("/"));
        httpClient.setConnectTimeout(30000);
        RequestParams params = new RequestParams();
        params.put("document_name",name_[name_.length-1]);
        params.put("document_id", documents.get(pos).getDoc_id());
        params.put("key", "tgs_appkey_amin");
        params.put("member_id",mem_id);

        httpClient.post(context, Constants.Host_Address + "members/delete_member_document", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideLoader();
                    String response = new String(responseBody);
                    JSONObject responseObj = new JSONObject(response);
                    Log.e("response",response);
                    if (responseObj.getString("status").equalsIgnoreCase("success"))
                    {
                       // startActivity(new Intent(VerificationActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                       // finish();
                        documents.remove(pos);
                        notifyDataSetChanged();
                        Toast.makeText(context,"Document deleted successfully",Toast.LENGTH_LONG).show();
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
                    hideLoader();
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
