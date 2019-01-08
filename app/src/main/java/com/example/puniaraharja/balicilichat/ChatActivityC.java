package com.example.puniaraharja.balicilichat;

import android.content.Intent;
import android.os.Bundle;

import com.example.puniaraharja.balicilichat.helper.UserGlobal;
import com.example.puniaraharja.balicilichat.persistence.Chat;
import com.example.puniaraharja.balicilichat.persistence.Message;
import com.example.puniaraharja.balicilichat.persistence.User;
import com.firebase.client.ServerValue;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivityC extends ChatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void checkReadStart()
    {
        User user = UserGlobal.getUser(this);
        mDatabase= FirebaseDatabase.getInstance().getReference();

        String[] idid=id.split("[|]");

        mDatabase.child("chatall/chat/"+ idid[0]+"/"+idid[1]).addListenerForSingleValueEvent(readChatVendor);
        mDatabase.child("chatall/chat/"+ idid[1]+"/"+idid[0]).addListenerForSingleValueEvent(readChatCustomer);

    }

    ValueEventListener readChatVendor = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                final User user = UserGlobal.getUser(ChatActivityC.this);
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
                final User user = UserGlobal.getUser(ChatActivityC.this);
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

    public void checkReadEnd()
    {
        User user = UserGlobal.getUser(this);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        String[] idid=id.split("[|]");
        mDatabase.child("chatall/chat/"+ idid[0]+"/"+idid[1]).removeEventListener(readChatVendor);
        mDatabase.child("chatall/chat/"+ idid[1]+"/"+idid[0]).removeEventListener(readChatCustomer);
    }



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

    ValueEventListener readMessageStartListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()) {
                User user = UserGlobal.getUser(ChatActivityC.this);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("chatall/chatCS").child(id).child("unread").setValue("false");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    private void sendMessage()//over
    {
        String status = UserGlobal.getUser(this).status;

        String messageText = textMessage.getText().toString();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        User user = UserGlobal.getUser(this);
        String idx = user.id;

        DateFormat df = new SimpleDateFormat("d MMM yy h:mm a");
        String data = df.format(new Date());


        Map<String, Object> value = new HashMap<>();
        value.put("id", idx);
//            Message message = new Message();
//            message.setId(idx);

        if(!imageMessageName.contentEquals("")) {
//                message.setMessage(captions);
//                message.setImageUpload(imageMessageName+".jpg");
//                message.setMessageObj(null);
//                message.setLink("");
//                message.setPosition(-1);
            value.put("message", captions);
            value.put("imageUpload", imageMessageName+".jpg");
            value.put("messageObj", null);
            value.put("link", "");
            value.put("position", -1);
        }
        else if(!link.contentEquals(""))
        {
//                message.setMessage(captions);
//                message.setImageUpload("");
//                message.setMessageObj(null);
//                message.setLink(link);
//                message.setPosition(-1);
            value.put("message", captions);
            value.put("imageUpload", "");
            value.put("messageObj", null);
            value.put("link", link);
            value.put("position", -1);
        }
        else if(!(replyMessage==null))
        {
//                message.setMessage(captions);
//                message.setImageUpload("");
//                message.setMessageObj(replyMessage);
//                message.setLink("");
//                message.setPosition(replyPosition);
            value.put("message", captions);
            value.put("imageUpload", "");
            value.put("messageObj", replyMessage);
            value.put("link", "");
            value.put("position", replyPosition);
        }
        else
        {
//                message.setMessage(messageText);
//                message.setImageUpload("");
//                message.setMessageObj(null);
//                message.setLink("");
//                message.setPosition(-1);
            value.put("message", messageText);
            value.put("imageUpload", "");
            value.put("messageObj", null);
            value.put("link", "");
            value.put("position", -1);

        }

//            message.setName(user.name);
//            message.setRead("false");
//            message.setImage(user.image);
//            message.setDate(System.currentTimeMillis());
        value.put("name", user.name);
        value.put("read", "false");
        value.put("image", user.image);
        value.put("date", ServerValue.TIMESTAMP);




        String[] idid=id.split("[|]");

        String[] namename=name.split("[|]");
        String[] imageimage=image.split("[|]");

        String idFirebase = mDatabase.child("chatall/bodychat").child(idid[0]).child(idid[1]).push().getKey();
        mDatabase.child("chatall/bodychat").child(idid[0]).child(idid[1]).child(idFirebase).setValue(value);


        String idFirebase2 = mDatabase.child("bodychat").child(idid[1]).child(idid[0]).push().getKey();
        mDatabase.child("chatall/bodychat").child(idid[1]).child(idid[0]).child(idFirebase2).setValue(value);



        String idFirebase3 = mDatabase.child("bodychatCS").child(id).push().getKey();
        mDatabase.child("chatall/bodychatCS").child(id).child(idFirebase3).setValue(value);


        Chat chat1 = new Chat();
        chat1.setImage(imageimage[1]);
        chat1.setUid(idid[1]);
        chat1.setDate((long) 0);
        chat1.setLastChat(value.get("message").toString());
        chat1.setUnread("true");
        chat1.setName(namename[1]);

        mDatabase.child("chatall/chat").child(idid[0]).child(idid[1]).setValue(chat1);
        mDatabase.child("chatall/chat").child(idid[0]).child(idid[1]).child("date").setValue(ServerValue.TIMESTAMP);

        Chat chat2 = new Chat();
        chat2.setImage(imageimage[0]);
        chat2.setUid(idid[0]);
        chat2.setDate((long) 0);
        chat2.setLastChat(value.get("message").toString());
        chat2.setUnread("true");
        chat2.setName(namename[0]);

        mDatabase.child("chatall/chat").child(idid[1]).child(idid[0]).setValue(chat2);
        mDatabase.child("chatall/chat").child(idid[1]).child(idid[0]).child("date").setValue(ServerValue.TIMESTAMP);


        Chat chat3 = new Chat();
        chat3.setImage(image);
        chat3.setUid(id);
        chat3.setDate((long) 0);
        chat3.setLastChat(value.get("message").toString());
        chat3.setUnread("false");
        chat3.setName(name);
        mDatabase.child("chatall/chatCS").child(id).setValue(chat3);
        mDatabase.child("chatall/chatCS").child(id).child("date").setValue(ServerValue.TIMESTAMP);



        textMessage.setText("");

    }

    private void fetchItem()//over
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
                .child("chatall/bodychatCS/"+id);

//            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(!dataSnapshot.hasChildren())
//                    {
//                        Toast.makeText(ChatActivity.this, "No more chats", Toast.LENGTH_SHORT).show();
//                        currentPage--;
//                    }
//                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
//                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
//                        while (iterator.hasNext()) {
//                            Message value = iterator.next().getValue(Message.class);
//
//                            listItem.add(value);
//
//                            pesan = value.getMessage();
//                        }
//                        mAdapter.notifyDataSetChanged();
//                        recyclerView.scrollToPosition(mAdapter.getItemCount()-1);
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//// Getting Post failed, log a message
//                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                    // [START_EXCLUDE]
//                    Toast.makeText(ChatActivity.this, "Failed to load post.",
//                            Toast.LENGTH_SHORT).show();
//                    // [END_EXCLUDE]
//                }
//            });


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message value = dataSnapshot.getValue(Message.class);

                listItem.add(value);

                pesan = value.getMessage();
                mAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(mAdapter.getItemCount()-1);
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
}
