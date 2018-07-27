package asim.tgs_member_app.models;

/**
 * Created by Asim Shahzad on 1/30/2018.
 */
public class Notification_Data
{
    private String title;
    private String body;
    private String extra;
    private String id;
    private boolean shown;

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Notification_Data() {
    }

    public Notification_Data(String title, String body, String extra) {
        this.title = title;
        this.body = body;
        this.extra = extra;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
