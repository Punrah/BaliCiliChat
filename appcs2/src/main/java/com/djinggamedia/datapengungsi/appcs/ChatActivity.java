package com.djinggamedia.datapengungsi.appcs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.djinggamedia.datapengungsi.appcs.adapter.MessageAdapter;
import com.djinggamedia.datapengungsi.appcs.helper.UserGlobal;
import com.djinggamedia.datapengungsi.appcs.persistence.Chat;
import com.djinggamedia.datapengungsi.appcs.persistence.Message;
import com.djinggamedia.datapengungsi.appcs.persistence.User;
import com.firebase.client.ServerValue;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    TextView textViewName;
    String name;
    String image;
    String tanggal;
    ImageView back;
    String id;

    private MessageAdapter mAdapter;
    private static LinearLayoutManager mLayoutManager;

    private List<Message> listItem;
    private List<String> listTemplate;
    private DatabaseReference mDatabase;

    EditText textMessage;
    ImageView send;
    Spinner plus;
    String pesan="";

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        textViewName=(TextView) findViewById(R.id.name);

        textMessage=(EditText) findViewById(R.id.input_message);
        send=(ImageView) findViewById(R.id.send);
plus=(Spinner) findViewById(R.id.plus);
        fetchSpinner();
        plus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
textMessage.setText(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                textMessage.setText("");

            }
        });
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

        name=getIntent().getStringExtra("name");
        id=getIntent().getStringExtra("id");
        image=getIntent().getStringExtra("image");
        pesan=getIntent().getStringExtra("lastChat");
        tanggal=getIntent().getStringExtra("date");
        textViewName.setText(name);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        fetchItem();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });





    }
    private void populateSpinner()
    {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listTemplate);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plus.setAdapter(dataAdapter);
    }

    private void fetchSpinner()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("templateCS/");
        ValueEventListener valueEventListener = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listTemplate = new ArrayList<>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    String value = iterator.next().getValue(String.class);
                    listTemplate.add(value);
                }
                populateSpinner();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
// Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(ChatActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
    }


    private void readMessage() {

            mDatabase = FirebaseDatabase.getInstance().getReference();

           String val = id;

            Chat chat3 = new Chat();
            chat3.setImage(image);
            chat3.setUid(val);
            chat3.setDate(tanggal);
            chat3.setLastChat(pesan);
            chat3.setUnread("false");
            chat3.setName(name);
            mDatabase.child("chatCS").child(val).setValue(chat3);


    }

    private void sendMessage() {

            String messageText = textMessage.getText().toString();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            User user = UserGlobal.getUser(this);
            String idx = user.id;

            DateFormat df = new SimpleDateFormat("d MMM yy h:mm a");
            String data = df.format(ServerValue.TIMESTAMP);

            Message message = new Message();
            message.setId(idx);
            message.setMessage(messageText);
            message.setRead("false");
            message.setImage(user.image);




            String idFirebase = mDatabase.child("bodychat").child(id).push().getKey();

            mDatabase.child("bodychat").child(id).child(idFirebase).setValue(message);

            String[] idid =id.split("[|]");
            String[] namename =name.split("[|]");
            String[] imageimage =image.split("[|]");

            Chat chat1 = new Chat();
            chat1.setImage(imageimage[1]);
            chat1.setUid(idid[1]);
            chat1.setDate(data);
            chat1.setLastChat(message.getMessage());
            chat1.setUnread("true");
            chat1.setName(namename[1]);

            mDatabase.child("chat").child(idid[0]).child(idid[1]).setValue(chat1);

            Chat chat2 = new Chat();
            chat2.setImage(imageimage[0]);
            chat2.setUid(idid[0]);
            chat2.setDate(data);
            chat2.setLastChat(message.getMessage());
            chat2.setUnread("true");
            chat2.setName(namename[0]);

            mDatabase.child("chat").child(idid[1]).child(idid[0]).setValue(chat2);

            Chat chat3 = new Chat();
            chat3.setImage(image);
            chat3.setUid(id);
            chat3.setDate(data);
            chat3.setLastChat(message.getMessage());
            chat3.setUnread("false");
            chat3.setName(name);

            mDatabase.child("chatCS").child(id).setValue(chat3);


            textMessage.setText("");

    }


    private void populate()
    {
        mAdapter = new MessageAdapter(this,listItem);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mAdapter.getItemCount()-1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        readMessage();

    }

    @Override
    protected void onPause() {
        super.onPause();
        readMessage();
    }

    private void fetch()
    {
        populate();
        //implementScrollListener();
    }


    private void fetchItem()
    {

         mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("bodychat/" + id);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listItem = new ArrayList<>();
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    while (iterator.hasNext()) {
                        Message value = iterator.next().getValue(Message.class);
                        listItem.add(value);
                        pesan=value.getMessage();
                    }
                    fetch();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
// Getting Post failed, log a message
                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(ChatActivity.this, "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            });

       }

}