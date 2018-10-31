package asim.tgs_member_app.models;


import com.google.android.gms.maps.model.LatLng;

import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by PC-GetRanked on 9/4/2018.
 */

public class Ride extends BaseModel {

    public Ride() {

    }

    private String pickup_loc,destination_loc;
    private RideStatus rideStatus;

    public RideStatus getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(RideStatus rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getPickup_loc() {
        return pickup_loc;
    }

    public void setPickup_loc(String pickup_loc) {
        this.pickup_loc = pickup_loc;
    }

    public String getDestination_loc() {
        return destination_loc;
    }

    public void setDestination_loc(String destination_loc) {
        this.destination_loc = destination_loc;
    }

    private LatLong meet_location,drop_location;

    public LatLong getMeet_location() {
        return meet_location;
    }

    public void setMeet_location(LatLong meet_location) {
        this.meet_location = meet_location;
    }

    public LatLong getDrop_location() {
        return drop_location;
    }

    public void setDrop_location(LatLong drop_location) {
        this.drop_location = drop_location;
    }
}
