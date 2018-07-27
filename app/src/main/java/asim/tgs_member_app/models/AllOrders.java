package asim.tgs_member_app.models;


import java.util.ArrayList;

import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by Asim Shahzad on 11/29/2017.
 */
public class AllOrders extends BaseModel
{
    private ArrayList<ArrayList<MOServiceTypeList>> orders;

    public AllOrders()
    {
        orders = new ArrayList<>();
    }

    public ArrayList<ArrayList<MOServiceTypeList>> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<ArrayList<MOServiceTypeList>> orders) {
        this.orders = orders;
    }

    public ArrayList<MOServiceTypeList> getOrder(int index)
    {
       return orders.get(index);
    }


}
