package asim.tgs_member_app.models;

import java.util.List;

import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by PC-GetRanked on 3/27/2018.
 */

public class CompletedJobObject extends BaseModel {

    private String order_id
            ,meet_loc,destination,datetime_ordered,
            datetime_meet,order_total,item_desc,delivery_person,doc_image,item_type,
            total_distance,status,instructions,no_of_hours,member_share,main_service_name,sub_services,sub_prices,
            order_item_id;
    private String booking_type,customer_id,datetime_accepted,datetime_started;
    private boolean show_options = true;
    private String job_starts_in = "not started yet";
    private boolean isChatEnabled = false;
    private String server_time;
    private String customer_name,customer_image,completed_time,uniform_id;
    private List<Order_Service_Info> order_service_infoList;

    public String getUniform_id() {
        return uniform_id;

    }

    public void setUniform_id(String uniform_id) {
        this.uniform_id = uniform_id;
    }

    public String getCompleted_Time() {
        return completed_time;
    }

    public void setCompleted_Time(String completed_time) {
        this.completed_time = completed_time;
    }

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

    public String getDatetime_started() {
        return datetime_started;
    }

    public void setDatetime_started(String datetime_started) {
        this.datetime_started = datetime_started;
    }

    public String getCompleted_time() {
        return completed_time;
    }

    public void setCompleted_time(String completed_time) {
        this.completed_time = completed_time;
    }

    public String getItem_desc() {
        return item_desc;
    }

    public void setItem_desc(String item_desc) {
        this.item_desc = item_desc;
    }

    public String getDelivery_person() {
        return delivery_person;
    }

    public void setDelivery_person(String delivery_person) {
        this.delivery_person = delivery_person;
    }

    public String getDoc_image() {
        return doc_image;
    }

    public void setDoc_image(String doc_image) {
        this.doc_image = doc_image;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    public String getMain_service_name() {
        return main_service_name;
    }

    public void setMain_service_name(String main_service_name) {
        this.main_service_name = main_service_name;
    }

    public String getSub_services() {
        return sub_services;
    }

    public void setSub_services(String sub_services) {
        this.sub_services = sub_services;
    }

    public String getSub_prices() {
        return sub_prices;
    }

    public void setSub_prices(String sub_prices) {
        this.sub_prices = sub_prices;
    }


    public List<Order_Service_Info> getOrder_service_infoList() {
        return order_service_infoList;
    }

    public void setOrder_service_infoList(List<Order_Service_Info> order_service_infoList) {
        this.order_service_infoList = order_service_infoList;
    }
}
