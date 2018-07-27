package asim.tgs_member_app.models;

import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by PC-GetRanked on 6/7/2018.
 */

public class MemberDocument extends BaseModel {

    private String doc_name;
    private String doc_image;
    private String doc_id;
    private String uploaded_date;

    public String getUploaded_date() {
        return uploaded_date;
    }

    public void setUploaded_date(String uploaded_date) {
        this.uploaded_date = uploaded_date;
    }

    public MemberDocument(String doc_name, String doc_image, String doc_id) {
        this.doc_name = doc_name;
        this.doc_image = doc_image;
        this.doc_id = doc_id;
    }

    public MemberDocument() {
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getDoc_image() {
        return doc_image;
    }

    public void setDoc_image(String doc_image) {
        this.doc_image = doc_image;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }
}
