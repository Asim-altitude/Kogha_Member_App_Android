package asim.tgs_member_app.finance;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.LedgerItem;

public class CreditHistoryAdapter extends BaseAdapter
{

    private Context context;
    private List<LedgerItem> ledgerItemList;

    public CreditHistoryAdapter(Context context, List<LedgerItem> ledgerItemList) {
        this.context = context;
        this.ledgerItemList = ledgerItemList;
    }

    @Override
    public int getCount() {
        return ledgerItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.credit_history_item,null);

        TextView amount = convertView.findViewById(R.id.transaction_amount_paid);
        TextView date = convertView.findViewById(R.id.transaction_date);
        TextView desc = convertView.findViewById(R.id.desc);
        TextView reference = convertView.findViewById(R.id.pay_id);
        ImageView in_out = convertView.findViewById(R.id.transaction_in_out_image);

        if (ledgerItemList.get(position).getIn_out().equalsIgnoreCase("IN")){
            in_out.setRotation(-90);
            in_out.setColorFilter(ContextCompat.getColor(context,R.color.green));
        }else {
            in_out.setRotation(90);
            in_out.setColorFilter(ContextCompat.getColor(context,R.color.reddish));
        }

        reference.setText(ledgerItemList.get(position).getPay_id());
        date.setText(ledgerItemList.get(position).getDate());
        amount.setText("RM"+ledgerItemList.get(position).getAmount());
        desc.setText(ledgerItemList.get(position).getDesc());

        return convertView;
    }
}
