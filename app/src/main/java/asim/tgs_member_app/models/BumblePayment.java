package asim.tgs_member_app.models;


import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by PC-GetRanked on 10/10/2018.
 */

public class BumblePayment extends BaseModel {

    private String order_id;
    private String payment_code;

    public BumblePayment()
    {

    }

    public BumblePayment(String order_id, String payment_code) {
        this.order_id = order_id;
        this.payment_code = payment_code;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPayment_code() {
        return payment_code;
    }

    public void setPayment_code(String payment_code) {
        this.payment_code = payment_code;
    }
}
