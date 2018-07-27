/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asim.tgs_member_app.models;



import java.util.ArrayList;

import asim.tgs_member_app.restclient.BaseModel;

/**
 * JsonResponse model is standard response for millao, everything service is
 * expected to return this model where as per service payLoad object may
 * represent a different model
 *
 * @author Sohaib
 */
public class MOCustomerProfile extends BaseModel {

	private String status;
	private String message;
	private int errorCode;
	private ArrayList<MOCustomerProfileData> data;

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

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public ArrayList<MOCustomerProfileData> getData() {
		return data;
	}

	public void setData(ArrayList<MOCustomerProfileData> data) {
		this.data = data;
	}
}
