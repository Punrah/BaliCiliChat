package com.example.puniaraharja.balicilichat.adapter;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.request.target.Target;
import com.example.puniaraharja.balicilichat.ChatActivity;
import com.example.puniaraharja.balicilichat.ChatActivityCCS;
import com.example.puniaraharja.balicilichat.ImageActivity;
import com.example.puniaraharja.balicilichat.LoginActivity;
import com.example.puniaraharja.balicilichat.R;
import com.example.puniaraharja.balicilichat.helper.UserGlobal;
import com.example.puniaraharja.balicilichat.persistence.Chat;
import com.example.puniaraharja.balicilichat.persistence.Message;
import com.example.puniaraharja.balicilichat.persistence.User;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Message> itemList;
    private Context context;
    private User user;

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    int selected;

    Long now;
    int highlightPosition = -1;


    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public TextView message;
        public TextView time;
        public ImageView senderImage;
        public ImageView myImage;
        public ImageView senderBubble;
        public ImageView myBubble;
        private LinearLayout messageInflater;




        public MyViewHolder(View view) {
            super(view);
            messageInflater = (LinearLayout) view.findViewById(R.id.message_inflater);
        }


    }




    FirebaseStorage storage = FirebaseStorage.getInstance();


    private int position=0;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public MessageAdapter(Context context, List<Message> moviesList) {
        this.itemList = moviesList;
        this.context=context;
        this.user = UserGlobal.getUser(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_message_inflater, parent, false);

        return new MyViewHolder(itemView);
    }

   // @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        Message item = itemList.get(position);
//        holder.setIsRecyclable(false);
//        holder.message.setText(item.getMessage());
//        holder.time.setText(toDuration(item.getDate()));
//
//        if(user.id.contentEquals(item.getId())) {
//
//           RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.bubble.getLayoutParams();
//            params.addRule(RelativeLayout.LEFT_OF, R.id.my_bubble);
//            params.removeRule(RelativeLayout.RIGHT_OF);
//            holder.bubble.setLayoutParams(params);
//
//
//            StorageReference gsReference = storage.getReferenceFromUrl("gs://balicilichat.appspot.com/" + item.getImage());
//            Glide.with(context)
//                    .using(new FirebaseImageLoader())
//                    .load(gsReference)
//                    .into(holder.myImage);
//
//            holder.senderImage.setVisibility(View.INVISIBLE);
//            holder.senderBubble.setVisibility(View.INVISIBLE);
//            holder.message.setTextColor(ContextCompat.getColor(context,R.color.white));
//
//            LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) holder.message.getLayoutParams();
//            params3.gravity = Gravity.END;
//            holder.message.setLayoutParams(params3);
//
//            holder.time.setTextColor(ContextCompat.getColor(context,R.color.white));
//            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.time.getLayoutParams();
//            params2.gravity = Gravity.END;
//            holder.time.setLayoutParams(params2);
//
//        }
//        else
//        {
//
//            int pL = holder.bubble.getPaddingLeft();
//            int pT = holder.bubble.getPaddingTop();
//            int pR = holder.bubble.getPaddingRight();
//            int pB = holder.bubble.getPaddingBottom();
//
//            holder.bubble.setBackgroundResource(R.drawable.rounded_corner1);
//            holder.bubble.setPadding(pL, pT, pR, pB);
//
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.bubble.getLayoutParams();
//            params.addRule(RelativeLayout.RIGHT_OF, R.id.sender_bubble);
//            params.removeRule(RelativeLayout.LEFT_OF);
//
//
//            holder.bubble.setLayoutParams(params);
//
//           StorageReference gsReference = storage.getReferenceFromUrl("gs://balicilichat.appspot.com/"+ item.getImage());
//            Glide.with(context)
//                    .using(new FirebaseImageLoader())
//                    .load(gsReference)
//                    .into(holder.senderImage);
//
//            holder.myImage.setVisibility(View.INVISIBLE);
//            holder.myBubble.setVisibility(View.INVISIBLE);
//
//        }
//            }


            Handler handler = new Handler();
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Message item = itemList.get(position);
        holder.setIsRecyclable(false);
        holder.itemView.setLongClickable(true);
        final int pos1=position;
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(pos1);
                return false;
            }
        });


        if(position == highlightPosition ){

            final ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(holder.itemView,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    Color.GRAY,
                    Color.TRANSPARENT
                   );
            backgroundColorAnimator.setDuration(3000);
            backgroundColorAnimator.start();

            highlightPosition=-1;
//
            // You can change the background here
        }else{
            // Set default background
        }

        LayoutInflater myInflater = LayoutInflater.from(context);
        View listMessage = null;
            if(item.getImageUpload().contentEquals("")&&item.getMessageObj()==null&&item.getLink().contentEquals(""))
            {
                if(user.id.contentEquals(item.getId())) {
                    listMessage = myInflater.inflate(R.layout.my_message, null);
                }
                else
                {
                    listMessage = myInflater.inflate(R.layout.sender_message, null);
                    TextView name = (TextView) listMessage.findViewById(R.id.name);
                    name.setText(item.getName());
                    CircleImageView circleImageView=(CircleImageView) listMessage.findViewById(R.id.my_image);
                    StorageReference gsReference = storage.getReference().child(item.getImage());
                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(gsReference)
                            .into(circleImageView);
                }

                TextView textViewMessage =(TextView) listMessage.findViewById(R.id.message);
                TextView textViewDate = (TextView) listMessage.findViewById(R.id.time);
                textViewMessage.setText(item.getMessage());
                textViewDate.setText(toDuration(item.getDate()));


            }
            else if(item.getImageUpload().contentEquals("")&&item.getMessageObj()!=null&&item.getLink().contentEquals(""))
            {
                if(user.id.contentEquals(item.getId())) {
                    listMessage = myInflater.inflate(R.layout.my_message_reply, null);
                }
                else
                {
                    listMessage = myInflater.inflate(R.layout.sender_message_reply, null);
                    TextView name = (TextView) listMessage.findViewById(R.id.name);
                    name.setText(item.getName());
                    CircleImageView circleImageView=(CircleImageView) listMessage.findViewById(R.id.my_image);
                    StorageReference gsReference = storage.getReference().child(item.getImage());
                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(gsReference)
                            .into(circleImageView);

                }
                LinearLayout reply = (LinearLayout) listMessage.findViewById(R.id.reply);

                TextView textViewMessage =(TextView) listMessage.findViewById(R.id.message);
                TextView textViewDate = (TextView) listMessage.findViewById(R.id.time);
                TextView nameReply = (TextView) listMessage.findViewById(R.id.reply_name);
                TextView messageReply = (TextView) listMessage.findViewById(R.id.reply_message);
                textViewMessage.setText(item.getMessage());
                textViewDate.setText(toDuration(item.getDate()));
                nameReply.setText(item.getMessageObj().getName());
                messageReply.setText(item.getMessageObj().getMessage());
                final int pos2=item.getPosition();
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(user.status.contentEquals("cs"))
                        {
                            ChatActivityCCS.recyclerView.scrollToPosition(pos2);
                            ChatActivityCCS.mAdapter.updateHighlightPosition(pos2);
                        }
                        else
                        {

                            ChatActivity.recyclerView.scrollToPosition(pos2);
                            ChatActivity.mAdapter.updateHighlightPosition(pos2);
                        }

                    }
                });

            }
            else if(item.getImageUpload().contentEquals("")&&item.getMessageObj()==null&&!item.getLink().contentEquals(""))
            {
                if(user.id.contentEquals(item.getId())) {
                    listMessage = myInflater.inflate(R.layout.my_message_link, null);
                }
                else
                {
                    //listMessage = myInflater.inflate(R.layout.sender_message_link, null);
                    listMessage = myInflater.inflate(R.layout.my_message_link, null);
                    TextView name = (TextView) listMessage.findViewById(R.id.name);
                    name.setText(item.getName());
                    CircleImageView circleImageView=(CircleImageView) listMessage.findViewById(R.id.my_image);
                    StorageReference gsReference = storage.getReference().child(item.getImage());
                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(gsReference)
                            .into(circleImageView);
                }

                TextView link =(TextView) listMessage.findViewById(R.id.link);
                TextView textViewMessage =(TextView) listMessage.findViewById(R.id.message);
                TextView textViewDate = (TextView) listMessage.findViewById(R.id.time);
                link.setText(item.getLink());

                textViewMessage.setText(item.getMessage());
                textViewDate.setText(toDuration(item.getDate()));

                final String linkString = item.getLink();
                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWebPage(linkString);
                    }
                });

            }
            else if(!item.getImageUpload().contentEquals("")&&item.getMessageObj()==null&&item.getLink().contentEquals(""))
            {
                if(user.id.contentEquals(item.getId())) {
                    listMessage = myInflater.inflate(R.layout.my_message_image, null);
                }
                else
                {
                    listMessage = myInflater.inflate(R.layout.sender_message_image, null);
                    TextView name = (TextView) listMessage.findViewById(R.id.name);
                    name.setText(item.getName());
                    CircleImageView circleImageView=(CircleImageView) listMessage.findViewById(R.id.my_image);
                    StorageReference gsReference = storage.getReference().child(item.getImage());
                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(gsReference)
                            .into(circleImageView);

                }

                ImageView imageMessage =(ImageView) listMessage.findViewById(R.id.image_message);
                TextView textViewMessage =(TextView) listMessage.findViewById(R.id.message);
                TextView textViewDate = (TextView) listMessage.findViewById(R.id.time);

                textViewMessage.setText(item.getMessage());
                textViewDate.setText(toDuration(item.getDate()));



                StorageReference gsReference2 = storage.getReference().child("imageSmall").child(item.getImageUpload());
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(gsReference2)
                        .into(imageMessage);

                final String imageUpload=item.getImageUpload();
                imageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ImageActivity.class);
                        i.putExtra("imageUpload",imageUpload);
                        context.startActivity(i);
                    }
                });



            }
            else
            {
                if(user.id.contentEquals(item.getId())) {
                    listMessage = myInflater.inflate(R.layout.list_my_message, null);
                }
                else
                {
                    listMessage = myInflater.inflate(R.layout.list_sender_message, null);
                }
                listMessage =myInflater.inflate(R.layout.list_my_message, null);
                TextView textView=(TextView) listMessage.findViewById(R.id.my_message);
                Gson gson = new Gson();
                String text = gson.toJson(item);
                textView.setText(text);
            }

        holder.messageInflater.addView(listMessage);


    }

    public void openWebPage(String url) {

        Uri webpage = Uri.parse(url);

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            webpage = Uri.parse("http://" + url);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



    public String toDuration(long time) {


        return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();

    }

    public void  updateHighlightPosition(int position){
        highlightPosition = position;
        notifyDataSetChanged();
    }






}

