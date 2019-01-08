package com.djinggamedia.datapengungsi.appcs.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djinggamedia.datapengungsi.appcs.R;
import com.djinggamedia.datapengungsi.appcs.helper.UserGlobal;
import com.djinggamedia.datapengungsi.appcs.persistence.Message;
import com.djinggamedia.datapengungsi.appcs.persistence.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Message> itemList;
    private Context context;
    private User user;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public ImageView senderImage;
        public ImageView myImage;
        public ImageView senderBubble;
        public ImageView myBubble;
        public TextView read;
        private LinearLayout bubble;

        public MyViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.message);
            senderImage = (ImageView) view.findViewById(R.id.sender_image);
            senderBubble = (ImageView) view.findViewById(R.id.sender_bubble);
            myImage = (ImageView) view.findViewById(R.id.my_image);
            myBubble = (ImageView) view.findViewById(R.id.my_bubble);
           // read=(TextView) view.findViewById(R.id.read);
            bubble=(LinearLayout) view.findViewById(R.id.bubble);
        }
    }
    FirebaseStorage storage = FirebaseStorage.getInstance();


    public MessageAdapter(Context context, List<Message> moviesList) {
        this.itemList = moviesList;
        this.context=context;

        this.user = UserGlobal.getUser(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_message, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message item = itemList.get(position);
        holder.setIsRecyclable(false);
        holder.message.setText(item.getMessage());

        if(user.id.contentEquals(item.getId())) {

           RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.bubble.getLayoutParams();
            params.addRule(RelativeLayout.LEFT_OF, R.id.my_bubble);
            params.removeRule(RelativeLayout.RIGHT_OF);
            holder.bubble.setLayoutParams(params);


            StorageReference gsReference = storage.getReferenceFromUrl("gs://balicilichat.appspot.com/" + item.getImage());
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(gsReference)
                    .into(holder.myImage);

            holder.read.setVisibility(View.INVISIBLE);
            holder.senderImage.setVisibility(View.INVISIBLE);
            holder.senderBubble.setVisibility(View.INVISIBLE);
            holder.message.setTextColor(ContextCompat.getColor(context,R.color.white));

        }
        else
        {

            int pL = holder.bubble.getPaddingLeft();
            int pT = holder.bubble.getPaddingTop();
            int pR = holder.bubble.getPaddingRight();
            int pB = holder.bubble.getPaddingBottom();

            holder.bubble.setBackgroundResource(R.drawable.rounded_corner1);
            holder.bubble.setPadding(pL, pT, pR, pB);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.bubble.getLayoutParams();
            params.addRule(RelativeLayout.RIGHT_OF, R.id.sender_bubble);
            params.removeRule(RelativeLayout.LEFT_OF);


            holder.bubble.setLayoutParams(params);

           StorageReference gsReference = storage.getReferenceFromUrl("gs://balicilichat.appspot.com/"+ item.getImage());
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(gsReference)
                    .into(holder.senderImage);

            holder.read.setVisibility(View.INVISIBLE);
            holder.myImage.setVisibility(View.INVISIBLE);
            holder.myBubble.setVisibility(View.INVISIBLE);

        }
            }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}