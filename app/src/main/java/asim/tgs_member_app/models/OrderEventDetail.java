package asim.tgs_member_app.models;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public class OrderEventDetail
{
    private String meet_location;
    private String destination;
    private String ord_date;
    private String meet_date;
    private String order_total;

    public OrderEventDetail(String meet_location, String destination, String ord_date, String meet_date) {
        this.meet_location = meet_location;
        this.destination = destination;
        this.ord_date = ord_date;
        this.meet_date = meet_date;
    }


    public String getOrder_total() {
        return order_total;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public OrderEventDetail(String meet_location, String destination, String ord_date, String meet_date, String order_total) {

        this.meet_location = meet_location;
        this.destination = destination;
        this.ord_date = ord_date;
        this.meet_date = meet_date;
        this.order_total = order_total;
    }

    public String getMeet_location() {
        return meet_location;
    }

    public void setMeet_location(String meet_location) {
        this.meet_location = meet_location;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrd_date() {
        return ord_date;
    }

    public void setOrd_date(String ord_date) {
        this.ord_date = ord_date;
    }

    public String getMeet_date() {
        return meet_date;
    }

    public void setMeet_date(String meet_date) {
        this.meet_date = meet_date;
    }
}
