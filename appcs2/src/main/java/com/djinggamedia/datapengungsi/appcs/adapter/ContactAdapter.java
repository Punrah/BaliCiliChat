package com.djinggamedia.datapengungsi.appcs.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djinggamedia.datapengungsi.appcs.ChatActivity;
import com.djinggamedia.datapengungsi.appcs.R;
import com.djinggamedia.datapengungsi.appcs.persistence.Contact;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private List<Contact> itemList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
        public TextView role;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_name);
            image = (ImageView) view.findViewById(R.id.img_item);
            role = (TextView) view.findViewById(R.id.role);
        }
    }
    FirebaseStorage storage = FirebaseStorage.getInstance();


    public ContactAdapter(Context context, List<Contact> moviesList) {
        this.itemList = moviesList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_contact, parent, false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Contact item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.role.setText(item.getRole());

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
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}