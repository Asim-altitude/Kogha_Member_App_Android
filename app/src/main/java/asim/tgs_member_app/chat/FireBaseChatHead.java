package asim.tgs_member_app.chat;

/**
 * Created by PC-GetRanked on 3/13/2018.
 */

public class FireBaseChatHead
{
    public static final String TGS_CHAT = "chat";

    public static String getUniqueChatId(String mem_id,String cust_id,String order_id)
    {
        return "chat_"+mem_id+"_"+cust_id+"_"+order_id;
    }
}
