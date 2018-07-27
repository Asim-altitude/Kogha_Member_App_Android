package asim.tgs_member_app.models;

import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by PC-GetRanked on 3/27/2018.
 */

public class CompletedJobObject extends BaseModel {

    private String order_id
            ,meet_loc,destination,datetime_ordered,
            datetime_meet,order_total,
            total_distance,status,instructions,no_of_hours,member_share,
            order_item_id;
    private String booking_type,customer_id,datetime_accepted;
    private boolean show_options = true;
    private String job_starts_in = "not started yet";
    private boolean isChatEnabled = false;
    private String server_time;
    private String customer_name,customer_image;

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_image() {
        return customer_image;
    }

    public void setCustomer_image(String customer_image) {
        this.customer_image = customer_image;
    }

    public String getServer_time() {
        return server_time;
    }

    public void setServer_time(String server_time) {
        this.server_time = server_time;
    }

    public String getDatetime_accepted() {
        return datetime_accepted;
    }

    public void setDatetime_accepted(String datetime_accepted) {
        this.datetime_accepted = datetime_accepted;
    }

    public boolean isChatEnabled() {
        return isChatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        isChatEnabled = chatEnabled;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(String order_item_id) {
        this.order_item_id = order_item_id;
    }

    public String getNo_of_hours() {
        return no_of_hours;
    }

    public void setNo_of_hours(String no_of_hours) {
        this.no_of_hours = no_of_hours;
    }

    public String getMember_share() {
        return member_share;
    }

    public void setMember_share(String member_share) {
        this.member_share = member_share;
    }

    public String getJob_starts_in() {
        return job_starts_in;
    }

    public void setJob_starts_in(String job_starts_in) {
        this.job_starts_in = job_starts_in;
    }

    public boolean isShow_options() {
        return show_options;
    }

    public void setShow_options(boolean show_options) {
        this.show_options = show_options;
    }

    public CompletedJobObject()
    {}

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMeet_loc() {
        return meet_loc;
    }

    public void setMeet_loc(String meet_loc) {
        this.meet_loc = meet_loc;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDatetime_ordered() {
        return datetime_ordered;
    }

    public void setDatetime_ordered(String datetime_ordered) {
        this.datetime_ordered = datetime_ordered;
    }

    public String getDatetime_meet() {
        return datetime_meet;
    }

    public void setDatetime_meet(String datetime_meet) {
        this.datetime_meet = datetime_meet;
    }

    public String getOrder_total() {
        return order_total;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public String getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(String total_distance) {
        this.total_distance = total_distance;
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
}
