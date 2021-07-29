package asim.tgs_member_app.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import asim.tgs_member_app.R;

/**
 * Created by PC-GetRanked on 10/8/2018.
 */

public class MissingDocumentAdapter extends RecyclerView.Adapter<MissingDocumentAdapter.MDocViewHolder>
{
    private Context context;
    private List<String> docs_list;

    public MissingDocumentAdapter(Context context, List<String> docs_list) {
        this.context = context;
        this.docs_list = docs_list;
    }

    @NonNull
    @Override
    public MDocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout_view = LayoutInflater.from(context).inflate(R.layout.missing_doc_item,parent,false);
        MDocViewHolder viewHolder = new MDocViewHolder(layout_view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MDocViewHolder holder, int position) {
        holder.doc_name.setText(docs_list.get(position));
    }

    @Override
    public int getItemCount() {
        return docs_list.size();
    }

    class MDocViewHolder extends RecyclerView.ViewHolder
    {
        TextView doc_name;
        public MDocViewHolder(View itemView) {
            super(itemView);
            doc_name = itemView.findViewById(R.id.doc_name);
        }
    }
}
