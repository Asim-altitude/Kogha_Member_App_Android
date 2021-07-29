package asim.tgs_member_app.models;

public class ServiceDocument
{
    private String doc_id,document_name,document_path;
    private boolean isUploaded;
    private String[] docArray;

    public ServiceDocument() {
        isUploaded = false;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getDocument_path() {
        return document_path;
    }

    public void setDocument_path(String document_path) {
        this.document_path = document_path;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String[] getDocArray() {
        return docArray;
    }

    public void setDocArray(String[] docArray) {
        this.docArray = docArray;
    }
}
