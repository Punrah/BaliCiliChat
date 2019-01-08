package com.djinggamedia.datapengungsi.appcs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.djinggamedia.datapengungsi.appcs.AsyncTask.MyAsyncTask;
import com.djinggamedia.datapengungsi.appcs.adapter.EventAdapter;
import com.djinggamedia.datapengungsi.appcs.app.AppConfig;
import com.djinggamedia.datapengungsi.appcs.app.HttpHandler;
import com.djinggamedia.datapengungsi.appcs.persistence.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    public EventAdapter adapter;
    public List<Event> orderList;
    private RecyclerView recyclerView;
    private static LinearLayoutManager mLayoutManager;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myInflater =inflater.inflate(R.layout.fragment_event, container, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) myInflater.findViewById(R.id.recycler_view);
        init(myInflater);
        swipeRefreshLayout = (SwipeRefreshLayout) myInflater.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchOrder();
            }
        });
        //fetchOrder();

        return myInflater;


    }



    @Override
    public void onResume() {
        super.onResume();
        orderList = new ArrayList<>();

    }

    private void init(View myInflater)
    {
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

    }
    private void populate()
    {
        adapter = new EventAdapter(getActivity(),orderList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void fetch()
    {
        populate();
        //implementScrollListener();
    }


    public void fetchOrder() {
        new fetchOrder().execute();
    }


    private class fetchOrder extends MyAsyncTask {
        JSONArray response;


        @Override
        public Context getContext() {
            return getActivity();
        }

        @Override
        public void setSuccessPostExecute() {
            if (response.length() > 0) {

                for (int i = 0; i < response.length(); i++) {
                    try {

                        // Getting JSON Array node
                        JSONObject c = response.getJSONObject(i);

                        Event order = new Event();
                        order.id_event = c.getString("id_event");
                        order.title = c.getString("title");
                        order.image = c.getString("image");
                        order.date = c.getString("date");
                        order.start_time = c.getString("start_time");
                        order.end_time = c.getString("end_time");
                        order.location = c.getString("location");
                        order.address = c.getString("address");
                        order.description=c.getString("description");

                        orderList.add(0, order);
                    } catch (JSONException e) {
                        badServerAlert();
                    }
                }
                fetch();
            }
            else
            {

            }

        }

        @Override
        public void setFailPostExecute() {
            Toast.makeText(getActivity(), "fail execute post", Toast.LENGTH_SHORT).show();
                    }

        @Override
        public void postData() {
            String url = AppConfig.getEventUrl();
            HttpHandler sh = new HttpHandler();


            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCall(url);
                if (jsonStr != null) {
                    try {
                        response = new JSONArray(jsonStr);
                        isSucces=true;

                    } catch (final JSONException e) {
                        badServerAlert();
                    }
                } else {
                    badServerAlert();
                }
            } catch (IOException e) {
                badInternetAlert();
            }

        }

        @Override
        public void setPreloading() {
            orderList = new ArrayList<>();
            populate();

            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void setPostLoading() {
            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);


        }
    }






}
