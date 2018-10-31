package asim.tgs_member_app.models;

/**
 * Created by Asim Shahzad on 2/18/2018.
 */
public class MemberLocationObject
{
    private String mem_id;
    private String mem_name;
    private String mem_type;
    private String mem_lat;
    private String mem_lng;
    private String current_job;

    public MemberLocationObject() {
    }

    public MemberLocationObject(String mem_id, String mem_name, String mem_type, String mem_lat, String mem_lng) {
        this.mem_id = mem_id;
        this.mem_name = mem_name;
        this.mem_type = mem_type;
        this.mem_lat = mem_lat;
        this.mem_lng = mem_lng;
    }

    public String getCurrent_job() {
        return current_job;
    }

    public void setCurrent_job(String current_job) {
        this.current_job = current_job;
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

    public String getMem_type() {
        return mem_type;
    }

    public void setMem_type(String mem_type) {
        this.mem_type = mem_type;
    }

    public String getMem_lat() {
        return mem_lat;
    }

    public void setMem_lat(String mem_lat) {
        this.mem_lat = mem_lat;
    }

    public String getMem_lng() {
        return mem_lng;
    }

    public void setMem_lng(String mem_lng) {
        this.mem_lng = mem_lng;
    }
}
