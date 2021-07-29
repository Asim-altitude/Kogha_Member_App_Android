package asim.tgs_member_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.HashMap;
import java.util.List;

import asim.tgs_member_app.R;

/**
 * Created by PC-GetRanked on 6/27/2018.
 */

public class ExpandableSideMenu extends BaseExpandableListAdapter
{
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private  boolean isApproved = false;

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public ExpandableSideMenu(Context context, List<String> listDataHeader,
                              HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return _listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader =  convertView
                .findViewById(R.id.lblListHeader);

        lblListHeader.setText(headerTitle);

        ImageView lblListIcon=  convertView
                .findViewById(R.id.item_icon);


        if (isApproved)
            ApprovedMenuIcons(lblListIcon,groupPosition);
        else
            UnapprovedMenuIcons(lblListIcon,groupPosition);


        return convertView;
    }

    private void ApprovedMenuIcons(ImageView lblListIcon,int groupPosition)
    {
        if (groupPosition==0)
        {
            lblListIcon.setImageResource(R.drawable.home);
        }
        else if (groupPosition==1)
        {
            lblListIcon.setImageResource(R.drawable.suggested_jobs);
        }
        else if (groupPosition==2)
        {
            lblListIcon.setImageResource(R.drawable.completed_jobs);
        }
        else if (groupPosition==3)
        {
            lblListIcon.setImageResource(R.drawable.finance);
        }
        else if (groupPosition==4)
        {
            lblListIcon.setImageResource(R.drawable.profile);
        }
        else if (groupPosition==5)
        {
            lblListIcon.setImageResource(R.drawable.notifications);
        }
        else if (groupPosition==6)
        {
            lblListIcon.setImageResource(R.drawable.language);
        }
        else if (groupPosition==7)
        {
            lblListIcon.setImageResource(R.drawable.logout);
        }
        else if (groupPosition==8)
        {
            lblListIcon.setImageResource(R.drawable.languages);
        }
        else if (groupPosition==9)
        {
            lblListIcon.setImageResource(R.drawable.logout);
        }
    }

    private void UnapprovedMenuIcons(ImageView lblListIcon,int groupPosition)
    {
        if (groupPosition==0)
        {
            lblListIcon.setImageResource(R.drawable.profile);
        }
        else if (groupPosition==1)
        {
            lblListIcon.setImageResource(R.drawable.services);
        }
        else if (groupPosition==2)
        {
            lblListIcon.setImageResource(R.drawable.documents);
        }
        else if (groupPosition==3)
        {
            lblListIcon.setImageResource(R.drawable.languages);
        }
        else if (groupPosition==4)
        {
            lblListIcon.setImageResource(R.drawable.logout);
        }
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
