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
public class MOCustomerProfileData extends BaseModel {

	private String customer_id;
	private String customer_name;
	private String customer_mobile;
	private String customer_email;
	private String customer_full_img;
	private String customer_thumb_img;

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getCustomer_mobile() {
		return customer_mobile;
	}

	public void setCustomer_mobile(String customer_mobile) {
		this.customer_mobile = customer_mobile;
	}

	public String getCustomer_email() {
		return customer_email;
	}

	public void setCustomer_email(String customer_email) {
		this.customer_email = customer_email;
	}

	public String getCustomer_full_img() {
		return customer_full_img;
	}

	public void setCustomer_full_img(String customer_full_img) {
		this.customer_full_img = customer_full_img;
	}

	public String getCustomer_thumb_img() {
		return customer_thumb_img;
	}

	public void setCustomer_thumb_img(String customer_thumb_img) {
		this.customer_thumb_img = customer_thumb_img;
	}
}
