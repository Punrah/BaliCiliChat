package com.example.puniaraharja.balicilichat;

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

import com.example.puniaraharja.balicilichat.AsyncTask.MyAsyncTask;
import com.example.puniaraharja.balicilichat.adapter.EventAdapter;
import com.example.puniaraharja.balicilichat.adapter.ProductAdapter;
import com.example.puniaraharja.balicilichat.app.AppConfig;
import com.example.puniaraharja.balicilichat.app.HttpHandler;
import com.example.puniaraharja.balicilichat.persistence.Event;
import com.example.puniaraharja.balicilichat.persistence.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = ProductFragment.class.getSimpleName();

    public ProductAdapter adapter;
    public List<Product> orderList;
    private RecyclerView recyclerView;
    private static LinearLayoutManager mLayoutManager;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myInflater =inflater.inflate(R.layout.fragment_event, container, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) myInflater.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        swipeRefreshLayout = (SwipeRefreshLayout) myInflater.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        fetchOrder();
                                    }
                                }
        );
                return myInflater;


    }

    @Override
    public void onResume() {
        super.onResume();


    }



    public void fetchOrder() {
        new fetchOrder().execute();
    }

    @Override
    public void onRefresh() {
        fetchOrder();
    }


    private class fetchOrder extends MyAsyncTask {
        JSONArray response;



        @Override
        public Context getContext() {
            return getActivity();
        }

        @Override
        public void setSuccessPostExecute() {
            adapter = new ProductAdapter(getActivity(),orderList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);


        }

        @Override
        public void setFailPostExecute() {
            Toast.makeText(getActivity(), "fail execute post", Toast.LENGTH_SHORT).show();
                    }

        @Override
        public void postData() {
            String url = AppConfig.getProductUrl();
            HttpHandler sh = new HttpHandler();


            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCall(url);
                if (jsonStr != null) {
                    try {
                        response = new JSONArray(jsonStr);
                        if (response.length() > 0) {
                            isSucces=true;
                            orderList =new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    // Getting JSON Array node
                                    JSONObject c = response.getJSONObject(i);

                                    Product order = new Product();
                                    order.id_product = c.getString("id_product");
                                    order.title = c.getString("title");
                                    order.image = c.getString("foto");
                                    order.subtitle = c.getString("sub_title");
                                    order.code = c.getString("code");
                                    order.type = c.getString("type");

                                    orderList.add(0, order);
                                } catch (JSONException e) {
                                    badServerAlert();
                                }
                            }
                        }
                        else
                        {
                            msgTitle="Error";
                            msg="Event Kosong";
                        }

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
        }

        @Override
        public void setPostLoading() {


        }
    }






}
