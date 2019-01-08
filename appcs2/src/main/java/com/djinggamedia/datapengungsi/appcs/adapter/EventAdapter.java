package com.djinggamedia.datapengungsi.appcs.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.djinggamedia.datapengungsi.appcs.AsyncTask.ImageAsyncTask;
import com.djinggamedia.datapengungsi.appcs.R;
import com.djinggamedia.datapengungsi.appcs.helper.UserGlobal;
import com.djinggamedia.datapengungsi.appcs.persistence.Event;
import com.djinggamedia.datapengungsi.appcs.persistence.User;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<Event> itemList;
    private Context context;
    private User user;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView judul;
        public TextView tanggal;
        public TextView waktu;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            judul = (TextView) view.findViewById(R.id.judul);
            tanggal = (TextView) view.findViewById(R.id.tanggal);
            waktu = (TextView) view.findViewById(R.id.waktu);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
    FirebaseStorage storage = FirebaseStorage.getInstance();


    public EventAdapter(Context context, List<Event> moviesList) {
        this.itemList = moviesList;
        this.context=context;

        this.user = UserGlobal.getUser(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_event, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Event item = itemList.get(position);
        holder.setIsRecyclable(false);
        holder.judul.setText(item.getTitle());
        holder.tanggal.setText(item.getDate());
        holder.waktu.setText(item.getStart_time()+"-"+item.getEnd_time());

        holder.image.setTag(item.getImage());
        new ImageAsyncTask().execute(holder.image);



            }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}