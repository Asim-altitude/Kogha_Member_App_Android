package asim.tgs_member_app.models;


import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public class Member extends BaseModel
{

    private String mem_id;
    private String mem_name;
    private String mem_passport;
    private String mem_image;
    private String mem_lat;
    private String mem_lon;
    private String mem_email;
    private String mem_dob;
    private String mem_address;
   // private String mem_id;


    public Member() {
    }

    public Member(String mem_id, String mem_name, String mem_passport, String mem_image, String mem_lat, String mem_lon, String mem_email, String mem_dob, String mem_address) {
        this.mem_id = mem_id;
        this.mem_name = mem_name;
        this.mem_passport = mem_passport;
        this.mem_image = mem_image;
        this.mem_lat = mem_lat;
        this.mem_lon = mem_lon;
        this.mem_email = mem_email;
        this.mem_dob = mem_dob;
        this.mem_address = mem_address;
    }


    public String getMem_id() {
        return mem_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public String getMem_name() {
        return mem_name;
    }

    public void setMem_name(String mem_name) {
        this.mem_name = mem_name;
    }

    public String getMem_passport() {
        return mem_passport;
    }

    public void setMem_passport(String mem_passport) {
        this.mem_passport = mem_passport;
    }

    public String getMem_image() {
        return mem_image;
    }

    public void setMem_image(String mem_image) {
        this.mem_image = mem_image;
    }

    public String getMem_lat() {
        return mem_lat;
    }

    public void setMem_lat(String mem_lat) {
        this.mem_lat = mem_lat;
    }

    public String getMem_lon() {
        return mem_lon;
    }

    public void setMem_lon(String mem_lon) {
        this.mem_lon = mem_lon;
    }

    public String getMem_email() {
        return mem_email;
    }

    public void setMem_email(String mem_email) {
        this.mem_email = mem_email;
    }

    public String getMem_dob() {
        return mem_dob;
    }

    public void setMem_dob(String mem_dob) {
        this.mem_dob = mem_dob;
    }

    public String getMem_address() {
        return mem_address;
    }

    public void setMem_address(String mem_address) {
        this.mem_address = mem_address;
    }
}
