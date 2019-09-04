package com.example.chatroom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.chatroom.Adapter.Group_Adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Group_Chat extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton imageButton;
    private EditText editText;
    private ScrollView scrollView;
    private TextView textView;
    private RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ListView listView = null;



    private String currentGroupName,currentUserID,currentUserName,currentdate,currenttime,currentuserimage;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef,GroupnameRef,GroupmsgkeyRef,memRef;

    StringBuilder s = new StringBuilder(100);

    List<String> items = new ArrayList<String>();

    ArrayList<Usermessages> usermessages;
    Usermessages get;

    //android.support.v7.app.ActionBar actionBar = getSupportActionBar();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__chat);

        //android.support.v7.app.ActionBar actionBar = getSupportActionBar();


        //toolbar = findViewById(R.id.group_chat_barlayout);
        //setSupportActionBar(toolbar);

        currentGroupName = getIntent().getExtras().get("groupname").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupnameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Messages");
        memRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Members");


        getUserInfo();

        usermessages = new ArrayList<>();




        imageButton = findViewById(R.id.send_message);
        editText = findViewById(R.id.input);
        mRecyclerView=findViewById(R.id.recyclerview_group);



        currentGroupName = getIntent().getExtras().get("groupname").toString();
        getSupportActionBar().setTitle(currentGroupName);
        Toast.makeText(Group_Chat.this,currentGroupName,Toast.LENGTH_LONG).show();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savetoDatabase();

            }
        });

        readmemGroups();
        //actionBar.setSubtitle(s);



    }

    private void readmemGroups() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Members");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                s.setLength(0);

                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    String d = (((DataSnapshot)iterator.next()).getKey());
                    items.add(d);
                    s.append(d +", ");
                    //items.add(((DataSnapshot)iterator.next()).getKey());
                    //s.append(((DataSnapshot)iterator.next()).getKey()+ " ");
                }
                if(s.length()>2) {
                    s.setLength(s.length() - 2);
                }

                Log.d("lol", String.valueOf(s));
                android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                actionBar.setSubtitle(s);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        super.onCreateOptionsMenu(menu2);
        getMenuInflater().inflate(R.menu.menu2, menu2);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.Groupmem:
                Groupmemlist();

        }

        return false;
    }

    private void Groupmemlist() {


        listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter <String>(this,R.layout.list_item,R.id.txtitem,items);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(Group_Chat.this,R.style.AlertDialog);
        builder.setTitle(currentGroupName + "  Members");
        builder.setCancelable(true);
        builder.setPositiveButton("ok",null);

        builder.setView(listView);
        builder.show();
    }

    @Override
    protected void onStart(){
        super.onStart();

        GroupnameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot dataSnapshot){

        GroupnameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usermessages.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    get = new Usermessages();
                    get.setDate(postSnapshot.child("date").getValue().toString());
                    get.setMessage(postSnapshot.child("message").getValue().toString());
                    get.setMsgid(postSnapshot.child("msgid").getValue().toString());
                    get.setName(postSnapshot.child("name").getValue().toString());
                    get.setTime(postSnapshot.child("time").getValue().toString());
                    get.setUserid(postSnapshot.child("userid").getValue().toString());
                    get.setUserimage(postSnapshot.child("userimage").getValue().toString());
                    get.setGroupname(postSnapshot.child("groupname").getValue().toString());
                    get.setLikes(postSnapshot.child("likes").getValue().toString());
                    Log.d("msg",get.message);
                    usermessages.add(get);
                }

                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(Group_Chat.this);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new Group_Adapter(usermessages);
                //mRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
                //dialog.dismiss();
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void getUserInfo() {
        mRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("username").getValue().toString();
                    currentuserimage = dataSnapshot.child("imageurl").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void savetoDatabase(){
        String message = editText.getText().toString();
        String messageKey = GroupnameRef.push().getKey();
        if(TextUtils.isEmpty(message)){
            Toast.makeText(Group_Chat.this,"enter message",Toast.LENGTH_LONG).show();
        }
        else{
            Calendar ccalForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            currentdate = currentDateFormat.format(ccalForDate.getTime());

            Calendar calfortime = Calendar.getInstance();
            SimpleDateFormat currenttimeformat = new SimpleDateFormat("hh:mm a");
            currenttime = currenttimeformat.format(calfortime.getTime());

            HashMap<String,Object> Groupmessagekey = new HashMap <>();
            GroupnameRef.updateChildren(Groupmessagekey);

            GroupmsgkeyRef = GroupnameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap = new HashMap <>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("userimage",currentuserimage);
            //Log.d("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentdate);
            messageInfoMap.put("time",currenttime);
            messageInfoMap.put("userid",currentUserID);
            messageInfoMap.put("msgid",messageKey);
            messageInfoMap.put("groupname",currentGroupName);
            messageInfoMap.put("likes",0);

            GroupmsgkeyRef.updateChildren(messageInfoMap);

            editText.setText("");

            memRef.child(currentUserName).setValue("");



        }
    }
}
