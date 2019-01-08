/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.djinggamedia.datapengungsi.appcs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.djinggamedia.datapengungsi.appcs.AsyncTask.MyAsyncTask;
import com.djinggamedia.datapengungsi.appcs.app.AppConfig;
import com.djinggamedia.datapengungsi.appcs.app.Formater;
import com.djinggamedia.datapengungsi.appcs.helper.SessionManager;
import com.djinggamedia.datapengungsi.appcs.helper.UserSQLiteHandler;
import com.djinggamedia.datapengungsi.appcs.persistence.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private TextView btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private UserSQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.button_login);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new UserSQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if(email.isEmpty())
                {
                    Formater.viewDialog(LoginActivity.this,getString(R.string.empty_email_pop_up));
                }
                else if(password.isEmpty())
                {
                    Formater.viewDialog(LoginActivity.this,getString(R.string.empty_password_pop_up));
                }
                else
                {
                    // login user
                    new checkLogin(email, password).execute();
                }
            }

        });

    }



    private class checkLogin extends MyAsyncTask {

        String password;
        String email;
        User user = new User();

        public checkLogin(String email,String password)
        {
            this.email=email;
            this.password=password;
        }




        @Override
        public Context getContext () {
            return LoginActivity.this;
        }

        @Override
        protected Void doInBackground (Void...params){
            postData();
            return super.doInBackground(params);
        }

        @Override
        public void setSuccessPostExecute() {
            // user successfully logged in
            // Create login session
            session.setLogin(true);

            db.addUser(user);

            Intent intent = new Intent(LoginActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void setFailPostExecute() {

        }

        public void postData() {
            String url = AppConfig.URL_LOGIN;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", password));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        status = obj.getString("status");

                        if (status.contentEquals("1")) {
                            isSucces = true;
                            user.image = obj.getString("name")+".jpg";
                            user.name = obj.getString("name");
                            user.email = obj.getString("email");
                            user.id=obj.getString("id_user");
                            user.status=obj.getString("role");
                        }
                        else if (status.contentEquals("2"))
                        {
                            badServerAlert();

                        }
                        else {
                            msg = getString(R.string.wrong_password_pop_up);
                            msgTitle = getString(R.string.wrong_password_pop_up_title);
                            alertType=DIALOG_TITLE;

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

    }


}
