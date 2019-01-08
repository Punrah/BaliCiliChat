package com.example.puniaraharja.balicilichat.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.puniaraharja.balicilichat.R;
import com.example.puniaraharja.balicilichat.helper.UserGlobal;
import com.example.puniaraharja.balicilichat.persistence.Event;
import com.example.puniaraharja.balicilichat.persistence.Product;
import com.example.puniaraharja.balicilichat.persistence.User;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private List<Product> itemList;
    private Context context;
    private User user;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView judul;
        public TextView subjudul;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            judul = (TextView) view.findViewById(R.id.judul);
            subjudul = (TextView) view.findViewById(R.id.subjudul);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
    FirebaseStorage storage = FirebaseStorage.getInstance();


    public ProductAdapter(Context context, List<Product> moviesList) {
        this.itemList = moviesList;
        this.context=context;

        this.user = UserGlobal.getUser(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product item = itemList.get(position);
        holder.setIsRecyclable(false);
        holder.judul.setText(item.getTitle());
        holder.subjudul.setText(item.getSubtitle());

        Glide.with(context)
                .load(item.getImage()) // resizes the image to 100x200 pixels but does not respect aspect ratio
                .into(holder.image);



            }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}