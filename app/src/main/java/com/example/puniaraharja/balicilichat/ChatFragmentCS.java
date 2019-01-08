package com.example.puniaraharja.balicilichat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.puniaraharja.balicilichat.adapter.ChatAdapter;
import com.example.puniaraharja.balicilichat.adapter.ChatAdapterCS;
import com.example.puniaraharja.balicilichat.helper.CustomComparator;
import com.example.puniaraharja.balicilichat.persistence.Chat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChatFragmentCS extends Fragment {


    private List<Chat> listItem;
    private static RelativeLayout bottomLayout;
    private RecyclerView recyclerView;
    private ChatAdapterCS mAdapter;
    private static LinearLayoutManager mLayoutManager;
    private DatabaseReference mDatabase;

    // Variables for scroll listener
    private boolean userScrolled = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    private NotificationManager mNotificationManager;
    private int notificationID = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myInflater = inflater.inflate(R.layout.fragment_chat, container, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) myInflater.findViewById(R.id.recycler_view);
        init(myInflater);
        // Inflate the layout for this fragment
        return myInflater;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchItemStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        fetchItemEnd();
    }

    private void init(View myInflater)
    {
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

    }

    private void populate()
    {
        mAdapter = new ChatAdapterCS(getActivity(),listItem);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    // Implement scroll listener
    private void implementScrollListener() {
        recyclerView
                .addOnScrollListener(new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView,
                                                     int newState) {

                        super.onScrollStateChanged(recyclerView, newState);

                        // If scroll state is touch scroll then set userScrolled
                        // true
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            userScrolled = true;

                        }

                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx,
                                           int dy) {

                        super.onScrolled(recyclerView, dx, dy);
                        // Here get the child count, item count and visibleitems
                        // from layout manager

                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisiblesItems = mLayoutManager
                                .findFirstVisibleItemPosition();

                        // Now check if userScrolled is true and also check if
                        // the item is end then update recycler view and set
                        // userScrolled to false
                        if (userScrolled
                                && (visibleItemCount + pastVisiblesItems) == totalItemCount) {
                            userScrolled = false;

                            updateRecyclerView();
                        }

                    }

                });

    }

    private void updateRecyclerView() {

        //fetchItem2();

    }

    private void fetch()
    {
        populate();
        //implementScrollListener();
    }

    private void fetchItemStart()
    {final String pesan;
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("chatall/chatCS/");
            mDatabase.addValueEventListener(fetchItemListener);
}

    private void fetchItemEnd()
    {


        final String pesan;
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("chatall/chatCS/");
        mDatabase.removeEventListener(fetchItemListener);
    }

            ValueEventListener fetchItemListener =new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listItem =new ArrayList<>();
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    while (iterator.hasNext()) {
                        Chat value = iterator.next().getValue(Chat.class);

                        listItem.add(value);
                    }
                    Collections.sort(listItem, new CustomComparator());
                    Collections.reverse(listItem);
                    fetch();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
// Getting Post failed, log a message
                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(getActivity(), "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            };


    void displayNotification(String msg) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                getActivity());
        nBuilder.setContentTitle("BaliCili");
        nBuilder.setContentText(msg);
        nBuilder.setAutoCancel(true);
        nBuilder.setSmallIcon(R.drawable.main_logo);

        Intent intent = new Intent(getActivity(), ChatActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addParentStack(ChatActivity.class);

        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);

        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, nBuilder.build());
    }






}
