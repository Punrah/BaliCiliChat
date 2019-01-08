package com.djinggamedia.datapengungsi.appcs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.djinggamedia.datapengungsi.appcs.adapter.ContactAdapter;
import com.djinggamedia.datapengungsi.appcs.helper.UserGlobal;
import com.djinggamedia.datapengungsi.appcs.persistence.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContactFragment extends Fragment {


    private List<Contact> listItem;
    private static RelativeLayout bottomLayout;
    private RecyclerView recyclerView;
    private ContactAdapter mAdapter;
    private static LinearLayoutManager mLayoutManager;
    private DatabaseReference mDatabase;

    // Variables for scroll listener
    private boolean userScrolled = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myInflater = inflater.inflate(R.layout.fragment_contact, container, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) myInflater.findViewById(R.id.recycler_view);
        init(myInflater);

        fetchItem();



        // Inflate the layout for this fragment
        return myInflater;
    }



    private void init(View myInflater)
    {
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

    }

    private void populate()
    {
        mAdapter = new ContactAdapter(getActivity(),listItem);
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

        fetchItem2();

    }

    private void fetch()
    {
        populate();
        //implementScrollListener();
    }

    private void fetchItem()
    {


            String status = UserGlobal.getUser(getActivity()).status;
            if( status.contentEquals("customer"))
            {
                mDatabase = FirebaseDatabase.getInstance().getReference()
                        .child("contactUser");
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listItem =new ArrayList<>();
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            Contact value = iterator.next().getValue(Contact.class);

                            listItem.add(value);
                        }
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
                });
            }
            else if(status.contentEquals("vendor"))
            {
                String id =UserGlobal.getUser(getActivity()).id;
                mDatabase = FirebaseDatabase.getInstance().getReference()
                        .child("contactVendorCS/"+id);
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listItem =new ArrayList<>();
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            Contact value = iterator.next().getValue(Contact.class);

                            listItem.add(value);
                        }
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
                });

            }
 }
    private void fetchItem2()
    {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            bottomLayout.setVisibility(View.VISIBLE);
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("contact");
            ValueEventListener postListener = new ValueEventListener() {


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    while (iterator.hasNext()) {
                        Contact value = iterator.next().getValue(Contact.class);

                        listItem.add(value);
                    }
                    mAdapter.notifyDataSetChanged();
                    bottomLayout.setVisibility(View.GONE);
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
            mDatabase.addListenerForSingleValueEvent(postListener);
        }
    }





}
