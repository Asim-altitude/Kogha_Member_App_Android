package asim.tgs_member_app.chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import asim.tgs_member_app.R;
import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.models.Member;
import asim.tgs_member_app.service.ChatMessageNotifier;
import asim.tgs_member_app.service.FcmMessagingService;
import cz.msebera.android.httpclient.Header;

public class ChatActivity extends AppCompatActivity {

    private Member member;
    private FirebaseDatabase firebaseDatabase;
    private String chat_id = "";
    private SharedPreferences settings;
    private Customer customer;
    private EditText message_box;
    private ImageView send_btn;
    private RecyclerView chat_recycler;
    private ChatListAdapter chatListAdapter;
    private List<Chat_Message> chat_list;
     java.util.Calendar c;
    String order_id;

    public void clearNotfcations()
    {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(10);
        FcmMessagingService.count = 0;
    }
     String cust_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_chat);

        setupToolbar();
        c = java.util.Calendar.getInstance();

        clearNotfcations();

        chat_recycler = (RecyclerView) findViewById(R.id.chat_list);
        message_box = (EditText) findViewById(R.id.message_box);
        send_btn = (ImageView) findViewById(R.id.send_btn);

        Constants.isChatting = true;
        settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String name = settings.getString(Constants.PREFS_USER_NAME,"");
        String id = settings.getString(Constants.PREFS_USER_ID,"");
        String image = settings.getString(Constants.PREFS_USER_IMAGE,"");
        String lat = settings.getString("lastLocationLatitude","");
        String lng = settings.getString("lastLocationLongitude","");
        member = new Member(id,name,"",image,lat,lng,"","","");
        customer = (Customer) getIntent().getSerializableExtra("customer");
        chat_id = getIntent().getStringExtra("chat_id");
        cust_id = getIntent().getStringExtra("customer_id");
        order_id = getIntent().getStringExtra("order_id");

        if (chat_id==null)
            chat_id = FireBaseChatHead.getUniqueChatId(member.getMem_id(),cust_id,order_id);

        chat_list = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chat_list,getApplicationContext());
        chat_recycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this,LinearLayout.VERTICAL,false));
        chat_recycler.setAdapter(chatListAdapter);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message_box.getText().toString().equals(""))
                {
                    try
                    {
                        Chat_Message message = new Chat_Message();
                        message.setCustomer(customer);
                        message.setMember(member);
                        DateFormat format1 = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
                        String time = format1.format(c.getTime());
                        String am_pm="pm";
                        time = time+" "+am_pm;
                        Message message1 = new Message(message_box.getText().toString(),time, "mem");
                        message1.setOrder_id(order_id);
                        message1.setShown(false);
                        String server_time = ServerValue.TIMESTAMP.get("date");
                        message1.setServer_time(server_time);
                        message.setMessage(message1);

                        if (cust_id==null)
                        {
                            if (customer!=null)
                                cust_id = customer.getC_id();
                        }

                        String key = firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).push().getKey();
                        firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).child(key).setValue(message);
                        message_box.setText("");
                        showSoftwareKeyboard(false);
                        chat_recycler.smoothScrollToPosition(chat_list.size()-1);
                        sendNotifcationCustomer(member.getMem_id(),cust_id,message1.getMessage_text(),chat_id);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        loadAllOldData();

        AddChatValuChangeListner();
    }

    public void setListViewHeightBasedOnChildren() {

        /*try {

            ListAdapter listAdapter = visitorList.getAdapter();
            if (listAdapter == null) {
                // pre-condition
            }

                return;
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, visitorList);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = visitorList.getLayoutParams();
            params.height = totalHeight + (visitorList.getDividerHeight() * (listAdapter.getCount() - 1));
            visitorList.setLayoutParams(params);
            visitorList.requestLayout();

            View v = getActivity().getCurrentFocus();
            if (v != null)
                v.clearFocus();

        } catch (Exception e) {
            Log.e("cause of error", e.getMessage());
        }*/
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView)toolbar.findViewById(R.id.pageTitle)).setText("Chat");
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        setTitle("");

    }


    private void AddChatValuChangeListner()
    {
        try {
            firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try
                    {
                        Chat_Message message = dataSnapshot.getValue(Chat_Message.class);
                        Log.e("sender",message.getMessage().getSender());

                        chat_list.add(message);
                        chatListAdapter.notifyDataSetChanged();

                        if (customer==null)
                            customer = message.getCustomer();

                        chat_recycler.smoothScrollToPosition(chat_list.size()-1);

                        message.getMessage().setShown(true);
                        updateMessageStatus(chat_id,dataSnapshot.getKey(),message);

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
        catch (Exception e)
        {
            Log.e("error",e.getMessage());
        }
    }

    private void updateMessageStatus(String chat_id,String key, Chat_Message message_val)
    {
        firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).child(key).setValue(message_val);

    }

    private void loadAllOldData()
    {
        try {
            firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try
                    {
                        String s = new String();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            Log.e("error",e.getMessage());
        }
    }

    protected void showSoftwareKeyboard(boolean showKeyboard) {
        try {
            final Activity activity = ChatActivity.this;
            final InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.isChatting = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constants.isChatting = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.isChatting = true;
    }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private void sendNotifcationCustomer(String member_id,String customer_id,String messge,String chat_id)
    {
        asyncHttpClient.setConnectTimeout(30000);

        Log.e("url",Constants.Host_Address + "members/send_chat_notification_to_customer/" +member_id+"/"+customer_id+"/"+chat_id+"/"+messge+"/tgs_appkey_amin");
        asyncHttpClient.get(ChatActivity.this,Constants.Host_Address + "members/send_chat_notification_to_customer/" +member_id+"/"+customer_id+"/"+chat_id+"/"+messge+"/tgs_appkey_amin", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.e("response ",response);
                }
                catch (Exception e)
                {
                    Log.e("error_notification",e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String response = new String(responseBody);
                    Log.e("response ",response);
                }
                catch (Exception e)
                {
                    Log.e("error_notification",e.getMessage());
                }
            }
        });


    }
}
