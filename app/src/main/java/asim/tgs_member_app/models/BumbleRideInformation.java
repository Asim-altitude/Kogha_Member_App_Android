package asim.tgs_member_app.models;

import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by PC-GetRanked on 9/5/2018.
 */

public class BumbleRideInformation extends BaseModel
{
    private String customer_id,member_id,order_id,updated_by,cash_amount;
    private String ride_status;
    private String payment_code;

    public String getCash_amount() {
        return cash_amount;
    }

    public void setCash_amount(String cash_amount) {
        this.cash_amount = cash_amount;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public BumbleRideInformation() {

    }

    public BumbleRideInformation(String customer_id, String member_id, String order_id, String ride_status) {
        this.customer_id = customer_id;
        this.member_id = member_id;
        this.order_id = order_id;
        this.ride_status = ride_status;
        this.payment_code = "0";
    }

    public String getPayment_code() {
        return payment_code;
    }

    public void setPayment_code(String payment_code) {
        this.payment_code = payment_code;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getRide_status() {
        return ride_status;
    }

    public void setRide_status(String ride_status) {
        this.ride_status = ride_status;
    }
}
