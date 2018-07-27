package asim.tgs_member_app.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Sohaib on 6/9/2017.
 */

public class LocationsDTO {
    public LatLng origin_location;
    public String origin_address="";
    public LatLng dest_location;
    public String dest_address="";
    public boolean check=false;

    private static LocationsDTO instance;;

    public static LocationsDTO getInstance(){
        if(instance==null){
            instance = new LocationsDTO();
        }
        return instance;
    }
    private LocationsDTO(){

    }
}
