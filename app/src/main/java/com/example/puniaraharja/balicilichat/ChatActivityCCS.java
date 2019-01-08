package com.example.puniaraharja.balicilichat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.puniaraharja.balicilichat.adapter.MessageAdapter;
import com.example.puniaraharja.balicilichat.helper.UserGlobal;
import com.example.puniaraharja.balicilichat.persistence.Chat;
import com.example.puniaraharja.balicilichat.persistence.Message;
import com.example.puniaraharja.balicilichat.persistence.User;
import com.firebase.client.ServerValue;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivityCCS extends AppCompatActivity {
    private static final int TOTAL_ITEM_EACH_LOAD = 10;
    private int currentPage = 0;
    private static int RESULT_LOAD_IMG = 1;
    TextView textViewName;
    String name;
    String image;
    String tanggal="";
    String lastChat;
    ImageView back;
    ImageView delete;
    public String id;
    TextView csRead;
    TextView vendorRead;
    private List<String> listTemplate;
    FirebaseStorage storage;

    ImageView plus;

    public static MessageAdapter mAdapter;
    private static LinearLayoutManager mLayoutManager;

    public List<Message> listItem ;
    public DatabaseReference mDatabase;

    EditText textMessage;
    ImageView send;
    String pesan="";

    Bitmap imageMessage;
    String imageMessageName="";
    String captions="";
    String link="";
    Message replyMessage=null;
    int replyPosition=-1;


    public static RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        textViewName=(TextView) findViewById(R.id.name);



        textMessage=(EditText) findViewById(R.id.input_message);
        send=(ImageView) findViewById(R.id.send);

        plus=(ImageView) findViewById(R.id.plus);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populatePlus();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivityCCS.this);
                builder.setMessage("Hapus Chat?").setPositiveButton("Ya", hapusChatClickListener)
                        .setNegativeButton("Tidak", hapusChatClickListener).show();
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
                if(!textMessage.getText().equals(""))
                sendMessage();
            }
        });

        checkReadStart();
        populate();
        fetchItem();
    }



    private void populatePlus()
    {
        final String [] stockArr = {"Pick Template", "Send Image","Send Link"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivityCCS.this);
        builder.setItems(stockArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0) {
                    fetchTemplate();
                }
                else if(which==1) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                }
                else if(which==2)
                {
                    populateLinkDialog();

                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            populateImageDialog(data);
        }
    }

    public void uploadImageFirebase(final Bitmap bitmap, final String captions2)
    {
        Bitmap bitmap1=bitmap;
         final ProgressDialog asyncDialog =new ProgressDialog(this);
            asyncDialog.setMessage("Please wait...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
         imageMessageName = mDatabase.child("chatall/imagename").push().getKey();


        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("image/"+imageMessageName+".jpg");

// Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("image/images/"+imageMessageName+".jpg");

// While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Toast.makeText(ChatActivityCCS.this, "fail", Toast.LENGTH_SHORT).show();
                asyncDialog.dismiss();

                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Create a storage reference from our app
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a reference to "mountains.jpg"
                StorageReference mountainsRef = storageRef.child("imageSmall/"+imageMessageName+".jpg");

// Create a reference to 'images/mountains.jpg'
                StorageReference mountainImagesRef = storageRef.child("imageSmall/images/"+imageMessageName+".jpg");

// While the file names are the same, the references point to different files
                mountainsRef.getName().equals(mountainImagesRef.getName());    // true
                mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Toast.makeText(ChatActivityCCS.this, "fail", Toast.LENGTH_SHORT).show();
                        asyncDialog.dismiss();

                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        asyncDialog.dismiss();
                        captions=captions2;
                        sendMessage();
                        captions="";
                        imageMessageName="";

                    }
                });

            }
        });
    }


    private void populateImageDialog(Intent data)
    {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        final Bitmap bitmap =BitmapFactory.decodeFile(picturePath);
        ImageView image = new ImageView(this);
        RelativeLayout layout;
        LayoutInflater myInflater = LayoutInflater.from(this);
        layout = (RelativeLayout) myInflater.inflate(R.layout.image_layout,null);
        ImageView imageDialog = (ImageView) layout.findViewById(R.id.image);
        final EditText editTextDialog = (EditText) layout.findViewById(R.id.captions);
        CircleImageView sendButton = (CircleImageView) layout.findViewById(R.id.send);

        imageDialog.setImageBitmap(bitmap);
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen).setCancelable(true).
                        setView(layout);

        final AlertDialog alertDialog = builder.create();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageFirebase(bitmap,editTextDialog.getText().toString());
                alertDialog.dismiss();

            }
        });


        alertDialog.show();
    }

    private void populateLinkDialog()
    {

        LinearLayout layout;
        LayoutInflater myInflater = LayoutInflater.from(this);
        layout = (LinearLayout) myInflater.inflate(R.layout.link_layout,null);
        final EditText editTextLink = (EditText) layout.findViewById(R.id.link);
        final EditText editTextCaptions = (EditText) layout.findViewById(R.id.captions);
        CircleImageView sendButton = (CircleImageView) layout.findViewById(R.id.send);

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this).setCancelable(true).
                        setView(layout);

        final AlertDialog alertDialog = builder.create();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link=editTextLink.getText().toString();
                captions=editTextCaptions.getText().toString();
                sendMessage();
                link="";
                captions="";
                alertDialog.dismiss();

            }
        });


        alertDialog.show();
    }
    private void populateTemplate()
    {
        final String [] stockArr = listTemplate.toArray(new String[listTemplate.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivityCCS.this);
        builder.setTitle("Pick a template");
        builder.setItems(stockArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                textMessage.setText(stockArr[which]);
            }
        });
        builder.show();
    }

    private void fetchTemplate()
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
                populateTemplate();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
// Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(ChatActivityCCS.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
    }

    DialogInterface.OnClickListener hapusChatClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    User user = UserGlobal.getUser(ChatActivityCCS.this);
                    readMessageEnd();
                    checkReadEnd();
                    mDatabase=FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("chatall/bodychat").child(user.id).setValue(null);
                    mDatabase.child("chatall/chat").child(user.id).setValue(null);

                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:

                    //No button clicked
                    break;
            }
        }
    };









    private void populate()
    {
        listItem=new ArrayList<Message>();
        mAdapter = new MessageAdapter(this,listItem);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(null);
        registerForContextMenu(recyclerView);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.recycler_view) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = mAdapter.getPosition();
        switch (item.getItemId()) {
            case R.id.reply:
                populateReplyDialog(listItem.get(position),position);return true;
            case  R.id.copy:
                setClipboard(this,listItem.get(position).getMessage());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    private void populateReplyDialog(final Message replyMessage2, final int position)
    {
        storage = FirebaseStorage.getInstance();

        User user = UserGlobal.getUser(this);
        LinearLayout layout;
        LayoutInflater myInflater = LayoutInflater.from(this);
        View listMessage = null;
        if(replyMessage2.getImageUpload().contentEquals("")&&replyMessage2.getMessageObj()==null&&replyMessage2.getLink().contentEquals(""))
        {
            if(user.id.contentEquals(replyMessage2.getId())) {
                listMessage = myInflater.inflate(R.layout.my_message, null);
            }
            else
            {
                listMessage = myInflater.inflate(R.layout.sender_message, null);
                TextView name = (TextView) listMessage.findViewById(R.id.name);
                name.setText(replyMessage2.getName());
                CircleImageView circleImageView=(CircleImageView) listMessage.findViewById(R.id.my_image);
                StorageReference gsReference = storage.getReference().child(replyMessage2.getImage());
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(gsReference)
                        .into(circleImageView);
            }

            TextView textViewMessage =(TextView) listMessage.findViewById(R.id.message);
            TextView textViewDate = (TextView) listMessage.findViewById(R.id.time);
            textViewMessage.setText(replyMessage2.getMessage());
            textViewDate.setText(toDuration(replyMessage2.getDate()));


        }
        else if(replyMessage2.getImageUpload().contentEquals("")&&replyMessage2.getMessageObj()!=null&&replyMessage2.getLink().contentEquals(""))
        {
            if(user.id.contentEquals(replyMessage2.getId())) {
                listMessage = myInflater.inflate(R.layout.my_message_reply, null);
            }
            else
            {
                listMessage = myInflater.inflate(R.layout.sender_message_reply, null);
                TextView name = (TextView) listMessage.findViewById(R.id.name);
                name.setText(replyMessage2.getName());
                CircleImageView circleImageView=(CircleImageView) listMessage.findViewById(R.id.my_image);
                StorageReference gsReference = storage.getReference().child(replyMessage2.getImage());
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(gsReference)
                        .into(circleImageView);

            }
            LinearLayout reply = (LinearLayout) listMessage.findViewById(R.id.reply);

            TextView textViewMessage =(TextView) listMessage.findViewById(R.id.message);
            TextView textViewDate = (TextView) listMessage.findViewById(R.id.time);
            TextView nameReply = (TextView) listMessage.findViewById(R.id.reply_name);
            TextView messageReply = (TextView) listMessage.findViewById(R.id.reply_message);
            textViewMessage.setText(replyMessage2.getMessage());
            textViewDate.setText(toDuration(replyMessage2.getDate()));
            nameReply.setText(replyMessage2.getMessageObj().getName());
            messageReply.setText(replyMessage2.getMessageObj().getMessage());


        }
        else if(replyMessage2.getImageUpload().contentEquals("")&&replyMessage2.getMessageObj()==null&&!replyMessage2.getLink().contentEquals(""))
        {
            if(user.id.contentEquals(replyMessage2.getId())) {
                listMessage = myInflater.inflate(R.layout.my_message_link, null);
            }
            else
            {
                //listMessage = myInflater.inflate(R.layout.sender_message_link, null);
                listMessage = myInflater.inflate(R.layout.my_message_link, null);
                TextView name = (TextView) listMessage.findViewById(R.id.name);
                name.setText(replyMessage2.getName());
                CircleImageView circleImageView=(CircleImageView) listMessage.findViewById(R.id.my_image);
                StorageReference gsReference = storage.getReference().child(replyMessage2.getImage());
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(gsReference)
                        .into(circleImageView);
            }

            TextView link =(TextView) listMessage.findViewById(R.id.link);
            TextView textViewMessage =(TextView) listMessage.findViewById(R.id.message);
            TextView textViewDate = (TextView) listMessage.findViewById(R.id.time);
            link.setText(replyMessage2.getLink());

            textViewMessage.setText(replyMessage2.getMessage());
            textViewDate.setText(toDuration(replyMessage2.getDate()));


        }
        else if(!replyMessage2.getImageUpload().contentEquals("")&&replyMessage2.getMessageObj()==null&&replyMessage2.getLink().contentEquals(""))
        {
            if(user.id.contentEquals(replyMessage2.getId())) {
                listMessage = myInflater.inflate(R.layout.my_message_image, null);
            }
            else
            {
                listMessage = myInflater.inflate(R.layout.sender_message_image, null);
                TextView name = (TextView) listMessage.findViewById(R.id.name);
                name.setText(replyMessage2.getName());
                CircleImageView circleImageView=(CircleImageView) listMessage.findViewById(R.id.my_image);
                StorageReference gsReference = storage.getReference().child(replyMessage2.getImage());
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(gsReference)
                        .into(circleImageView);

            }

            ImageView imageMessage =(ImageView) listMessage.findViewById(R.id.image_message);
            TextView textViewMessage =(TextView) listMessage.findViewById(R.id.message);
            TextView textViewDate = (TextView) listMessage.findViewById(R.id.time);

            textViewMessage.setText(replyMessage2.getMessage());
            textViewDate.setText(toDuration(replyMessage2.getDate()));



            StorageReference gsReference2 = storage.getReference().child("imageSmall").child(replyMessage2.getImageUpload());
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(gsReference2)
                    .into(imageMessage);





        }
        else
        {
            if(user.id.contentEquals(replyMessage2.getId())) {
                listMessage = myInflater.inflate(R.layout.list_my_message, null);
            }
            else
            {
                listMessage = myInflater.inflate(R.layout.list_sender_message, null);
            }
            listMessage =myInflater.inflate(R.layout.list_my_message, null);
            TextView textView=(TextView) listMessage.findViewById(R.id.my_message);
            Gson gson = new Gson();
            String text = gson.toJson(replyMessage2);
            textView.setText(text);
        }

        layout = (LinearLayout) myInflater.inflate(R.layout.reply_layout,null);
        final LinearLayout textViewReply = (LinearLayout) layout.findViewById(R.id.reply);
        textViewReply.addView(listMessage);

        final EditText editTextCaptions = (EditText) layout.findViewById(R.id.captions);
        CircleImageView sendButton = (CircleImageView) layout.findViewById(R.id.send);

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this).setCancelable(true).
                        setView(layout);

        final AlertDialog alertDialog = builder.create();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyPosition=position;
                replyMessage=replyMessage2;
                captions=editTextCaptions.getText().toString();
                sendMessage();
                replyPosition=-1;
                replyMessage=null;
                captions="";
                alertDialog.dismiss();

            }
        });


        alertDialog.show();
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        readMessageStart();


    }

    @Override
    protected void onPause() {
        super.onPause();
        readMessageEnd();
        checkReadEnd();
    }



    public String toDuration(long time) {


        return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();

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
                final User user = UserGlobal.getUser(ChatActivityCCS.this);
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
                final User user = UserGlobal.getUser(ChatActivityCCS.this);
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
         mDatabase.child("chatall/chatCS").child(id).child("unread").removeEventListener(readMessageStartListener);













    }

    ValueEventListener readMessageStartListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()) {
                User user = UserGlobal.getUser(ChatActivityCCS.this);
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