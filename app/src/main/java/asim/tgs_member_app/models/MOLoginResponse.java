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
public class MOLoginResponse extends BaseModel {

	private String status;
	private String message;
	private String name;
	private String token;
	private String customer_id;
	private String mobile;
	private String sos_status;
	private String device_info;

	public String getSos_status() {
		return sos_status;
	}

	public void setSos_status(String sos_status) {
		this.sos_status = sos_status;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getSos_enable_disable() {
		return sos_status;
	}

	public void setSos_enable_disable(String sos_enable_disable) {
		this.sos_status = sos_enable_disable;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	private String customer_full_img;
	private String customer_thumb_img;
	private String profile_img;

	public String getProfile_img() {
		return profile_img;
	}

	public void setProfile_img(String profile_img) {
		this.profile_img = profile_img;
	}

	private int errorCode;
	private boolean customer_login;
	private MOUser payLoad;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		errorCode = errorCode;
	}

	public boolean isCustomer_login() {
		return customer_login;
	}

	public void setCustomer_login(boolean customer_login) {
		this.customer_login = customer_login;
	}

	public MOUser getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(MOUser payLoad) {
		this.payLoad = payLoad;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
}
