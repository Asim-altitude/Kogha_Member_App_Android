package asim.tgs_member_app.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.MemberDocument;
import asim.tgs_member_app.utils.TouchImageView;

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
        ImageView doc_image = (ImageView) convertView.findViewById(R.id.document_image);
        TextView document_uploaded_date = (TextView) convertView.findViewById(R.id.document_uploaded_date);
        doc_name.setText(documents.get(position).getDoc_name());
        Glide.with(context).load(documents.get(position).getDoc_image()).placeholder(R.drawable.loading_).into(doc_image);

        doc_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageDialog(position);
            }
        });
        document_uploaded_date.setText(documents.get(position).getUploaded_date());

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
}
