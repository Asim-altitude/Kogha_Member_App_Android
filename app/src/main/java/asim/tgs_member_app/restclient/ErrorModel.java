package asim.tgs_member_app.restclient;

/**
 * Created by sohaibkhalid on 11/1/16.
 *
 */

public class ErrorModel extends BaseModel {
    private int status;
    private String exception;
    private String request;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
