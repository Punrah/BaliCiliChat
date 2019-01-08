package com.example.puniaraharja.balicilichat.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.puniaraharja.balicilichat.ChatActivity;
import com.example.puniaraharja.balicilichat.R;
import com.example.puniaraharja.balicilichat.persistence.Chat;
import com.example.puniaraharja.balicilichat.persistence.Contact;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<Chat> itemList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
        public TextView lastChat;
        public TextView date;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_name);
            image = (ImageView) view.findViewById(R.id.img_item);
            lastChat=(TextView) view.findViewById(R.id.last_chat);
            date = (TextView) view.findViewById(R.id.date);
        }
    }
    FirebaseStorage storage = FirebaseStorage.getInstance();


    public ChatAdapter(Context context, List<Chat> moviesList) {
        this.itemList = moviesList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_chat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Chat item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.lastChat.setText(item.getLastChat());
        holder.date.setText(toDuration(item.getDate()));

        if(item.getUnread().contentEquals("true"))
        {
            holder.lastChat.setTextColor(Color.parseColor("#ff9c3a"));
        }


        StorageReference gsReference = storage.getReferenceFromUrl("gs://balicilichat.appspot.com/"+ item.getImage());
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("name", item.getName());
                i.putExtra("id",item.getUid());
                i.putExtra("image",item.getImage());
                i.putExtra("lastChat",item.getLastChat());
                i.putExtra("date",item.getDate());
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public String toDuration(long time) {



        return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();

    }
}