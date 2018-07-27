package asim.tgs_member_app.models;

import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by PC-GetRanked on 5/23/2018.
 */

public class MemberService extends BaseModel{

    private String name,id;
    private int imageDrawable;
    private boolean selected;

    public MemberService() {
        selected = false;
    }

    public MemberService(String name, String id, int imageDrawable, boolean selected) {
        this.name = name;
        this.id = id;
        this.imageDrawable = imageDrawable;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(int imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
