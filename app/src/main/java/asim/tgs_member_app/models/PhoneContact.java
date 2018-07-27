package asim.tgs_member_app.models;


import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by Asim Shahzad on 12/13/2017.
 */
public class PhoneContact extends BaseModel
{
    private String name;
    private String number;
    private String id;
    private boolean isSelected;
    private boolean isSent = false;

    public boolean isSent() {
        return isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public PhoneContact(String name, String number,boolean isSelected,String id) {
        this.name = name;
        this.number = number;
        this.isSelected = isSelected;
        this.id=id;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
