package asim.tgs_member_app.models;


import asim.tgs_member_app.restclient.BaseModel;

/**
 * Created by Asim Shahzad on 1/22/2018.
 */
public class PersonContact extends BaseModel
{
    private String name;
    private String number;
    private String email;
    private String id;

    public PersonContact(String name, String number, String email, String id) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.id = id;
    }

    public PersonContact() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
