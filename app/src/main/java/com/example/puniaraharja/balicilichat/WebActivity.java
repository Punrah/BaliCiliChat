package com.example.puniaraharja.balicilichat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.puniaraharja.balicilichat.AsyncTask.MyAsyncTask;
import com.example.puniaraharja.balicilichat.app.AppConfig;
import com.example.puniaraharja.balicilichat.app.HttpHandler;

import java.io.IOException;


public class WebActivity extends RootActivity {

    private WebView webView;
    TextView title;
    Toolbar toolbar;
    String webAction;
    String webActionTitle;
    String web;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.webView1);
        title=(TextView) findViewById(R.id.title);
        webView.getSettings().setJavaScriptEnabled(true);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                WebActivity.super.onBackPressed();
            }
        });


        webAction=getIntent().getStringExtra("action");
        webActionTitle = getIntent().getStringExtra("title");
        title.setText(webActionTitle);


        new getWeb().execute();





    }



    private class getWeb extends MyAsyncTask {




        @Override
        public Context getContext() {
            return WebActivity.this;
        }

        @Override
        public void setSuccessPostExecute() {
            webView.loadData(web, "text/html", "UTF-8");

        }

        @Override
        public void setFailPostExecute() {

        }

        @Override
        public void postData() {
            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getWebURL(webAction);


            try {
                web = sh.makeServiceCall(url);
                isSucces=true;
            } catch (IOException e) {
                badInternetAlert();
            }
        }
    }
}
