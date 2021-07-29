package asim.tgs_member_app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import asim.tgs_member_app.DrawerActivity;
import asim.tgs_member_app.R;
import asim.tgs_member_app.chat.ChatActivity;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.Notification_Data;
import asim.tgs_member_app.utils.NotificationUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by PC-GetRanked on 12/9/2017.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    Intent intent;

    int badgeCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
       // startTimer();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {

            String body = remoteMessage.getData().get("body");
            String title = remoteMessage.getData().get("title");
            String post_id = remoteMessage.getData().get("post_id");

            Log.e("message",remoteMessage.toString());


            if (!Constants.can_login)
            {
                return;
            }

            if (post_id==null)
                post_id = "0";

            if (body != null) {
                if (post_id.contains("chat"))
                {
                    NotifyUser(body,post_id,title);
                }
               else if (title.contains("job") || title.contains("Job"))
                    savenotification(body, title, post_id,Constants.USER_JOB_NOTIFICATION);
                else
                    savenotification(body, title, post_id,Constants.USER_NOTIFICATION);
               // sendNotification(body, title, post_id);

          /*  Intent local = new Intent();

            local.setAction("YourIntentAction").putExtra("notification_count",  db.getCount()+"");

            this.sendBroadcast(local);*/

            }

            //   Log.d(TAG, "From: " + remoteMessage.getFrom());
            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            }
            super.onMessageReceived(remoteMessage);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("error  ",e.getMessage());
        }

    }


    private void savenotification(String body, String title, String post_id,String prefs_name)
    {
        settings = getSharedPreferences(prefs_name,MODE_PRIVATE);

        Gson gson = new Gson();
        String notification_type;
        String json;
        String  date_before = new SimpleDateFormat("dd MMM yyyy h:mm").format(Calendar.getInstance().getTime());
        // String json = gson.toJson(notifications);
        if (prefs_name.equalsIgnoreCase(Constants.USER_JOB_NOTIFICATION)) {
            json = settings.getString("job_notifications", "");
            notification_type = "job_notifications";
        }
        else {
            json = settings.getString("gen_notifications", "");
            notification_type = "gen_notifications";
        }


        notifications = new ArrayList<>();

        if (json==null || json.isEmpty())
        {


            Notification_Data notification = new Notification_Data();
            notification.setBody(body);
            notification.setTitle(title);
            notification.setExtra("");
            notification.setId("1");
            notification.setTime(date_before);
            notification.setShown(false);

            notifications.add(notification);

            sendNotification(notification.getBody(),notification.getTitle(),notification.getId());

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
                notification.setBody(body);
                notification.setTitle(title);
                notification.setExtra("");
                notification.setTime(date_before);
                notification.setId(extra_id + "");
                notification.setShown(false);

                notifications.add(notification);

                sendNotification(notification.getBody(),notification.getTitle(),notification.getId());

            }


        }

        String data = gson.toJson(notifications);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(notification_type,data);
        editor.apply();


    }

    public void sendNotification(String messageBody, String title_, String post_id) {

         intent = new Intent(this, DrawerActivity.class);

        intent.putExtra("title",title_);
        intent.putExtra("body",messageBody);
        intent.putExtra("post_id",post_id);
        intent.putExtra("index",1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.kogha_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notificationUtils==null)
                notificationUtils = new NotificationUtils(getApplicationContext());

            Notification.Builder notificationBuilder = notificationUtils.getAndroidChannelNotification(title_,messageBody);
            notificationBuilder.setSmallIcon(R.drawable.kogha_launcher);
            ;
          /*  notificationBuilder.setContentTitle(title);
            if (count > 1) {
                notificationBuilder.setContentTitle(count + " chat messeges");
                notificationBuilder.setStyle(new Notification.BigTextStyle().bigText(messeges));
                notificationBuilder.setContentText(messeges);
                messeges = "";
            } else {
                notificationBuilder.setContentText(message);
            }*/

            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setLargeIcon(bm);
            notificationBuilder.setContentIntent(pendingIntent);

            notificationUtils.getManager().notify(101, notificationBuilder.build());

        }
        else {

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.kogha_launcher);
                // notificationBuilder.setColor(getResources().getColor(R.color.reddish));
            } else {
                notificationBuilder.setSmallIcon(R.drawable.kogha_launcher);
            }
            notificationBuilder
                    .setLargeIcon(bm)
                    .setContentTitle(title_)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constants.NOTICIATION_ID /* ID of notification */, notificationBuilder.build());

        }
    }


    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    int counter =0;
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.e("in timer", "in timer "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent(Constants.BROADCAST_INTENT_FILTER);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    ArrayList<String> messege_list = new ArrayList<>();
    public static int count = 0;
    String messeges="";
    NotificationUtils notificationUtils;
    private void NotifyUser(String message, String chat_id,String title)
    {
        if (count==0)
        {
            messege_list.clear();
            messeges = "";
        }
        count++;
        messege_list.add(message);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notificationUtils==null)
                notificationUtils = new NotificationUtils(getApplicationContext());

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.kogha_launcher);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification.Builder notificationBuilder = notificationUtils.getAndroidChannelNotification(title,message);
            notificationBuilder.setSmallIcon(R.drawable.kogha_launcher);
            ;
          /*  notificationBuilder.setContentTitle(title);
            if (count > 1) {
                notificationBuilder.setContentTitle(count + " chat messeges");
                notificationBuilder.setStyle(new Notification.BigTextStyle().bigText(messeges));
                notificationBuilder.setContentText(messeges);
                messeges = "";
            } else {
                notificationBuilder.setContentText(message);
            }*/

            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setLargeIcon(bm);
            notificationBuilder.setContentIntent(pendingIntent);

            notificationUtils.getManager().notify(101, notificationBuilder.build());

        }
        else {

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.kogha_launcher);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setSmallIcon(R.drawable.kogha_launcher);
            ;
            notificationBuilder.setContentTitle(title);
            if (count > 1) {
                notificationBuilder.setContentTitle(count + " chat messeges");
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messeges));
                notificationBuilder.setContentText(messeges);
                messeges = "";
            } else {
                notificationBuilder.setContentText(message);
            }
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setLargeIcon(bm);
            notificationBuilder.setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            saveChatNotification(message, title, chat_id);
            notificationManager.notify(10 /* ID of notification */, notificationBuilder.build());

        }
    }
    ArrayList<Notification_Data> notifications;
    SharedPreferences settings;
    private void saveChatNotification(String message,String title,String chat_id)
    {
        settings = getSharedPreferences(Constants.USER_CHAT_NOTIFICATION , MODE_PRIVATE);



        Gson gson = new Gson();
        String notification_type;
        String json;
        // String json = gson.toJson(notifications);

        json = settings.getString("chat_notifications", "");
        notification_type = "chat_notifications";

        String  date_before = new SimpleDateFormat("dd MMM yyyy h:mm").format(Calendar.getInstance().getTime());

        notifications = new ArrayList<>();

        if (json==null || json.isEmpty())
        {


            Notification_Data notification = new Notification_Data();
            notification.setBody(message);
            notification.setTitle(title);
            notification.setExtra("");
            notification.setId("1");
            notification.setChat_id(chat_id);
            notification.setChat(true);
            notification.setTime(date_before);
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
                notification.setBody(message);
                notification.setTitle(title);
                notification.setExtra("");
                notification.setId(extra_id + "");
                notification.setShown(false);
                notification.setChat_id(chat_id);
                notification.setChat(true);
                notification.setTime(date_before);
                notifications.add(notification);

            }


        }

        String data = gson.toJson(notifications);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(notification_type,data);
        editor.apply();

    }
}
