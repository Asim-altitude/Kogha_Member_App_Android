package asim.tgs_member_app.utils;

import asim.tgs_member_app.models.Order_Service_Info;

/**
 * Created by PC-GetRanked on 9/14/2018.
 */

public interface ServiceSelectionCallBack
{
    void onServiceSelected(Order_Service_Info order_service_info,int code,int pos);
}
