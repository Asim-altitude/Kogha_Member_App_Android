package asim.tgs_member_app.restclient;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public interface ServiceResponseListner {
    void onResultsFound(String json, String tag);
}
