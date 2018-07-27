/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asim.tgs_member_app.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * JsonResponse model is standard response for millao, everything service is
 * expected to return this model where as per service payLoad object may
 * represent a different model
 *
 * @author Sohaib
 */
public class MOOrderPlace implements Serializable {

	private String dateTime;
	private String location;
	private String hours;
	private String pickUpLocation;



	public ArrayList<MOServiceTypeList> getOrder_data() {
		return order_data;
	}

	public void setOrder_data(ArrayList<MOServiceTypeList> order_data) {
		this.order_data = order_data;
	}

	private ArrayList<MOServiceTypeList> order_data;


	private String hoursOneServiceType1;
	private String hoursTwoServiceType1;
	private String hoursThreeServiceType1;

	private String hoursOneServiceType2;
	private String hoursTwoServiceType2;
	private String hoursThreeServiceType2;

	private String hoursOneServiceType3;
	private String hoursTwoServiceType3;
	private String hoursThreeServiceType3;


	private String hoursOneServiceType4;
	private String hoursTwoServiceType4;
	private String hoursThreeServiceType4;

	public String getPickUpLocation() {
		return pickUpLocation;
	}

	public void setPickUpLocation(String pickUpLocation) {
		this.pickUpLocation = pickUpLocation;
	}

	public String getHoursOneServiceType1() {
		return hoursOneServiceType1;
	}

	public void setHoursOneServiceType1(String hoursOneServiceType1) {
		this.hoursOneServiceType1 = hoursOneServiceType1;
	}

	public String getHoursTwoServiceType1() {
		return hoursTwoServiceType1;
	}

	public void setHoursTwoServiceType1(String hoursTwoServiceType1) {
		this.hoursTwoServiceType1 = hoursTwoServiceType1;
	}

	public String getHoursThreeServiceType1() {
		return hoursThreeServiceType1;
	}

	public void setHoursThreeServiceType1(String hoursThreeServiceType1) {
		this.hoursThreeServiceType1 = hoursThreeServiceType1;
	}

	public String getHoursOneServiceType2() {
		return hoursOneServiceType2;
	}

	public void setHoursOneServiceType2(String hoursOneServiceType2) {
		this.hoursOneServiceType2 = hoursOneServiceType2;
	}

	public String getHoursTwoServiceType2() {
		return hoursTwoServiceType2;
	}

	public void setHoursTwoServiceType2(String hoursTwoServiceType2) {
		this.hoursTwoServiceType2 = hoursTwoServiceType2;
	}

	public String getHoursThreeServiceType2() {
		return hoursThreeServiceType2;
	}

	public void setHoursThreeServiceType2(String hoursThreeServiceType2) {
		this.hoursThreeServiceType2 = hoursThreeServiceType2;
	}

	public String getHoursOneServiceType3() {
		return hoursOneServiceType3;
	}

	public void setHoursOneServiceType3(String hoursOneServiceType3) {
		this.hoursOneServiceType3 = hoursOneServiceType3;
	}

	public String getHoursTwoServiceType3() {
		return hoursTwoServiceType3;
	}

	public void setHoursTwoServiceType3(String hoursTwoServiceType3) {
		this.hoursTwoServiceType3 = hoursTwoServiceType3;
	}

	public String getHoursThreeServiceType3() {
		return hoursThreeServiceType3;
	}

	public void setHoursThreeServiceType3(String hoursThreeServiceType3) {
		this.hoursThreeServiceType3 = hoursThreeServiceType3;
	}

	public String getHoursOneServiceType4() {
		return hoursOneServiceType4;
	}

	public void setHoursOneServiceType4(String hoursOneServiceType4) {
		this.hoursOneServiceType4 = hoursOneServiceType4;
	}

	public String getHoursTwoServiceType4() {
		return hoursTwoServiceType4;
	}

	public void setHoursTwoServiceType4(String hoursTwoServiceType4) {
		this.hoursTwoServiceType4 = hoursTwoServiceType4;
	}

	public String getHoursThreeServiceType4() {
		return hoursThreeServiceType4;
	}

	public void setHoursThreeServiceType4(String hoursThreeServiceType4) {
		this.hoursThreeServiceType4 = hoursThreeServiceType4;
	}



	private String uniformId;
	private String instructions;
	private String trip_type;
	private int badge = 0;
	private ArrayList<String> serviceId = new ArrayList<>();
	private ArrayList<String> quantity = new ArrayList<>();
	private ArrayList<String> price = new ArrayList<>();
	private ArrayList<String> serviceName = new ArrayList<>();

	public ArrayList<String> getServiceName() {
		return serviceName;
	}

	public void setServiceName(ArrayList<String> serviceName) {
		this.serviceName = serviceName;
	}

	public int getBadge() {
		return badge;
	}

	public void setBadge(int badge) {
		this.badge = badge;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getUniformId() {
		return uniformId;
	}

	public void setUniformId(String uniformId) {
		this.uniformId = uniformId;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getTrip_type() {
		return trip_type;
	}

	public void setTrip_type(String trip_type) {
		this.trip_type = trip_type;
	}

	public ArrayList<String> getServiceId() {
		return serviceId;
	}

	public void setServiceId(ArrayList<String> serviceId) {
		this.serviceId = serviceId;
	}

	public ArrayList<String> getQuantity() {
		return quantity;
	}

	public void setQuantity(ArrayList<String> quantity) {
		this.quantity = quantity;
	}

	public ArrayList<String> getPrice() {
		return price;
	}

	public void setPrice(ArrayList<String> price) {
		this.price = price;
	}
}
