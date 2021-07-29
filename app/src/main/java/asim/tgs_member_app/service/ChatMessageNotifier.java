package asim.tgs_member_app.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import asim.tgs_member_app.R;
import asim.tgs_member_app.chat.ChatActivity;
import asim.tgs_member_app.chat.Chat_Message;
import asim.tgs_member_app.chat.FireBaseChatHead;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.Notification_Data;

/**
 * Created by PC-GetRanked on 3/14/2018.
 */

public class ChatMessageNotifier extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private Handler handler;
    private Runnable runnable;


    private FirebaseDatabase firebaseDatabase;
    String member_id;

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacks(runnable);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void onCreate() {
        super.onCreate();

        SharedPreferences settings = this.getSharedPreferences(Constants.PREFS_NAME, 0);

        firebaseDatabase = FirebaseDatabase.getInstance();
        Log.e("service for chat","activated");
        member_id = settings.getString(Constants.PREFS_USER_ID, "");

        handler = new Handler();
        runnable = new Runnable() {
            public void run()
            {
                try {
                    handler.postDelayed(runnable, 4000);

                    Log.e("chat service ","started again");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };

        handler.postDelayed(runnable, 15000);

        listenForChatMessages();
    }

    private void listenForChatMessages() {
        firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getAllChatNodes((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private ArrayList<String> chat_nodes = new ArrayList<>();
    private void getAllChatNodes(Map<String,Object> nodes)
    {
        if (nodes!=null) {
            for (Map.Entry<String, Object> entry : nodes.entrySet()) {

                String chat_id = entry.getKey();
                Log.e("key", chat_id);
                if (chat_id.contains(member_id)) {
                    ListenUpdates(chat_id);
                    chat_nodes.add(chat_id);
                }
            }
        }
    }

    private void ListenUpdates(final String chat_id)
    {
        firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                try
                {
                    Chat_Message message = dataSnapshot.getValue(Chat_Message.class);

                    Log.e("message status",message.getMessage().getSender());
                    if (!message.getMessage().isShown()
                            && message.getMessage().getSender().equalsIgnoreCase("cust")
                            && !Constants.isChatting)
                    {
                        String key = dataSnapshot.getKey();
                        message.getMessage().setShown(true);
                        updateMessageStatus(chat_id,key,message);
                        NotifyUser(message,chat_id);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    ArrayList<String> messege_list = new ArrayList<>();
    public static int count = 0;
    String messeges="";
    private void NotifyUser(Chat_Message message,String chat_id)
    {
        if (count==0)
        {
            messege_list.clear();
            messeges = "";
        }
        count++;
        messege_list.add(message.getMessage().getMessage_text());

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chat_id",chat_id);

        //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        if (count>1)
        {
            for (int i=0;i<messege_list.size();i++)
            {
                messeges += "\n"+messege_list.get(i);
            }
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_logo);;
        notificationBuilder.setContentTitle("TGS Message");
        if (count>1)
        {
            notificationBuilder.setContentTitle(count+" TGS Messeges");
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messeges));
            notificationBuilder.setContentText(messeges);
            messeges = "";
        }
        else
        {
            notificationBuilder.setContentText(message.getMessage().getMessage_text());
        }
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        saveChatNotification(message);
        notificationManager.notify(10 /* ID of notification */, notificationBuilder.build());
    }
    ArrayList<Notification_Data> notifications;
    SharedPreferences settings;
    private void saveChatNotification(Chat_Message message)
    {
        settings = getSharedPreferences(Constants.USER_CHAT_NOTIFICATION , MODE_PRIVATE);

        Gson gson = new Gson();
        String notification_type;
        String json;
        // String json = gson.toJson(notifications);

        json = settings.getString("chat_notifications", "");
        notification_type = "chat_notifications";


        notifications = new ArrayList<>();

        if (json==null || json.isEmpty())
        {


            Notification_Data notification = new Notification_Data();
            notification.setBody(message.getMessage().getMessage_text());
            notification.setTitle("Sender: "+message.getMember().getMem_name());
            notification.setExtra("");
            notification.setId("1");
            notification.setShown(false);

            notifications.add(notification);


        }
        else
        {

            Type type = new TypeToken<List<Notification_Data>>() {}.getType();
            notifications = gson.fromJson(json, type);

            if (notifications!=null)
            {
                int extra_id = notifications.size();
                extra_id++;

                Notification_Data notification = new Notification_Data();
                notification.setBody(message.getMessage().getMessage_text());
                notification.setTitle("Sender: "+message.getMember().getMem_name());
                notification.setExtra("");
                notification.setId(extra_id + "");
                notification.setShown(false);

                notifications.add(notification);

            }


        }

        String data = gson.toJson(notifications);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(notification_type,data);
        editor.apply();

    }
    private void updateMessageStatus(String chat_id,String key, Chat_Message message_val)
    {
        firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).child(key).setValue(message_val);
    }
}
