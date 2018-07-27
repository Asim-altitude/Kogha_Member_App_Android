package asim.tgs_member_app.chat;
/**
 * Created by PC-GetRanked on 3/13/2018.
 */

import asim.tgs_member_app.models.Member;
import asim.tgs_member_app.restclient.BaseModel;
public class Chat_Message extends BaseModel
{
    private Customer customer;
    private Member member;
    private Message message;

    public Chat_Message() {
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
