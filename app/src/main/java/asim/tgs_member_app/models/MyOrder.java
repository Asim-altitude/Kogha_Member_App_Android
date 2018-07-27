package asim.tgs_member_app.models;


import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public class MyOrder extends BaseModel
{
    public String getOrder_trip_type() {
        return order_trip_type;
    }

    public void setOrder_trip_type(String order_trip_type) {
        this.order_trip_type = order_trip_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getUniform() {
        return uniform;
    }

    public void setUniform(String uniform) {
        this.uniform = uniform;
    }

    private String order_number;
    private String order_date;
    private String order_time;
    private String order_total;
    private String order_meet_location;
    private String order_destination;
    private String order_bodyguards="0";
    private String order_chauffer="0";
    private String order_trip_type;

    private String status,instructions,uniform;



    public MyOrder(String order_number, String order_date, String order_time, String order_total,
                   String order_meet_location, String order_destination, String order_bodyguards,
                   String order_chauffer,String trip_type,String status, String instructions, String uniform) {

        this.order_number = order_number;
        this.order_date = order_date;
        this.order_time = order_time;
        this.order_total = order_total;
        this.order_meet_location = order_meet_location;
        this.order_destination = order_destination;
        this.order_bodyguards = order_bodyguards;
        this.order_chauffer = order_chauffer;

        this.order_trip_type = trip_type;

        this.status = status;
        this.instructions = instructions;
        this.uniform = uniform;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getOrder_total() {
        return order_total;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public String getOrder_meet_location() {
        return order_meet_location;
    }

    public void setOrder_meet_location(String order_meet_location) {
        this.order_meet_location = order_meet_location;
    }

    public String getOrder_destination() {
        return order_destination;
    }

    public void setOrder_destination(String order_destination) {
        this.order_destination = order_destination;
    }

    public String getOrder_bodyguards() {
        return order_bodyguards;
    }

    public void setOrder_bodyguards(String order_bodyguards) {
        this.order_bodyguards = order_bodyguards;
    }

    public String getOrder_chauffer() {
        return order_chauffer;
    }

    public void setOrder_chauffer(String order_chauffer) {
        this.order_chauffer = order_chauffer;
    }
}
