package asim.tgs_member_app.models;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public class OrderServiceDetail
{
    private String bodyguard_prem,bodyguard_intermediate,bodyguard_basic;
    private String chauffer,cum_driver,remarks,total_hrs,total_amount;

    public OrderServiceDetail(String bodyguard_prem, String bodyguard_intermediate, String bodyguard_basic, String chauffer, String cum_driver, String remarks, String total_hrs, String total_amount) {
        this.bodyguard_prem = bodyguard_prem;
        this.bodyguard_intermediate = bodyguard_intermediate;
        this.bodyguard_basic = bodyguard_basic;
        this.chauffer = chauffer;
        this.cum_driver = cum_driver;
        this.remarks = remarks;
        this.total_hrs = total_hrs;
        this.total_amount = total_amount;
    }

    public String getBodyguard_prem() {
        return bodyguard_prem;
    }

    public void setBodyguard_prem(String bodyguard_prem) {
        this.bodyguard_prem = bodyguard_prem;
    }

    public String getBodyguard_intermediate() {
        return bodyguard_intermediate;
    }

    public void setBodyguard_intermediate(String bodyguard_intermediate) {
        this.bodyguard_intermediate = bodyguard_intermediate;
    }

    public String getBodyguard_basic() {
        return bodyguard_basic;
    }

    public void setBodyguard_basic(String bodyguard_basic) {
        this.bodyguard_basic = bodyguard_basic;
    }

    public String getChauffer() {
        return chauffer;
    }

    public void setChauffer(String chauffer) {
        this.chauffer = chauffer;
    }

    public String getCum_driver() {
        return cum_driver;
    }

    public void setCum_driver(String cum_driver) {
        this.cum_driver = cum_driver;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTotal_hrs() {
        return total_hrs;
    }

    public void setTotal_hrs(String total_hrs) {
        this.total_hrs = total_hrs;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }
}
