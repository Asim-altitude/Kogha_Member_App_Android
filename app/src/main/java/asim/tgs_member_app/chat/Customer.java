package asim.tgs_member_app.chat;


import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by PC-GetRanked on 3/13/2018.
 */

public class Customer extends BaseModel {
    private String c_name,c_id,c_image,c_lat,c_lng;

    public Customer()
    {

    }

    public Customer(String c_name, String c_id, String c_image, String c_lat, String c_lng) {
        this.c_name = c_name;
        this.c_id = c_id;
        this.c_image = c_image;
        this.c_lat = c_lat;
        this.c_lng = c_lng;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getC_image() {
        return c_image;
    }

    public void setC_image(String c_image) {
        this.c_image = c_image;
    }

    public String getC_lat() {
        return c_lat;
    }

    public void setC_lat(String c_lat) {
        this.c_lat = c_lat;
    }

    public String getC_lng() {
        return c_lng;
    }

    public void setC_lng(String c_lng) {
        this.c_lng = c_lng;
    }
}
