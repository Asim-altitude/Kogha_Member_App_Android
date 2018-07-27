package asim.tgs_member_app.models;

import java.io.Serializable;

/**
 * Created by Asim Shahzad on 12/29/2017.
 */
public class Service_Slot implements Serializable
{

    private String slot_id,slot_name;

    public Service_Slot(String slot_id, String slot_name, boolean isActive) {
        this.slot_id = slot_id;
        this.slot_name = slot_name;
        this.isActive = isActive;
    }

    public Service_Slot() {
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    private boolean isActive;

    public Service_Slot(String slot_id, String slot_name) {
        this.slot_id = slot_id;
        this.slot_name = slot_name;
    }

    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public String getSlot_name() {
        return slot_name;
    }

    public void setSlot_name(String slot_name) {
        this.slot_name = slot_name;
    }
}
