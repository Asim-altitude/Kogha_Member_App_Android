/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asim.tgs_member_app.models;


import asim.tgs_member_app.restclient.BaseModel;

/**
 * JsonResponse model is standard response for millao, everything service is
 * expected to return this model where as per service payLoad object may
 * represent a different model
 *
 * @author Sohaib
 */
public class MOServiceTypeList extends BaseModel {

	private String service_id;
	private String service_type;
	private String service_price;
	private String member_share;
	private String minimum_hours;
	private String status;
	private String cost_per_km;
	private String member_per_km_share;

	private String member_calculated_share;
	private String member_extra_amount;

	public String getMember_extra_amount() {
		return member_extra_amount;
	}

	public void setMember_extra_amount(String member_extra_amount) {
		this.member_extra_amount = member_extra_amount;
	}

	public String getMember_calculated_share() {
		return member_calculated_share;
	}

	public void setMember_calculated_share(String member_calculated_share) {
		this.member_calculated_share = member_calculated_share;
	}

	public String getMember_per_km_share() {
		return member_per_km_share;
	}

	public void setMember_per_km_share(String member_per_km_share) {
		this.member_per_km_share = member_per_km_share;
	}


	public String getMinimum_km() {
		return cost_per_km;
	}

	public void setMinimum_km(String minimum_km) {
		this.cost_per_km = minimum_km;
	}

	public int getService_hours() {
		return service_hours;
	}

	public void setService_hours(int service_hours) {
		this.service_hours = service_hours;
	}

	private int service_hours;
	public String getPrice_of_service() {
		return price_of_service;
	}

	public void setPrice_of_service(String price_of_service) {
		this.price_of_service = price_of_service;
	}

	private String price_of_service;

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getService_type() {
		return service_type;
	}

	public void setService_type(String service_type) {
		this.service_type = service_type;
	}

	public String getService_price() {
		return service_price;
	}

	public void setService_price(String service_price) {
		this.service_price = service_price;
	}

	public String getMember_share() {
		return member_share;
	}

	public void setMember_share(String member_share) {
		this.member_share = member_share;
	}

	public String getMinimum_hours() {
		return minimum_hours;
	}

	public void setMinimum_hours(String minimum_hours) {
		this.minimum_hours = minimum_hours;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
