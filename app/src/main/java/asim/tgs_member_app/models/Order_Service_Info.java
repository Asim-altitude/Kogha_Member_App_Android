package asim.tgs_member_app.models;


import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by PC-GetRanked on 8/27/2018.
 */

public class Order_Service_Info extends BaseModel {

    private String service_id,service_type_id,service_name;
    private int quantity;
    private int selected = -1;

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getService_type_id() {
        return service_type_id;
    }

    public void setService_type_id(String service_type_id) {
        this.service_type_id = service_type_id;
    }
}
