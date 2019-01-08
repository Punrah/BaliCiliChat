package com.example.puniaraharja.balicilichat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.example.puniaraharja.balicilichat.adapter.MessageAdapter;
import com.example.puniaraharja.balicilichat.helper.UserGlobal;
import com.example.puniaraharja.balicilichat.persistence.Chat;
import com.example.puniaraharja.balicilichat.persistence.Message;
import com.example.puniaraharja.balicilichat.persistence.User;
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

public class ChatActivityCS extends AppCompatActivity {

    TextView textViewName;
    String name;
    String image;
    String tanggal="";
    String lastChat;
    ImageView back;
    ImageView delete;
    String id;
    TextView csRead;
    TextView vendorRead;
    private List<String> listTemplate;

    Spinner plus;

    private MessageAdapter mAdapter;
    private static LinearLayoutManager mLayoutManager;

    private List<Message> listItem;
    private DatabaseReference mDatabase;

    EditText textMessage;
    ImageView send;
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
        delete =(ImageView) findViewById(R.id.delete);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivityCS.this);
                builder.setMessage("Hapus Chat?").setPositiveButton("Ya", dialogClickListener)
                        .setNegativeButton("Tidak", dialogClickListener).show();
            }
        });


        csRead=(TextView) findViewById(R.id.cs_read);
        vendorRead=(TextView) findViewById(R.id.vendor_read);

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


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        checkReadStart();
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
        User user=UserGlobal.getUser(this);
        if(user.status.contentEquals("customer")) {
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("templateCustomer/");
        }
        else if(user.status.contentEquals("cs"))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("templateCS/");
        }
        else if(user.status.contentEquals("vendor"))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("templateVendor/");
        }
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
                Toast.makeText(ChatActivityCS.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    User user = UserGlobal.getUser(ChatActivityCS.this);
                    readMessageEnd();
                    checkReadEnd();
                    mDatabase=FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("chatall/bodychatCS").child(user.id).setValue(null);
                    mDatabase.child("chatall/chatCS").child(user.id).setValue(null);

                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:

                    //No button clicked
                    break;
            }
        }
    };

    public void checkReadStart()
    {
        User user = UserGlobal.getUser(this);
        mDatabase= FirebaseDatabase.getInstance().getReference();

        String[] idid=id.split("[|]");

        mDatabase.child("chatall/chat/"+ idid[0]+"/"+idid[1]).addListenerForSingleValueEvent(readChatVendor);
        mDatabase.child("chatall/chat/"+ idid[1]+"/"+idid[0]).addListenerForSingleValueEvent(readChatCustomer);

       }

    public void checkReadEnd()
    {
        User user = UserGlobal.getUser(this);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        String[] idid=id.split("[|]");
        mDatabase.child("chatall/chat/"+ idid[0]+"/"+idid[1]).removeEventListener(readChatVendor);
        mDatabase.child("chatall/chat/"+ idid[1]+"/"+idid[0]).removeEventListener(readChatCustomer);
    }

    ValueEventListener readChatVendor = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                final User user = UserGlobal.getUser(ChatActivityCS.this);
                mDatabase= FirebaseDatabase.getInstance().getReference();
                String[] idid=id.split("[|]");
                mDatabase.child("chatall/chat/"+ idid[0]+"/"+idid[1]).child("unread").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            String value = (String) dataSnapshot.getValue();
                            if (value.contentEquals("true")) {
                                vendorRead.setText("");
                            } else {
                                String[] namename=name.split("[|]");
                                    vendorRead.setText(namename[0] + " read");

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener readChatCustomer= new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                final User user = UserGlobal.getUser(ChatActivityCS.this);
                mDatabase= FirebaseDatabase.getInstance().getReference();
                String[] idid=id.split("[|]");
                mDatabase.child("chatall/chat/"+ idid[1]+"/"+idid[0]).child("unread").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            String value = (String) dataSnapshot.getValue();
                            if (value.contentEquals("true")) {
                                csRead.setText("");
                            } else {
                                String[] namename=name.split("[|]");
                                    csRead.setText(namename[1] + " read");

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };



    private void readMessageStart() {

        User user = UserGlobal.getUser(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
               mDatabase.child("chatall/bodychatCS/"+id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mDatabase.child("chatall/chatCS").child(id).child("unread").addValueEventListener(readMessageStartListener);
                }
                else {
                    DateFormat df = new SimpleDateFormat("d MMM yy h:mm a");
                    String data = df.format(new Date());
                    Chat chat1 = new Chat();
                    chat1.setImage(image);
                    chat1.setUid(id);
                    chat1.setDate((long) 0);
                    chat1.setLastChat(pesan);
                    chat1.setUnread("true");
                    chat1.setName(name);
                    mDatabase.child("chatall/chatCS").child(id).setValue(chat1);
                    mDatabase.child("chatall/chatCS").child(id).child("date").setValue(ServerValue.TIMESTAMP);

//                    Chat chat2 = new Chat();
//                    chat2.setImage(user.image);
//                    chat2.setUid(idx);
//                    chat2.setDate((long) 0);
//                    chat2.setLastChat(pesan);
//                    chat2.setUnread("false");
//                    chat2.setName(user.name);
//                    mDatabase.child("chatall/chat").child(id).child(idx).setValue(chat2);
//                    mDatabase.child("chatall/chat").child(id).child(idx).child("date").setValue(ServerValue.TIMESTAMP);
//
//
//
//                    if(user.status.contentEquals("vendor")) {
//                        Chat chat3 = new Chat();
//                        chat3.setImage(user.image+"|"+image);
//                        chat3.setUid(val);
//                        chat3.setDate((long) 0);
//                        chat3.setLastChat(pesan);
//                        chat3.setUnread("true");
//                        chat3.setName(user.name + "|" + name);
//                        mDatabase.child("chatall/chatCS").child(val).setValue(chat3);
//                        mDatabase.child("chatall/chatCS").child(val).child("date").setValue(ServerValue.TIMESTAMP);
//
//                    }
//                    else if(user.status.contentEquals("customer"))
//                    {
//                        Chat chat3 = new Chat();
//                        chat3.setImage(image+"|"+user.image);
//                        chat3.setUid(val);
//                        chat3.setDate((long) 0);
//                        chat3.setLastChat(pesan);
//                        chat3.setUnread("true");
//                        chat3.setName(name + "|" + user.name);
//                        mDatabase.child("chatall/chatCS").child(val).setValue(chat3);
//                        mDatabase.child("chatall/chatCS").child(val).child("date").setValue(ServerValue.TIMESTAMP);
//
//                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });












    }


    ValueEventListener readMessageStartListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()) {
                User user = UserGlobal.getUser(ChatActivityCS.this);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("chatall/chatCS").child(id).child("unread").setValue("false");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    private void readMessageEnd() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        User user = UserGlobal.getUser(this);
               mDatabase.child("chatall/bodychatCS/"+id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mDatabase.child("chatall/chatCS").child(id).child("unread").removeEventListener(readMessageStartListener);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });












    }


    private void sendMessage() {
        String status = UserGlobal.getUser(this).status;

            String messageText = textMessage.getText().toString();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            User user = UserGlobal.getUser(this);
            String idx = user.id;

            DateFormat df = new SimpleDateFormat("d MMM yy h:mm a");
            String data = df.format(new Date());

            Message message = new Message();
            message.setId(idx);
            message.setMessage(messageText);
            message.setRead("false");
            message.setImage(user.image);
        message.setDate((long)0);

           String[] idid=id.split("[|]");

        String[] namename=name.split("[|]");
        String[] imageimage=image.split("[|]");

            String idFirebase = mDatabase.child("chatall/bodychat").child(idid[0]).child(idid[1]).push().getKey();
            mDatabase.child("chatall/bodychat").child(idid[0]).child(idid[1]).child(idFirebase).setValue(message);
            mDatabase.child("chatall/bodychat").child(idid[0]).child(idid[1]).child(idFirebase).child("date").setValue(ServerValue.TIMESTAMP);

            String idFirebase2 = mDatabase.child("bodychat").child(idid[1]).child(idid[0]).push().getKey();
            mDatabase.child("chatall/bodychat").child(idid[1]).child(idid[0]).child(idFirebase2).setValue(message);
            mDatabase.child("chatall/bodychat").child(idid[1]).child(idid[0]).child(idFirebase2).child("date").setValue(ServerValue.TIMESTAMP);


            String idFirebase3 = mDatabase.child("bodychatCS").child(id).push().getKey();
            mDatabase.child("chatall/bodychatCS").child(id).child(idFirebase3).setValue(message);
            mDatabase.child("chatall/bodychatCS").child(id).child(idFirebase3).child("date").setValue(ServerValue.TIMESTAMP);



            Chat chat1 = new Chat();
            chat1.setImage(imageimage[1]);
            chat1.setUid(idid[1]);
            chat1.setDate((long) 0);
            chat1.setLastChat(message.getMessage());
            chat1.setUnread("true");
            chat1.setName(namename[1]);

             mDatabase.child("chatall/chat").child(idid[0]).child(idid[1]).setValue(chat1);
             mDatabase.child("chatall/chat").child(idid[0]).child(idid[1]).child("date").setValue(ServerValue.TIMESTAMP);

            Chat chat2 = new Chat();
            chat2.setImage(imageimage[0]);
            chat2.setUid(idid[0]);
            chat2.setDate((long) 0);
            chat2.setLastChat(message.getMessage());
            chat2.setUnread("true");
            chat2.setName(namename[0]);

            mDatabase.child("chatall/chat").child(idid[1]).child(idid[0]).setValue(chat2);
            mDatabase.child("chatall/chat").child(idid[1]).child(idid[0]).child("date").setValue(ServerValue.TIMESTAMP);


               Chat chat3 = new Chat();
                chat3.setImage(image);
                chat3.setUid(id);
                chat3.setDate((long) 0);
                chat3.setLastChat(message.getMessage());
                chat3.setUnread("false");
                chat3.setName(name);
                mDatabase.child("chatall/chatCS").child(id).setValue(chat3);
                mDatabase.child("chatall/chatCS").child(id).child("date").setValue(ServerValue.TIMESTAMP);



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
        fetchItem();
        readMessageStart();


    }

    @Override
    protected void onPause() {
        super.onPause();
        readMessageEnd();
        checkReadEnd();
    }

    private void fetch()
    {
        populate();
        //implementScrollListener();
    }


    private void fetchItem()
    {
        User user = UserGlobal.getUser(this);
        String val1="";
        if(user.status.contentEquals("vendor")) {
            val1 = user.id + "|" + id;
        }
        else if(user.status.contentEquals("customer"))
        {
            val1 = id + "|" + user.id;
        }
        final String val=val1;

            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("chatall/bodychatCS/" +id);
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
                    Toast.makeText(ChatActivityCS.this, "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            });
        }
}